import com.java_polytech.pipeline_interfaces.IConsumer;
import com.java_polytech.pipeline_interfaces.IExecutor;
import com.java_polytech.pipeline_interfaces.RC;

public class Executor implements IExecutor {


    public enum Action{
        ENCODE,
        DECODE
    }


    private final static RC RC_INVALID_ACTION = new RC(RC.RCWho.EXECUTOR,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Invalid executor's action - can only be ENCODE or DECODE");


    private Config cnfg;
    private Action action;
    private String tablePath;
    private final SubstitutionTable table = new SubstitutionTable();
    private final AbstractGrammar grammar = new ExecutorGrammar();
    private IConsumer consumer;

    @Override
    public RC setConfig(String path) {
        this.cnfg = new Config(grammar);
        RC err = this.cnfg.ParseConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        // if we've made it to this stage of program
        // it's guaranteed config has all token values and nothing else
        for (ExecutorGrammar.ExecutorTokens token:
                ExecutorGrammar.ExecutorTokens.values()){
            switch (token){
                case ACTION:
                    try{
                        action = Action.valueOf(cnfg.get(token.toString()));
                    } catch (IllegalArgumentException e){
                        return RC_INVALID_ACTION;
                    }
                    break;
                case TABLE_PATH:
                    tablePath = cnfg.get(token.toString());
                    break;
            }
        }
        return table.loadTable(action,tablePath);
    }


    @Override
    public RC consume(byte[] bytes) {
        if (bytes == null)
            return consumer.consume(null);
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; ++i){
            result[i] = table.get(bytes[i]);
        }
        return consumer.consume(result);
    }

    @Override
    public RC setConsumer(IConsumer iConsumer) {
        this.consumer = iConsumer;
        return RC.RC_SUCCESS;
    }


}
