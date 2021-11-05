package com.company;

import com.java_polytech.pipeline_interfaces.RC;

import java.util.Arrays;

enum ExecutorTokens{
    TABLE_PATH,
    ACTION //
}

public class ExecutorGrammar extends AbstractGrammar{

    ExecutorGrammar() {
        super(Arrays.stream(ExecutorTokens.values()).map(Enum::toString).toArray(String[]::new));
    }

    @Override
    RC getGrammarErrorCode() {
        return RC.RC_EXECUTOR_CONFIG_GRAMMAR_ERROR;
    }

    @Override
    RC getNoFileErrorCode() {
        return RC.RC_EXECUTOR_CONFIG_FILE_ERROR;
    }

    @Override
    RC getIncompleteConfigErrorCode() {
        return new RC(RC.RCWho.EXECUTOR,
                RC.RCType.CODE_CUSTOM_ERROR,
                "Not enough parameters in executors config for work.");
    }
}
