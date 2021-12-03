import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;


public class Reader implements IReader{


    private final static TYPE[] SUPPORTED_TYPES =
            new TYPE[]{TYPE.CHAR_ARRAY, TYPE.BYTE_ARRAY, TYPE.INT_ARRAY};


    private InputStream inputStream;
    private byte[] buffer;
    private IConsumer consumer;
    private Config cnfg;
    private int nonEmptyBufSize;
    private final BaseGrammar grammar = new ReaderGrammar();

    private final static RC RC_READER_CLOSE_STREAM_ERROR = new RC(RC.RCWho.READER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Reader couldn't close stream.");


    public class ByteMediator implements IMediator{
        @Override
        public Object getData() {
            // means reached file's end
            if (nonEmptyBufSize < 0)
                return null;
            else
                return Arrays.copyOf(buffer,nonEmptyBufSize);
        }
    }

    public class IntMediator implements IMediator{
        @Override
        public Object getData() {
            // means reached file's end
            if (nonEmptyBufSize < 0)
                return null;
            return Caster.bytesToInts(buffer,nonEmptyBufSize);
        }
    }

    public class CharMediator implements IMediator{
        @Override
        public Object getData() {
            // means reached file's end
            if (nonEmptyBufSize < 0)
                return null;
            return Caster.bytesToChars(buffer,nonEmptyBufSize);
        }
    }


    @Override
    public RC setConfig(String cnfg) {
        this.cnfg = new Config(grammar);
        RC err = this.cnfg.ParseConfig(cnfg);
        if (err != RC.RC_SUCCESS){
            return err;
        }
        try{
            String[] bufferSizeParams = this.cnfg.get(ReaderGrammar.ReaderTokens.BUFFER_SIZE.toString());
            if (bufferSizeParams == null || bufferSizeParams.length != 1)
                return RC.RC_READER_CONFIG_GRAMMAR_ERROR;

            int szBuffer = Integer.parseInt(bufferSizeParams[0]);
            if (szBuffer < 1)
                return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
            buffer = new byte[szBuffer];
        } catch (NumberFormatException e){
            return RC.RC_READER_CONFIG_SEMANTIC_ERROR;
        }
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConsumer(IConsumer consumer) {
        this.consumer = consumer;
        return this.consumer.setProvider(this);
    }

    @Override
    public TYPE[] getOutputTypes() {
        return Arrays.copyOf(SUPPORTED_TYPES, SUPPORTED_TYPES.length);
    }


    @Override
    public IMediator getMediator(TYPE type) {
        IMediator result = null;
        switch (type){
            case CHAR_ARRAY: result = new CharMediator(); break;
            case BYTE_ARRAY: result = new ByteMediator(); break;
            case INT_ARRAY: result = new IntMediator(); break;
        }
        return result;
    }

    @Override
    public RC setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC run() {
        do {
            try {
                nonEmptyBufSize = inputStream.read(buffer, 0, buffer.length);
                if (nonEmptyBufSize < 0)
                    break;
            } catch (IOException e) {
                return RC.RC_READER_FAILED_TO_READ;
            }

            if (consumer == null)
                System.out.println("NULL CONSUMER IN READER");
            RC err = consumer.consume();
            if (!err.equals(RC.RC_SUCCESS))
                return err;
        } while (nonEmptyBufSize > 0);

//            if (!err.equals(RC.RC_SUCCESS))
//            return err;
        return consumer.consume();
    }


}
