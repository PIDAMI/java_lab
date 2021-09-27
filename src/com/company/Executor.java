package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Executor {

    public static final String TABLE_DELIMITER="=";
    public static final int TOKENS_PER_LINE = 2;
    public static final int TABLE_SIZE = 256;
    private ReturnCode errorState;

    // check if table is bijection and 256 elements long
    public static ReturnCode isValidTable(HashMap<Byte,Byte> table){

        // valid table is basically 2 permutations which means
        // 2 sets(contain distinct values), each has alphabet's size (256, byte's size)
        if (table.size() < TABLE_SIZE){
            System.out.println("Invalid table - size is less 256");
            return ReturnCode.GRAMMAR_ERROR;
        }

        HashSet<Byte> image = new HashSet<Byte>();
        for (Byte b: table.keySet()){
            image.add(table.get(b));
        }


        if (image.size() >= TABLE_SIZE)
            return ReturnCode.SUCCESS;
        else {
            System.out.println("Invalid table - not injective");
            return ReturnCode.GRAMMAR_ERROR;
        }
    }

    // check if line's format is <X>=<Y>, X,Y - decimal signed bytes
    public static ReturnCode isValidGrammarly(String word){
        try{
            String[] tokens = word.split(Executor.TABLE_DELIMITER);
            if (tokens.length != Executor.TOKENS_PER_LINE) {
                System.out.println("Invalid table format. Line contains 2 tokens separated by '='");
                return ReturnCode.GRAMMAR_ERROR;
            }
            byte LeftByte = Byte.parseByte(tokens[0]);
            byte RightByte = Byte.parseByte(tokens[1]);
        } catch (NumberFormatException e){
            System.out.println("Invalid config format: tokens must be decimal integers in range -127 to 128");
            return ReturnCode.GRAMMAR_ERROR;
        }

        return ReturnCode.SUCCESS;
    }

    private final HashMap<Byte,Byte> encoder = new HashMap<Byte,Byte>();
    private final HashMap<Byte,Byte> decoder = new HashMap<Byte,Byte>();
    private boolean validInitialization = false;

    public ReturnCode setTable(String path) {
        try (Scanner scanner = new Scanner(new File(path))) {
            String line;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                String[] tokens = line.split(TABLE_DELIMITER);
                this.errorState = Executor.isValidGrammarly(line);
                if (this.errorState == ReturnCode.GRAMMAR_ERROR) {
                    return ReturnCode.GRAMMAR_ERROR;
                }
                byte x = Byte.parseByte(tokens[0]);
                byte y = Byte.parseByte(tokens[1]);
                this.encoder.put(x, y);
                this.decoder.put(y, x);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Table file not found");
            this.errorState = ReturnCode.FILE_NOT_FOUND;
            return ReturnCode.FILE_NOT_FOUND;
        }
        this.errorState = isValidTable(this.decoder);
        if (this.errorState == ReturnCode.GRAMMAR_ERROR){
            return ReturnCode.GRAMMAR_ERROR;
        }

        this.validInitialization = true;

        return this.errorState;
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
        if (!this.validInitialization){
            this.errorState = ReturnCode.INVALID_INITIALIZATION;
        }
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
