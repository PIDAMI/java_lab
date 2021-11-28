import com.java_polytech.pipeline_interfaces.*;

import java.util.Arrays;
import java.util.List;


public class AbstractGrammar {

    private final String[] tokens;
    //    AbstractGrammar(String[] tokens){this.tokens=tokens;}
//    String[] getTokens() {return this.tokens;}
    public final static String DEMILIMITER = "=";


    private final RC grammarError;
    private final RC noFileError;
    private final RC incompleteConfigError;

    protected AbstractGrammar(String[] tokens, RC grammarError,
                              RC noFileError, RC incompleteConfigError) {
        this.tokens = tokens;
        this.grammarError = grammarError;
        this.noFileError = noFileError;
        this.incompleteConfigError = incompleteConfigError;
    }

    public boolean isValidToken(String val) {return Arrays.asList(tokens).contains(val);}
    public final int getNumTokens() {
        return this.tokens == null ? 0 : this.tokens.length;
    }


    // specific error code for every executor's grammatic error
    public RC getGrammarErrorCode() { return grammarError;};

    // same thing for case when config file not found
    // might not be the most appropriate place for this method to be
    // but convenient, so it's in grammar
    public RC getNoFileErrorCode() { return noFileError;};


    // if config doesnt contain all needed parameters
    public RC getIncompleteConfigErrorCode(){return incompleteConfigError;};

}
