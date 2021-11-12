import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Manager implements IConfigurable {




    private static final RC RC_MANAGER_READER_NAME_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Can't make instance of reader."
    );


    private static final RC RC_MANAGER_WRITER_NAME_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Can't make instance of writer."
    );


    private static final RC RC_MANAGER_EXECUTOR_NAME_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Can't make instance of executor."
    );


    private OutputStream outputStream;
    private InputStream inputStream;
    private String readerConfig;
    private String executorConfig;
    private String writerConfig;

    private String readerName;
    private String executorName;
    private String writerName;

    private IReader reader;
    private IExecutor executor;
    private IWriter writer;

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
                case READER_NAME:
                    this.readerName = value;
                    break;
                case WRITER_NAME:
                    this.writerName = value;
                    break;
                case EXECUTOR_NAME:
                    this.executorName = value;
                    break;
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


    private IConfigurable createConfigurable(String className)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {


        Class<?> c = Class.forName(className);
        Constructor<?> cons = c.getConstructor();
        Object object = cons.newInstance();
        return (IConfigurable) object;

    }

    private RC setReader(){


        RC err = reader.setConfig(readerConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        err = reader.setInputStream(inputStream);

        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return reader.setConsumer(executor);
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

        try {
            reader = (IReader) createConfigurable(readerName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            e.printStackTrace();
            return RC_MANAGER_READER_NAME_ERROR;
        }

        try {
            executor = (IExecutor) createConfigurable(executorName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            return RC_MANAGER_EXECUTOR_NAME_ERROR;
        }

        try {
            writer = (IWriter) createConfigurable(writerName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            return RC_MANAGER_WRITER_NAME_ERROR;
        }


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
