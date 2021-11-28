import com.java_polytech.pipeline_interfaces.*;
import com.sun.xml.internal.ws.policy.ComplexAssertion;

public class Executor implements IExecutor {


    private final static TYPE[] SUPPORTED_TYPES = new TYPE[]{
            TYPE.BYTE_ARRAY,
            TYPE.CHAR_ARRAY };
    private IMediator mediator;
    private byte[] buffer;
    private TYPE commonType;


    private void convertFromCommonToByte(Object data){
        if (commonType == TYPE.BYTE_ARRAY)
            buffer = (byte[])data;
        else
            buffer = Caster.charsToBytes((char[]) data, ((char[])data).length);
    }


    @Override
    public RC setProvider(IProvider provider) {
        commonType = (Caster.getCommonTypes(
                provider.getOutputTypes(),
                SUPPORTED_TYPES));
        if (commonType == null){
            return RC.RC_EXECUTOR_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        mediator = provider.getMediator(commonType);
        return RC.RC_SUCCESS;
    }


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
    private final BaseGrammar grammar = new ExecutorGrammar();
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
    public RC consume() {
        // only supported type is byte array,
        // so we can explicitly convert to bytes
        convertFromCommonToByte(mediator.getData());
        if (buffer == null){

        }

        byte[] result = new byte[buffer.length];
        for (int i = 0; i < buffer.length; ++i){
            result[i] = table.get(buffer[i]);
        }
        return consumer.consume();
    }

    @Override
    public RC setConsumer(IConsumer iConsumer) {
        this.consumer = iConsumer;
        return RC.RC_SUCCESS;
    }

    @Override
    public TYPE[] getOutputTypes() {
        return new TYPE[0];
    }

    @Override
    public IMediator getMediator(TYPE type) {
        return null;
    }


}
