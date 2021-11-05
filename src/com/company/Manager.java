package com.company;
import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.util.HashMap;

public class Manager implements IConfigurable {


    enum ManagerTokens{
        INPUT_FILE,
        OUTPUT_FILE,
        READER_CONFIG_FILE,
        WRITER_CONFIG_FILE,
        EXECUTOR_CONFIG_FILE
    }


    private OutputStream outputStream;
    private InputStream inputStream;
    private String readerConfig;
    private String executorConfig;
    private String writerConfig;

    private final IReader reader = new Reader();
    private final IWriter writer = new Writer();
    private final IExecutor executor = new Executor();
    private AbstractGrammar grammar = new ManagerGrammar();


    @Override
    public RC setConfig(String path) {
        Config cnfg = new Config(grammar);
        RC err = cnfg.ParseConfig(path);
        if (err != RC.RC_SUCCESS){
            return err;
        }
        HashMap<String,String> params = cnfg.getParams();
        for (String token:params.keySet()){
            String value = params.get(token);
            switch (ManagerTokens.valueOf(token)){
                case INPUT_FILE:
                    try {
                        this.inputStream = new FileInputStream(value);
                    } catch (FileNotFoundException e) {
                        return RC.RC_MANAGER_INVALID_INPUT_FILE;
                    }
                    break;
                case OUTPUT_FILE:
                    try {
                        this.outputStream = new FileOutputStream(value);
                    } catch (FileNotFoundException e) {
                        return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
                    }
                    break;
                case READER_CONFIG_FILE:
                    this.readerConfig = value;
                    break;
                case WRITER_CONFIG_FILE:
                    this.writerConfig = value;
                    break;
                case EXECUTOR_CONFIG_FILE:
                    this.executorConfig = value;
                    break;
            }
        }
        return RC.RC_SUCCESS;
    }

    private RC setReader(){
        RC err = this.reader.setConfig(readerConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        err = this.reader.setInputStream(inputStream);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return this.reader.setConsumer(executor);
    }

    private RC setWriter(){
        RC err = this.writer.setConfig(this.writerConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return this.writer.setOutputStream(this.outputStream);
    }

    private RC setExecutor(){
        RC err = this.executor.setConfig(this.executorConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return this.executor.setConsumer(this.writer);
    }


    public RC BuildPipeline(String path) {

        RC err = setConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        err = setReader();
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        err = setExecutor();
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        err = setWriter();
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        return reader.run();
    }


//    private ReturnCode errorState;


//    public boolean isValidInitialization(){
//        if (this.reader.isValidInitialization() &&
//            this.writer.isValidInitialization() &&
//            this.executor.isValidInitialization()){
//            this.errorState = ReturnCode.SUCCESS;
//            return true;
//        }
//        else {
//            this.errorState = ReturnCode.INVALID_INITIALIZATION;
//        }
//        return false;
//    }
//    public ReturnCode getErrorState() {return this.errorState;}
//
//    public ReturnCode setParams(Config confg){
//        this.errorState = confg.isValidConfg();
//        if (this.errorState != ReturnCode.SUCCESS){
//            System.out.println("Invalid config");
//            return this.errorState;
//        }


//        HashMap<String,String> confgParams = confg.getParams();
//        for (String key: confgParams.keySet()){
//            if (!confGrammar.isValidToken(key)){
//                    System.out.println("Invalid config: " + key);
//                    return false;
//                }
//            switch (Parameters.valueOf(key)){
//                case BUFFER_SIZE:
//                    try{
//                        int buffer_size = Integer.parseInt(confgParams.get(key));
//                        this.errorState = this.reader.SetBuffer(buffer_size);
//                        if (this.errorState != ReturnCode.SUCCESS){
//                            return this.errorState;
//                        }
//                    } catch (NumberFormatException e) {
//                        System.out.println("Buffer size value must be an integer");
//                        this.errorState = ReturnCode.INVALID_BUFFER_SIZE;
//                        return ReturnCode.INVALID_BUFFER_SIZE;
//                    }
//                    break;
//                case INPUT_FILE:
//                    this.errorState = this.reader.SetPath(confgParams.get(key));
//                    if (this.errorState != ReturnCode.SUCCESS){
//                        return this.errorState ;
//                    }
//                    break;
//                case OUTPUT_FILE:
//                    this.errorState  = this.writer.SetPath(confgParams.get(key));
//                    if (this.errorState != ReturnCode.SUCCESS){
//                        return this.errorState ;
//                    }
//                    break;
//                case ACTION:
//                    try {
//                        this.action = Action.valueOf(confgParams.get(key));
//                    } catch (IllegalArgumentException e){
//                        System.out.println("Invalid mode value: " + key);
//                        this.errorState = ReturnCode.INVALID_MODE_VALUE;
//                        return this.errorState ;
//                    }
//                    break;
//                case TABLE_PATH:
//                    this.errorState = this.executor.setTable(confgParams.get(key));
//                    if (this.errorState != ReturnCode.SUCCESS){
//                        System.out.println("Error in setting table");
//                        return this.errorState ;
//                    }
//                    break;
//
//            }
//
//
//        }
//        this.errorState = ReturnCode.SUCCESS;
//        return this.errorState ;
//    }

//    public Manager(Config confg){
//        if (setParams(confg) != ReturnCode.SUCCESS){
//            System.out.println("Error while setting parameters");
//        }



//    public ReturnCode Run(){
//
//        if (!isValidInitialization()){
//            System.out.println("Some parameters are not initialized correctly");
//            return this.errorState;
//        }
//        int read_bytes = 0;
//        while ((read_bytes = reader.ReadBatch()) != 0){
//            System.out.println(read_bytes);
//            byte[] buf = reader.getBuffer();
//            byte[] processed;
//            if (this.action == Action.ENCODE){
//                processed = executor.Encode(buf);
//            } else {
//                processed = executor.Decode(buf);
//            }
//            writer.WriteBatch(processed,read_bytes);
//
//        }
//        this.errorState = CloseAll();
//        return this.errorState;
//    }
//
//    public ReturnCode CloseAll(){
//
//        this.errorState = this.reader.CloseStream();
//        if (this.errorState != ReturnCode.SUCCESS){
//            this.writer.CloseStream();
//        } else {
//            this.errorState = this.writer.CloseStream();
//        }
//        return this.errorState;
//    }

}
