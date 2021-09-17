package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.company.Grammar;
public class Config {

//    private final Grammar grammar = new Grammar(new String[]{"BUFFER_SIZE", "OUTPUT_FILE","INPUT_FILE","ACTION"});
    private final HashMap<String, String> params = new HashMap<>();
    private boolean validConfg = false;

    public Config(String path){
        try (Scanner scanner = new Scanner(new File(path))){
            String line;
            int numLines = 0;
            while (scanner.hasNext()){
                line = scanner.nextLine().trim();
                numLines++;
                String[] tokens = line.split(Grammar.DEMILIMITER);
                if (tokens.length != Grammar.TOKENS_PER_LINE){
                    System.out.println("Improper config format at line " + numLines + ". Must be <PARAMETER>=<VALUE>");
                    break;
                }
                this.params.put(tokens[0],tokens[1]);
            }
            this.validConfg = true;

        } catch(FileNotFoundException noFileExc){
            System.out.println("Config file not found");
        }


    }

    public HashMap<String,String> getParams() { return this.params;}
}
