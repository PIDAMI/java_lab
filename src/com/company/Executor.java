package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Executor {

    public static final String TABLE_DELIMITER="=";
    public static final int TOKENS_PER_LINE = 2;

    // check if table is bijection and 256 elements long
    public static boolean isValidTable(HashMap<Byte,Byte> table){

        // valid table is basically 2 permutations which means
        // 2 sets(contain distinct values), each has alphabet's size (256, byte's size)
        if (table.size() < TABLE_SIZE){
            return false;
        }

        HashSet<Byte> image = new HashSet<Byte>();
        for (Byte b: table.keySet()){
            image.add(table.get(b));
        }
        return image.size() >= TABLE_SIZE;
    }

    // check if line's format is <X>=<Y>, X,Y - decimal signed bytes
    public static boolean isValidGrammarly(String word){
        boolean isValid = false;
        try{
            String[] tokens = word.split(Executor.TABLE_DELIMITER);
            if (tokens.length != Executor.TOKENS_PER_LINE) {
                System.out.println("Invalid table format. Line contains 2 tokens separated by '='");
                return false;
            }
            byte LeftByte = Byte.parseByte(tokens[0]);
            byte RightByte = Byte.parseByte(tokens[1]);
            isValid = true;
        } catch (NumberFormatException e){
            System.out.println("Invalid config format: tokens must be decimal integers in range -127 to 128");
        }
        return isValid;
    }

    public static final int TABLE_SIZE = 256;
    private final HashMap<Byte,Byte> encoder = new HashMap<Byte,Byte>();
    private final HashMap<Byte,Byte> decoder = new HashMap<Byte,Byte>();
    private boolean validInitialization = false;

    public boolean setTable(String path) {
        try (Scanner scanner = new Scanner(new File(path))) {
            String line;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                String[] tokens = line.split(TABLE_DELIMITER);
                if (!Executor.isValidGrammarly(line)) {
                    return false;
                }
                byte x = Byte.parseByte(tokens[0]);
                byte y = Byte.parseByte(tokens[1]);
                this.encoder.put(x, y);
                this.decoder.put(y, x);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Table file not found");
            return false;
        }
        if (!isValidTable(this.decoder) || !isValidTable(this.encoder)){
            return false;
        }
        this.validInitialization = true;

        return true;
    }


//    public boolean setSubstTable(String path){
//        try {
//            FileInputStream inputStream = new FileInputStream(new File(path));
//
//            HashMap<String,String> table = tableConfg.getParams();
//            for (String key:table.keySet()){
//                try {
//                    byte x = Byte.parseByte(key);
//                    byte y = Byte.parseByte(table.get(key));
//                    this.encoder.put(x,y);
//                    this.decoder.put(y,x);
//                } catch (NumberFormatException e) {
//                    System.out.println("Invalid substitution table file format:" + key + "_" + table.get(key));
//                    return false;
//                }
//            }
//            if (this.decoder.size() != TABLE_SIZE || this.encoder.size() != TABLE_SIZE){
//                System.out.println("Incomplete substituion table: must contain 256 values");
//                return false;
//            }
//
//
//        } catch (FileNotFoundException e){
//            System.out.println("Substitution table file not found");
//            return false;
//        }
//        this.validInitialization = true;
//        return true;
//    }


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
