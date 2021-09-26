package com.company;

import java.io.*;

public class Writer {

    private FileOutputStream outputStream;
//    private byte[] buffer;
//    private int nonEmptyBufferSize = 0;
      public boolean validInitialization = false;
    private ReturnCode errorState;

    public boolean isValidInitialization(){
        if (!this.validInitialization){
            this.errorState = ReturnCode.INVALID_INITIALIZATION;
        }
        return this.validInitialization;
    }
    public ReturnCode getErrorState(){return this.errorState;}
    public ReturnCode SetPath(String path){
        try {
            this.outputStream = new FileOutputStream(new File(path));
            this.validInitialization = true;
            this.errorState = ReturnCode.SUCCESS;

        } catch (FileNotFoundException e){
            System.out.println("Input file not found");
            this.errorState = ReturnCode.FILE_NOT_FOUND;
        }
        return this.errorState;
    }

    public ReturnCode WriteBatch(byte[] data, int nonZeroSize){

        try {
            outputStream.write(data,0,nonZeroSize);
            this.errorState = ReturnCode.SUCCESS;
        } catch (IOException e) {
            System.out.println("Error occurred while writing to file");
            errorState = ReturnCode.WRITE_ERROR;
        }
        return this.errorState;
    }

    public ReturnCode CloseStream(){
        try {
            outputStream.close();
            this.errorState = ReturnCode.SUCCESS;
        } catch (IOException e) {
            System.out.println("Error occurred while writing to file");
            this.errorState = ReturnCode.STREAM_CLOSE_ERROR;
        }
        return this.errorState;
    }

}
