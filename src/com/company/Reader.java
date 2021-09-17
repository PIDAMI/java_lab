package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Reader {

    private FileInputStream inputStream;
    private byte buffer[];
    private int nonEmptyBufferSize = 0;


    public Reader(String path, int bufferSize) throws FileNotFoundException {
        inputStream = new FileInputStream(new File(path));
        buffer = new byte[bufferSize];

    }

    public int ReadBatch(){

        try {
            nonEmptyBufferSize = inputStream.read(buffer,0,buffer.length);
        } catch (IOException e) {
            System.out.println("Error occured while reading file");
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
            System.out.println("Error occured while closing file");
        }
    }

}
