package com.company;

public class Grammar {
    final public static String DEMILIMITER = "=";
    final public static int TOKENS_PER_LINE = 2;
    private String[] tokens;

    public Grammar(String[] tokens) {this.tokens = tokens;}
    public String[] getTokens() { return tokens;}


}
