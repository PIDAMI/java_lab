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
    private boolean validConfg = true;

    public Config(String path){
        try (Scanner scanner = new Scanner(new File(path))){
            String line;
            int numLines = 0;
            while (scanner.hasNext()){
                line = scanner.nextLine().trim();
                numLines++;
                String[] pair = line.split(Grammar.DEMILIMITER);
                if (pair.length != 2){
                    System.out.println("Improper config format at line " + numLines + ". Must be <PARAMETER>=<VALUE>");
                    this.validConfg = false;
                    break;
                }
                this.params.put(pair[0],pair[1]);
            }

        } catch(FileNotFoundException noFileExc){
            System.out.println("Config file not found");
        }


    }

    public HashMap<String,String> getParams() { return this.params;}
}
