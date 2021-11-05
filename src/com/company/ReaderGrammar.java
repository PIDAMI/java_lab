package com.company;

import com.java_polytech.pipeline_interfaces.RC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

enum ReaderTokens {
    BUFFER_SIZE
}

public class ReaderGrammar extends AbstractGrammar{

    ReaderGrammar(){
        super(Arrays.stream(ReaderTokens.values()).map(Enum::toString).toArray(String[]::new));
    }


//    @Override
//    public List<String> getTokens(){
//        return Arrays.stream(TOKENS.values()).map(Enum::toString).collect(Collectors.toList());
//    }
//
//    @Override
//    public boolean isValidToken(String val) {
//        return val.equals(TOKENS.BUFFER_SIZE.toString());
//    }

//    @Override
//    public int getNumTokens() {
//        return TOKENS.values().length;
//    }

    @Override
    public RC getGrammarErrorCode() {
        return RC.RC_READER_CONFIG_GRAMMAR_ERROR;
    }

    @Override
    public RC getNoFileErrorCode() {
        return RC.RC_READER_CONFIG_FILE_ERROR;
    }

    @Override
    public RC getIncompleteConfigErrorCode() {
        return new RC(RC.RCWho.READER,
                RC.RCType.CODE_CUSTOM_ERROR,
                "Not enough parameters in readers config for work.");
    }
}
