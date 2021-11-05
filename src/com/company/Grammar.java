package com.company;

import java.io.InputStream;
import java.util.Arrays;
import com.java_polytech.pipeline_interfaces.*;
import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.RC;

import java.util.Set;

public class Grammar {
    final public static String DEMILIMITER = "=";
    final public static int TOKENS_PER_LINE = 2;
    private final String[] tokens;


    public Grammar(String[] tokens) {this.tokens = tokens;}
    public boolean isValidToken(String val) { return Arrays.stream(tokens).anyMatch(val::equals);}
    public String getToken(int index){
        return index < 0 || index >= tokens.length ? null:tokens[index];
    }
}
