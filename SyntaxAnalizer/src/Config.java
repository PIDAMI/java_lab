import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import com.java_polytech.pipeline_interfaces.*;

// syntax analyzer
public class Config {

    private final HashMap<String, String[]> params = new HashMap<>();
    private final BaseGrammar grammar;

    public Config(BaseGrammar grammar){
        this.grammar = grammar;
    }

    public RC ParseConfig(String path){
        try (Scanner scanner = new Scanner(new File(path))){
            String line;
            int numLines = 0;
            while (scanner.hasNext()){
                line = scanner.nextLine();
                if (line.startsWith(BaseGrammar.COMMENTARY_PREFIX))
                    continue;
                numLines++;
                String[] tokens = Arrays.stream(line
                                .split(BaseGrammar.DEMILIMITER))
                        .map(String::trim)
                        .toArray(String[]::new);
                if (tokens.length != 2)
                    return grammar.getGrammarErrorCode();
                if (grammar.isValidToken(tokens[0])){
                    String[] tokenValues = Arrays
                            .stream(tokens[1].split(BaseGrammar.TOKEN_VALUE_DELIMITER))
                            .map(String::trim)
                            .toArray(String[]::new);
                    this.params.put(tokens[0],tokenValues);
                }
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



    public String[] get(String key) { return params.get(key);}
}
