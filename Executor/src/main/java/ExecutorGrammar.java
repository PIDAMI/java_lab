
import com.java_polytech.pipeline_interfaces.RC;
import java.util.Arrays;



public class ExecutorGrammar extends BaseGrammar {

    public enum ExecutorTokens{
        TABLE_PATH,
        ACTION
    }
    private final static RC RC_EXECUTOR_INCOMPLETE_CONFIG_ERROR = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Not enough parameters in executors config for work.");

    ExecutorGrammar() {
        super(Arrays.stream(ExecutorTokens.values())
                    .map(Enum::toString)
                    .toArray(String[]::new),
                RC.RC_EXECUTOR_CONFIG_GRAMMAR_ERROR,
                RC.RC_EXECUTOR_CONFIG_FILE_ERROR,
                RC_EXECUTOR_INCOMPLETE_CONFIG_ERROR);
    }

}
