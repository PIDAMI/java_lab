package com.company;

import java.io.*;

public class Writer {

    private FileOutputStream outputStream;
//    private byte[] buffer;
//    private int nonEmptyBufferSize = 0;
    public boolean validInitialization = false;


    public Writer(String path) {
        try {
            this.outputStream = new FileOutputStream(new File(path));
            validInitialization = true;

        } catch (FileNotFoundException e){
            System.out.println("Input file not found");
        }


    }

    public void WriteBatch(byte[] data){

        try {
            outputStream.write(data,0,data.length);
        } catch (IOException e) {
            System.out.println("Error occurred while writing to file");
        }
    }

    public void CloseStream(){
        try {
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Error occurred while writing to file");
        }
    }

}
