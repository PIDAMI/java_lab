package com.company;
import com.java_polytech.pipeline_interfaces.*;

import java.util.List;

public interface IGrammar {
    String DEMILIMITER = "=";
//    private final String[] tokens;

    boolean isValidToken(String val);
    int getNumTokens();
    List<String> getTokens();
    // specific error code for every executor's grammatic error
    RC getGrammarErrorCode();

    // same thing for case when config file not found
    // might not be the most appropriate place for this method to be
    // but convenient, so it's in grammar
    RC getNoFileErrorCode();


    // if config doesnt contain all needed parameters
    RC getIncompleteConfigErrorCode();
}
