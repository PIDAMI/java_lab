package com.company;
import com.company.Reader;
import com.company.Writer;
import com.company.Config;

import java.util.HashMap;


public class Executor {

    public enum Action {
        COMPRESS,
        DECOMPRESS
    }

    private Reader reader;
    private Writer writer;
    private Config confg;
    private final Grammar confgGrammar = new Grammar(new String[]{"BUFFER_SIZE", "OUTPUT_FILE","INPUT_FILE","ACTION"});
    private Action action;

    HashMap<String,String> SemanticConfigParse(Config confg, Grammar grammar){
        HashMap<String,String> params = new HashMap<>();
        return params;
    }

    public Executor(Config confg){


    }


}
