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


    private enum Action{
        ENCODE,
        DECODE
    }


    private final static RC RC_INVALID_ACTION = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid executor's action - can only be ENCODE or DECODE");

    private final static RC RC_INVALID_TABLE_SIZE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid table: there must be " + Executor.TABLE_SIZE + "unique entries");
    private final static RC RC_INVALID_TABLE_VALUE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid table format: tokens must be decimal integers in range -127 to 128");

    private final static RC RC_TABLE_FILE_ERROR = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "File with substitution table not found.");


    private final HashMap<Byte,Byte> table = new HashMap<>();
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
                        return RC_INVALID_ACTION;
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
            result[i] = table.get(bytes[i]);
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

        // valid table is basically 2 permutations which means
        // 2 sets(contain distinct values), each has alphabet's size (256, byte's size)
        if (table.size() != TABLE_SIZE){
            return RC_INVALID_TABLE_SIZE;
        }

        int unique_values = new HashSet<>(table.values()).size();

        if (unique_values != TABLE_SIZE)
            return RC_INVALID_TABLE_SIZE;
        else {
            return RC.RC_SUCCESS;
        }
    }

    // check if line's format is <X>=<Y>,
    // X,Y - decimal signed integers representing java bytes
    public static RC isValidGrammarly(String word){
        try{
            String[] tokens = word.split(Executor.TABLE_DELIMITER);
            byte leftByte = Byte.parseByte(tokens[0]);
            byte rightByte = Byte.parseByte(tokens[1]);
        } catch (NumberFormatException e){
            return RC_INVALID_TABLE_VALUE;
        }
        return RC.RC_SUCCESS;
    }



    public RC loadTable() {
        int keyIndex;
        int valIndex;
        if (this.action == Action.ENCODE){
            keyIndex = 0;
            valIndex = 1;
        } else {
            keyIndex = 1;
            valIndex = 0;
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

                byte key = Byte.parseByte(tokens[keyIndex]);
                byte val = Byte.parseByte(tokens[valIndex]);
                this.table.put(key,val);

            }
        } catch (FileNotFoundException e) {
            return RC_TABLE_FILE_ERROR;
        }
        return isValidTable(this.table);
    }



}
