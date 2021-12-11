import com.java_polytech.pipeline_interfaces.RC;

import java.util.Arrays;


public class ManagerGrammar extends BaseGrammar {


    // maybe hasSingleValue name should be named
    // canOnlyHaveSingleValue
    public enum ManagerTokens {
        READER_NAME(true),
        EXECUTOR_NAMES(false),
        WRITER_NAME(true),
        INPUT_FILE(true),
        OUTPUT_FILE(true),
        READER_CONFIG_FILE(true),
        WRITER_CONFIG_FILE(true),
        EXECUTOR_CONFIG_FILES(false);


        public final boolean hasSingleValue;
        ManagerTokens(boolean hasSingleValue){
            this.hasSingleValue=hasSingleValue;
        }

    }

    private final static RC RC_MANAGER_INCOMPLETE_CONFIG_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Not enough parameters in manager's config for work.");

    ManagerGrammar() {
        super(Arrays.stream(ManagerTokens.values())
                .map(Enum::toString)
                .toArray(String[]::new),
                RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR,
                RC.RC_MANAGER_CONFIG_FILE_ERROR,
                RC_MANAGER_INCOMPLETE_CONFIG_ERROR);
    }


}
