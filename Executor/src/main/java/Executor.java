import com.java_polytech.pipeline_interfaces.*;

import java.util.Arrays;

public class Executor implements IExecutor {

    private final static TYPE[] SUPPORTED_TYPES = new TYPE[]{
            TYPE.BYTE_ARRAY,
            TYPE.CHAR_ARRAY };
    private IMediator readerMediator;
    private byte[] buffer;
    private TYPE commonType;


    private byte[] convertFromCommonToByte(Object data){
        if (data == null)
            return null;
        byte[] res = null;
        if (commonType == TYPE.BYTE_ARRAY)
            res = (byte[])data;
        else
            res = Caster.charsToBytes((char[]) data,
                    ((char[])data).length);
        return res;
    }


    @Override
    public RC setProvider(IProvider provider) {
        commonType = (Caster.getCommonTypes(
                provider.getOutputTypes(),
                SUPPORTED_TYPES));
        if (commonType == null){
            return RC.RC_EXECUTOR_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        readerMediator = provider.getMediator(commonType);
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
        buffer = convertFromCommonToByte(readerMediator.getData());
        if (buffer != null){
            for (int i = 0; i < buffer.length; ++i){
                buffer[i] = table.get(buffer[i]);
            }
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
        return Arrays.copyOf(SUPPORTED_TYPES,SUPPORTED_TYPES.length);
    }

    public class ByteMediator implements IMediator{
        @Override
        public Object getData() {
            if (buffer == null)
                return null;
            return Arrays.copyOf(buffer,buffer.length);
        }
    }

    public class CharMediator implements IMediator{
        @Override
        public Object getData() {
            return Caster.bytesToChars(buffer,buffer.length);
        }
    }


    @Override
    public IMediator getMediator(TYPE type) {
        IMediator result = null;
        switch (type){
            case CHAR_ARRAY: result = new CharMediator(); break;
            case BYTE_ARRAY: result = new ByteMediator(); break;
        }
        return result;
    }


}
