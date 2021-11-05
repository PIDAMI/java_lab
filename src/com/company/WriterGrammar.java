package com.company;

import com.java_polytech.pipeline_interfaces.RC;

import java.util.Arrays;

enum WriterTokens{

}

public class WriterGrammar extends AbstractGrammar{

    WriterGrammar() {
        super(Arrays.stream(WriterTokens.values()).map(Enum::toString).toArray(String[]::new));
    }

    @Override
    RC getGrammarErrorCode() {
        return RC.RC_WRITER_CONFIG_GRAMMAR_ERROR;
    }

    @Override
    RC getNoFileErrorCode() {
        return RC.RC_WRITER_CONFIG_FILE_ERROR;
    }

    @Override
    RC getIncompleteConfigErrorCode() {
        return new RC(RC.RCWho.WRITER,
                RC.RCType.CODE_CUSTOM_ERROR,
                "Not enough parameters in writer's config for work.");
    }
}
