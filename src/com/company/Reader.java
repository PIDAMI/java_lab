package com.company;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Reader {

    private FileInputStream inputStream;
    private byte[] buffer;
    private boolean validPath = false;
    private boolean validBuffer = false;
    private ReturnCode errorState;

    public ReturnCode SetPath(String path){
        try {
            this.inputStream = new FileInputStream(new File(path));
            this.validPath = true;
        } catch (FileNotFoundException e){
            System.out.println("Input file not found");
            this.errorState = ReturnCode.FILE_NOT_FOUND;
        }
        return this.errorState;
    }

    public ReturnCode SetBuffer(int bufferSize){
        if (bufferSize < 1){
            System.out.println("Buffer size must be a positive integer");
            this.errorState = ReturnCode.INVALID_BUFFER_SIZE;
        } else {
            buffer = new byte[bufferSize];
            this.validBuffer = true;
        }
        return this.errorState;
    }

    public  ReturnCode isValidInitialization(){
        boolean validInit = this.validBuffer && this.validPath;
        if (!validInit){
            this.errorState = ReturnCode.INVALID_INITIALIZATION;
        }
        return this.errorState;
    }

    public int ReadBatch(){

        int nonEmptyBufferSize = 0;
        try {
            nonEmptyBufferSize = inputStream.read(buffer,0,buffer.length);
            if (nonEmptyBufferSize == -1){ // if reached file's end
                nonEmptyBufferSize = 0;
            }
        } catch (IOException e) {
            System.out.println("Error occurred while reading file");
            this.errorState = ReturnCode.READ_ERROR;
            nonEmptyBufferSize = 0; // in both scenarios return 0 as it doesnt matter

        }

        return nonEmptyBufferSize;
    }

    public byte[] getBuffer() {
        return buffer;
    }


    public ReturnCode getErrorState() {return this.errorState;}
    public ReturnCode CloseStream(){
        try {
            this.inputStream.close();
            this.errorState = ReturnCode.SUCCESS;
        } catch (IOException e) {
            System.out.println("Error occurred while closing file");
            this.errorState = ReturnCode.STREAM_CLOSE_ERROR;
        }
        return this.errorState
    }

}
