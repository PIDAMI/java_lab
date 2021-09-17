package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Reader {

    private FileInputStream inputStream;
    private byte[] buffer;
    private int nonEmptyBufferSize = 0;
    public boolean validInitialization = false;


    public Reader(String path, int bufferSize) {
        if (bufferSize < 1){
            System.out.println("Buffer size must be a positive integer");
        } else {
            try {
                inputStream = new FileInputStream(new File(path));
                buffer = new byte[bufferSize];
                validInitialization = true;

            } catch (FileNotFoundException e){
                System.out.println("Input file not found");
            }
        }

    }

    public int ReadBatch(){

        try {
            nonEmptyBufferSize = inputStream.read(buffer,0,buffer.length);
        } catch (IOException e) {
            System.out.println("Error occurred while reading file");
            nonEmptyBufferSize = 0;
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
