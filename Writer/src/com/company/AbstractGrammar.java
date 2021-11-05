package com.company;
import com.java_polytech.pipeline_interfaces.*;

import java.util.Arrays;
import java.util.List;


public abstract class AbstractGrammar {

    private final String[] tokens;
    AbstractGrammar(String[] tokens){this.tokens=tokens;}
    String[] getTokens() {return this.tokens;}
    public final String DEMILIMITER = "=";
    public boolean isValidToken(String val) {return Arrays.asList(tokens).contains(val);}
    public final int getNumTokens() {
        return this.tokens == null ? 0 : this.tokens.length;
    }


    // specific error code for every executor's grammatic error
    abstract RC getGrammarErrorCode();

    // same thing for case when config file not found
    // might not be the most appropriate place for this method to be
    // but convenient, so it's in grammar
    abstract RC getNoFileErrorCode();


    // if config doesnt contain all needed parameters
    abstract RC getIncompleteConfigErrorCode();

}
