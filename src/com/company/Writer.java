package com.company;

import java.io.*;
import java.util.HashMap;

import com.java_polytech.pipeline_interfaces.*;

public class Writer implements IWriter{

    private OutputStream outputStream;
    private Config cnfg;
    private final AbstractGrammar grammar = new WriterGrammar();
//    private byte[] buffer;
//    private int nonEmptyBufferSize = 0;
//      public boolean validInitialization = false;
//    private ReturnCode errorState;

    @Override
    public RC setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String path) {
        this.cnfg = new Config(grammar);
        RC err = this.cnfg.ParseConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC consume(byte[] bytes) {
        if (bytes == null)
            return CloseStream();
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            return RC.RC_WRITER_FAILED_TO_WRITE;
        }

        return RC.RC_SUCCESS;
    }
    private RC CloseStream(){
        RC err;
        try {
            outputStream.close();
           err = RC.RC_SUCCESS;
        } catch (IOException e) {
            err = new RC(RC.RCWho.READER,
                    RC.RCType.CODE_CUSTOM_ERROR,
                    "Writer couldn't close stream.");
        }
        return err;
    }


//    public boolean isValidInitialization(){
//        if (!this.validInitialization){
//            this.errorState = ReturnCode.INVALID_INITIALIZATION;
//        }
//        return this.validInitialization;
//    }
//    public ReturnCode getErrorState(){return this.errorState;}
//    public ReturnCode SetPath(String path){
//        try {
//            this.outputStream = new FileOutputStream(new File(path));
//            this.validInitialization = true;
//            this.errorState = ReturnCode.SUCCESS;
//
//        } catch (FileNotFoundException e){
//            System.out.println("Input file not found");
//            this.errorState = ReturnCode.FILE_NOT_FOUND;
//        }
//        return this.errorState;
//    }
//
//    public ReturnCode WriteBatch(byte[] data, int nonZeroSize){
//
//        try {
//            outputStream.write(data,0,nonZeroSize);
//            this.errorState = ReturnCode.SUCCESS;
//        } catch (IOException e) {
//            System.out.println("Error occurred while writing to file");
//            errorState = ReturnCode.WRITE_ERROR;
//        }
//        return this.errorState;
//    }
//



}
