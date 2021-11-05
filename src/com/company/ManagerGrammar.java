package com.company;

import com.java_polytech.pipeline_interfaces.RC;

import java.util.Arrays;

enum ManagerTokens{
    INPUT_FILE,
    OUTPUT_FILE,
    READER_CONFIG_FILE,
    WRITER_CONFIG_FILE,
    EXECUTOR_CONFIG_FILE
}

public class ManagerGrammar extends AbstractGrammar {
    ManagerGrammar() {
        super(Arrays.stream(ManagerTokens.values()).map(Enum::toString).toArray(String[]::new));
    }

    @Override
    RC getGrammarErrorCode() {
        return RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
    }

    @Override
    RC getNoFileErrorCode() {
        return RC.RC_MANAGER_CONFIG_FILE_ERROR;
    }

    @Override
    RC getIncompleteConfigErrorCode() {
        return new RC(RC.RCWho.MANAGER,
                RC.RCType.CODE_CUSTOM_ERROR,
                "Not enough parameters in manager's config for work.");

    }
}
