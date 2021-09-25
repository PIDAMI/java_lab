package com.company;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Reader {

    private FileInputStream inputStream;
    private byte[] buffer;
    public boolean validPath = false;
    public boolean validBuffer = false;

    public boolean SetPath(String path){
        try {
            inputStream = new FileInputStream(new File(path));
            this.validPath = true;
        } catch (FileNotFoundException e){
            System.out.println("Input file not found");
        }
        return this.validPath;
    }

    public boolean SetBuffer(int bufferSize){
        if (bufferSize < 1){
            System.out.println("Buffer size must be a positive integer");
        } else {
            buffer = new byte[bufferSize];
            this.validBuffer = true;
        }
        return this.validBuffer;
    }

    public boolean isValidInitialization(){
        return this.validBuffer && this.validPath;
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
            nonEmptyBufferSize = 0; // in both scenarios return 0 as it doesnt matter

        }

        return nonEmptyBufferSize;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void CloseStream(){
        try {
            this.inputStream.close();
        } catch (IOException e) {
            System.out.println("Error occurred while closing file");
        }
    }

}
