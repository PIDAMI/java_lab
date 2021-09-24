package com.company;
import com.company.Reader;
import com.company.Writer;
import com.company.Config;

import java.util.HashMap;
import java.util.Set;

public class Executor {

    public enum Action {
        COMPRESS,
        DECOMPRESS
    }
    public enum Parameters {
        INPUT_FILE,
        OUTPUT_FILE,
        ACTION,
        TABLE,
        BUFFER_SIZE
    }

    private Reader reader;
    private Writer writer;
    private Config confg;
    private Action action;
    private final HashMap<Byte,Byte> substTable = new HashMap<Byte,Byte>();

    public boolean setParams(){
        HashMap<String,String> confgParams = this.confg.getParams();
        for (String key: confgParams.keySet()){
            switch (Parameters.valueOf(key)){
                case BUFFER_SIZE:
                    try{
                        int buffer_size = Integer.parseInt(confgParams.get(key));
                        if (!reader.SetBuffer(buffer_size)){
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Buffer size value must be an integer");
                    }
                    break;
                case INPUT_FILE:
                    if (!reader.SetPath(confgParams.get(key))){
                        return false;
                    }
                    break;
                case OUTPUT_FILE:
                    if (!writer.SetPath(confgParams.get(key))){
                        return false;
                    }
                    break;
                case ACTION:
                    try {
                        this.action = Action.valueOf(confgParams.get(key));
                    } catch (IllegalArgumentException e){
                        System.out.println("Invalid mode value: " + key);
                    }
                    break;
                case TABLE:




            }


        }

     return false;
    }

    public Executor(Config confg){


    }


}
