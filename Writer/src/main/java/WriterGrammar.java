
import com.java_polytech.pipeline_interfaces.RC;

import java.util.Arrays;

public class WriterGrammar extends AbstractGrammar{

    public enum WriterTokens {

    }


    private static final RC RC_WRITER_INCOMPLETE_CONFIG_ERROR = new RC(
            RC.RCWho.WRITER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Not enough parameters in writer's config for work.");

    WriterGrammar() {
        super(Arrays.stream(WriterTokens.values())
                    .map(Enum::toString)
                    .toArray(String[]::new),
                RC.RC_WRITER_CONFIG_GRAMMAR_ERROR,
                RC.RC_WRITER_CONFIG_FILE_ERROR,
                RC_WRITER_INCOMPLETE_CONFIG_ERROR);
    }

}
