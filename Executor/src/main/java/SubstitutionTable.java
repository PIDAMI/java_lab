import com.java_polytech.pipeline_interfaces.RC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class SubstitutionTable {


    private final HashMap<Byte,Byte> table = new HashMap<>();


    public static final String TABLE_DELIMITER="=";
    public static final int MAX_TABLE_SIZE = 256;

    private final static RC RC_NON_BIJECTIVE_TABLE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Table is not bijective");

    private final static RC RC_INVALID_TABLE_SIZE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid table: there must be less than " + MAX_TABLE_SIZE +
                    "unique entries");
    private final static RC RC_INVALID_TABLE_VALUE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid table format: tokens must be decimal integers in range -127 to 128");

    private final static RC RC_TABLE_FILE_ERROR = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "File with substitution table not found.");


    public Byte get(Byte key){
        Byte val = table.get(key);
        return val == null? key : val;
    }

    // check if table is bijection and less than 256 elements long
    public static RC isValidTable(HashMap<Byte,Byte> table){

        // valid table is basically 2 permutations(of same subset of 256 bytes)
        // which means 2 sets(contain distinct values)
        // each has <= 256 size
        if (table.size() > MAX_TABLE_SIZE){
            return RC_INVALID_TABLE_SIZE;
        }


        if (!table.keySet().equals(table.values()))
            return RC_NON_BIJECTIVE_TABLE;
        else {
            return RC.RC_SUCCESS;
        }
    }

    // check if line's format is <X>=<Y>,
    // X,Y - decimal signed integers representing java bytes
    public static RC isValidGrammarly(String word){
        try{
            String[] tokens = word.split(TABLE_DELIMITER);
            Byte.parseByte(tokens[0]);
            Byte.parseByte(tokens[1]);
        } catch (NumberFormatException e){
            return RC_INVALID_TABLE_VALUE;
        }
        return RC.RC_SUCCESS;
    }


    public RC loadTable(Executor.Action action, String tablePath) {

        try (Scanner scanner = new Scanner(new File(tablePath))) {
            String line;
            RC err;
            while (scanner.hasNext()) {
                line = scanner.nextLine();
                String[] tokens = line.split(TABLE_DELIMITER);
                err = isValidGrammarly(line);
                if (!err.equals(RC.RC_SUCCESS))
                    return err;

                byte key = Byte.parseByte(tokens[action.keyIndex]);
                byte val = Byte.parseByte(tokens[action.valIndex]);
                this.table.put(key,val);

            }
        } catch (FileNotFoundException e) {
            return RC_TABLE_FILE_ERROR;
        }
        return isValidTable(this.table);
    }
}
