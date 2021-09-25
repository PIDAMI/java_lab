package com.company;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class Executor {

    public static final int TABLE_SIZE = 256;
    private final HashMap<Byte,Byte> encoder = new HashMap<Byte,Byte>();
    private final HashMap<Byte,Byte> decoder = new HashMap<Byte,Byte>();
    private boolean validInitialization = false;


    public boolean setSubstTable(String path){
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            Config tableConfg = new Config(path);
            HashMap<String,String> table = tableConfg.getParams();
            for (String key:table.keySet()){
                try {
                    byte x = Byte.parseByte(key);
                    byte y = Byte.parseByte(table.get(key));
                    this.encoder.put(x,y);
                    this.decoder.put(y,x);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid substitution table file format:" + key + "_" + table.get(key));
                    return false;
                }
            }
            if (this.decoder.size() != TABLE_SIZE || this.encoder.size() != TABLE_SIZE){
                System.out.println("Incomplete substituion table: must contain 256 values");
                return false;
            }


        } catch (FileNotFoundException e){
            System.out.println("Substitution table file not found");
            return false;
        }
        this.validInitialization = true;
        return true;
    }
    public boolean isValidInitialization(){
        return this.validInitialization;
    }

    public byte[] Encode(byte[] input){
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; i++){
            result[i] = encoder.get(input[i]);
        }
        return result;
    }


    public byte[] Decode(byte[] input){
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; i++){
            result[i] = decoder.get(input[i]);
        }
        return result;
    }




}
