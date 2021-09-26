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
        if (this.reader.isValidInitialization() &&
            this.writer.isValidInitialization() &&
            this.executor.isValidInitialization()){
            this.errorState = ReturnCode.SUCCESS;
            return true;
        }
        else {
            this.errorState = ReturnCode.INVALID_INITIALIZATION;
        }
        return false;
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
                        this.errorState = this.reader.SetBuffer(buffer_size);
                        if (this.errorState != ReturnCode.SUCCESS){
                            return this.errorState;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Buffer size value must be an integer");
                        this.errorState = ReturnCode.INVALID_BUFFER_SIZE;
                        return ReturnCode.INVALID_BUFFER_SIZE;
                    }
                    break;
                case INPUT_FILE:
                    this.errorState = this.reader.SetPath(confgParams.get(key));
                    if (this.errorState != ReturnCode.SUCCESS){
                        return this.errorState ;
                    }
                    break;
                case OUTPUT_FILE:
                    this.errorState  = this.writer.SetPath(confgParams.get(key));
                    if (this.errorState != ReturnCode.SUCCESS){
                        return this.errorState ;
                    }
                    break;
                case ACTION:
                    try {
                        this.action = Action.valueOf(confgParams.get(key));
                    } catch (IllegalArgumentException e){
                        System.out.println("Invalid mode value: " + key);
                        this.errorState = ReturnCode.INVALID_MODE_VALUE;
                        return this.errorState ;
                    }
                    break;
                case TABLE_PATH:
                    this.errorState = this.executor.setTable(confgParams.get(key));
                    if (this.errorState != ReturnCode.SUCCESS){
                        System.out.println("Error in setting table");
                        return this.errorState ;
                    }
                    break;

            }


        }
        this.errorState = ReturnCode.SUCCESS;
        return this.errorState ;
    }

    public Manager(Config confg){
        if (setParams(confg) != ReturnCode.SUCCESS){
            System.out.println("Error while setting parameters");
        }


    }
    public ReturnCode Run(){

        if (!isValidInitialization()){
            System.out.println("Some parameters are not initialized correctly");
            return this.errorState;
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
        this.errorState = CloseAll();
        return this.errorState;
    }

    public ReturnCode CloseAll(){

        this.errorState = this.reader.CloseStream();
        if (this.errorState != ReturnCode.SUCCESS){
            this.writer.CloseStream();
        } else {
            this.errorState = this.writer.CloseStream();
        }
        return this.errorState;
    }

}
