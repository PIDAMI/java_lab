package com.company;

import java.io.*;

public class Writer {

    private FileOutputStream outputStream;
//    private byte[] buffer;
//    private int nonEmptyBufferSize = 0;
      public boolean validInitialization = false;


    public boolean isValidInitialization(){
        return this.validInitialization;
    }

    public boolean SetPath(String path){
        try {
            this.outputStream = new FileOutputStream(new File(path));
            this.validInitialization = true;

        } catch (FileNotFoundException e){
            System.out.println("Input file not found");
        }
        return this.validInitialization;
    }

    public void WriteBatch(byte[] data, int nonZeroSize){

        try {
            outputStream.write(data,0,nonZeroSize);
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
