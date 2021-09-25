package com.company;
import java.util.HashMap;

public class Manager {

    private enum Action {
        DECODE,
        ENCODE
    }
    private enum Parameters {
        INPUT_FILE,
        OUTPUT_FILE,
        ACTION,
        TABLE_PATH,
        BUFFER_SIZE
    }
//    private static final Grammar confGrammar = new Grammar(new String[]{"BUFFER_SIZE", "OUTPUT_FILE",
//                                                    "INPUT_FILE","ACTION","TABLE_PATH"});
    private final Reader reader = new Reader();
    private final Writer writer = new Writer();
    private final Executor executor = new Executor();
//    private Config confg;
    private Action action;
    private ReturnCode errorState;


    public boolean isValidInitialization(){

        return reader.isValidInitialization() && writer.isValidInitialization() && executor.isValidInitialization();
    }
    public ReturnCode getErrorState() {return this.errorState;}

    public ReturnCode setParams(Config confg){
        this.errorState = confg.isValidConfg();
        if (this.errorState != ReturnCode.SUCCESS){
            System.out.println("Invalid config");
            return this.errorState;
        }


        HashMap<String,String> confgParams = confg.getParams();
        for (String key: confgParams.keySet()){
//            if (!confGrammar.isValidToken(key)){
//                    System.out.println("Invalid config: " + key);
//                    return false;
//                }
            switch (Parameters.valueOf(key)){
                case BUFFER_SIZE:
                    try{
                        int buffer_size = Integer.parseInt(confgParams.get(key));
                        if (!reader.SetBuffer(buffer_size)){
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Buffer size value must be an integer");
                        return false;
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
                        return false;
                    }
                    break;
                case TABLE_PATH:
                    if (!this.executor.setTable(confgParams.get(key))){
                        return false;
                    }
                    break;

            }


        }

     return true;
    }

    public Manager(Config confg){
        if (!setParams(confg)){
            System.out.println("Error while setting parameters");
        }


    }
    public void Run(){
        if (!isValidInitialization()){
            System.out.println("Some parameters are not initialized correctly");
            return;
        }
        int read_bytes = 0;
        while ((read_bytes = reader.ReadBatch()) != 0){
            System.out.println(read_bytes);
            byte[] buf = reader.getBuffer();
            byte[] processed;
            if (this.action == Action.ENCODE){
                processed = executor.Encode(buf);
            } else {
                processed = executor.Decode(buf);
            }
            writer.WriteBatch(processed,read_bytes);

        }
        CloseAll();
    }

    public ReturnCode CloseAll(){

        this.errorState = reader.CloseStream();
        writer.CloseStream();
    }

}
