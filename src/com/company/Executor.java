package com.company;

import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IExecutor;
import com.java_polytech.pipeline_interfaces.RC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Executor implements IExecutor {


    enum Action{
        ENCODE,
        DECODE
    }

    private final HashMap<Byte,Byte> table = new HashMap<>();
    private final HashMap<Byte,Byte> encoder = new HashMap<Byte,Byte>();
    private final HashMap<Byte,Byte> decoder = new HashMap<Byte,Byte>();
    private Config cnfg;
    private Action action;
    private String tablePath;
    public static final String TABLE_DELIMITER="=";
    public static final int TABLE_SIZE = 256;
    private final AbstractGrammar grammar = new ExecutorGrammar();
    private IConsumer consumer;

    @Override
    public RC setConfig(String path) {
        this.cnfg = new Config(grammar);
        RC err = this.cnfg.ParseConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        HashMap<String,String> params = this.cnfg.getParams();
        for (String token:params.keySet()){
            switch (ExecutorTokens.valueOf(token)){
                case ACTION:
                    try{
                        this.action = Action.valueOf(params.get(token));
                    } catch (IllegalArgumentException e){
                        return new RC(RC.RCWho.EXECUTOR,
                                RC.RCType.CODE_CUSTOM_ERROR,
                                "Invalid executor's action - can only be ENCODE or DECODE");
                    }
                    break;
                case TABLE_PATH:
                    this.tablePath = params.get(token);
                    break;
            }
        }
        return loadTable();
    }


    @Override
    public RC consume(byte[] bytes) {
        if (bytes == null)
            return consumer.consume(null);
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i){
            result[i] = encoder.get(bytes[i]);
        }
        return consumer.consume(result);
    }

    @Override
    public RC setConsumer(IConsumer iConsumer) {
        this.consumer = iConsumer;
        return RC.RC_SUCCESS;
    }

    // check if table is bijection and 256 elements long
    public static RC isValidTable(HashMap<Byte,Byte> table){
        RC invalid_size_err = new RC(RC.RCWho.EXECUTOR,
                RC.RCType.CODE_CUSTOM_ERROR,
                "Invalid table: size not equal to " + Executor.TABLE_SIZE);
        // valid table is basically 2 permutations which means
        // 2 sets(contain distinct values), each has alphabet's size (256, byte's size)
        if (table.size() != TABLE_SIZE){
            return invalid_size_err;
        }

        HashSet<Byte> image = new HashSet<Byte>();
        int unique_values = new HashSet<>(table.values()).size();

        if (unique_values != TABLE_SIZE)
            return invalid_size_err;
        else {
            return RC.RC_SUCCESS;
        }
    }

    // check if line's format is <X>=<Y>,
    // X,Y - decimal signed integers representing java bytes
    public static RC isValidGrammarly(String word){
        try{
            String[] tokens = word.split(Executor.TABLE_DELIMITER);
            byte LeftByte = Byte.parseByte(tokens[0]);
            byte RightByte = Byte.parseByte(tokens[1]);
        } catch (NumberFormatException e){
            return new RC(RC.RCWho.EXECUTOR,
                    RC.RCType.CODE_CUSTOM_ERROR,
                    "Invalid table format: tokens must be decimal integers in range -127 to 128");
        }
        return RC.RC_SUCCESS;
    }



    public RC loadTable() {
        int key_index;
        int val_index;
        if (this.action == Action.ENCODE){ // so there's no need to check every iteration
            key_index = 0;
            val_index = 1;
        } else {
            key_index = 1;
            val_index = 0;
        }
        try (Scanner scanner = new Scanner(new File(this.tablePath))) {
            String line;
            RC err;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                String[] tokens = line.split(TABLE_DELIMITER);
                err = Executor.isValidGrammarly(line);
                if (!err.equals(RC.RC_SUCCESS))
                    return err;

                byte key = Byte.parseByte(tokens[key_index]);
                byte val = Byte.parseByte(tokens[val_index]);
                this.table.put(key,val);

                this.encoder.put(key, val);
                this.decoder.put(val, key);
            }
        } catch (FileNotFoundException e) {
            return new RC(RC.RCWho.EXECUTOR,
                    RC.RCType.CODE_CUSTOM_ERROR,
                    "File with substitution table not found.");
        }
        return isValidTable(this.table);
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


//    public boolean isValidInitialization(){
//        if (!this.validInitialization){
//            this.errorState = ReturnCode.INVALID_INITIALIZATION;
//        }
//        return this.validInitialization;
//    }

    public byte[] Encode(byte[] input){
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; ++i){
            result[i] = encoder.get(input[i]);
        }
        return result;
    }


    public byte[] Decode(byte[] input){
        byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; ++i){
            result[i] = decoder.get(input[i]);
        }
        return result;
    }



}
