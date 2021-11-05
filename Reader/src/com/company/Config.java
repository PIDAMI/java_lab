package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import com.java_polytech.pipeline_interfaces.*;

// syntax analyzer
public class Config {

    private final HashMap<String, String> params = new HashMap<>();
    private final AbstractGrammar grammar;

    public Config(AbstractGrammar grammar){
        this.grammar = grammar;
    }

    public RC ParseConfig(String path){
        try (Scanner scanner = new Scanner(new File(path))){
            String line;
            int numLines = 0;
            while (scanner.hasNext()){
                line = scanner.nextLine().trim();
                numLines++;
                String[] tokens = line.split(grammar.DEMILIMITER);
                if (!grammar.isValidToken(tokens[0])){
                    System.out.println("Invalid config value at line " + numLines+ " :" + tokens[0]);
                    return grammar.getGrammarErrorCode();
                }
                this.params.put(tokens[0],tokens[1]);
            }

            if (this.params.size() != grammar.getNumTokens()){
                return grammar.getIncompleteConfigErrorCode();
            }
            return RC.RC_SUCCESS;

        } catch(FileNotFoundException noFileExc){
            System.out.println("Config file not found");
            return grammar.getNoFileErrorCode();
        }
    }



    public HashMap<String,String> getParams() { return this.params;}
}
