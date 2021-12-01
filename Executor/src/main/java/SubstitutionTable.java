import com.java_polytech.pipeline_interfaces.RC;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class SubstitutionTable {

    private final HashMap<Byte,Byte> table = new HashMap<>();


    public static final String TABLE_DELIMITER="=";
    public static final int TABLE_SIZE = 256;

    private final static RC RC_INVALID_TABLE_SIZE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid table: there must be " + TABLE_SIZE + "unique entries");
    private final static RC RC_INVALID_TABLE_VALUE = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid table format: tokens must be decimal integers in range -127 to 128");

    private final static RC RC_TABLE_FILE_ERROR = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "File with substitution table not found.");


    public Byte get(Byte key){
        return table.get(key);
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
            String[] tokens = word.split(TABLE_DELIMITER);
            Byte.parseByte(tokens[0]);
            Byte.parseByte(tokens[1]);
        } catch (NumberFormatException e){
            return RC_INVALID_TABLE_VALUE;
        }
        return RC.RC_SUCCESS;
    }


    public RC loadTable(BaseExecutor.Action action, String tablePath) {


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
