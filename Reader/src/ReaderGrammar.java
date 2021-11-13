
import com.java_polytech.pipeline_interfaces.RC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



public class ReaderGrammar extends AbstractGrammar{

    private final static RC RC_READER_INCOMPLETE_CONFIG_ERROR = new RC(RC.RCWho.READER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Not enough parameters in readers config for work.");

    ReaderGrammar(){
        super(Arrays.stream(ReaderTokens.values())
                .map(Enum::toString)
                .toArray(String[]::new));
    }



    @Override
    public RC getGrammarErrorCode() {
        return RC.RC_READER_CONFIG_GRAMMAR_ERROR;
    }

    @Override
    public RC getNoFileErrorCode() {
        return RC.RC_READER_CONFIG_FILE_ERROR;
    }

    @Override
    public RC getIncompleteConfigErrorCode() { return RC_READER_INCOMPLETE_CONFIG_ERROR; }
}
