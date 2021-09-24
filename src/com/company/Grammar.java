package com.company;

import java.util.Arrays;
import java.util.Set;

public class Grammar {
    final public static String DEMILIMITER = "=";
    final public static int TOKENS_PER_LINE = 2;
    private String[] tokens;

    public Grammar(String[] tokens) {this.tokens = tokens;}
    public boolean isValidToken(String val) { return Arrays.stream(tokens).anyMatch(val::equals);}

}
