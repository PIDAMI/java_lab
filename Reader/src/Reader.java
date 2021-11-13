import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

import com.java_polytech.pipeline_interfaces.*;

public class Reader implements IReader{

    private InputStream inputStream;
    private byte[] buffer;
    private IConsumer consumer;
    private Config cnfg;
    private final AbstractGrammar grammar = new ReaderGrammar();


    private final static RC RC_READER_CLOSE_STREAM_ERROR = new RC(RC.RCWho.READER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Reader couldn't close stream.");


    private RC CloseStream(){
        RC err;
        try {
            this.inputStream.close();
            err = RC.RC_SUCCESS;
        } catch (IOException e) {
            err = RC_READER_CLOSE_STREAM_ERROR;
        }
        return err;
    }

    @Override
    public RC setConfig(String cnfg) {
        this.cnfg = new Config(grammar);
        RC err = this.cnfg.ParseConfig(cnfg);
        if (err != RC.RC_SUCCESS){
            return err;
        }
        try{
            int szBuffer = Integer.parseInt(this.cnfg
                    .getParams()
                    .get(ReaderTokens.BUFFER_SIZE.toString()));
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
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC run() {
        int nonEmptyBufSize;
        if (inputStream == null)
            System.out.println("empty stream");
        do {
            try {
                nonEmptyBufSize = inputStream.read(buffer, 0, buffer.length);
                if (nonEmptyBufSize < 0)
                    break;
            } catch (IOException e) {
                return RC.RC_READER_FAILED_TO_READ;
            }
            byte[] data = Arrays.copyOf(buffer, nonEmptyBufSize);
            if (consumer == null)
                System.out.println("empty consumer");
            RC err = consumer.consume(data);
            if (!err.equals(RC.RC_SUCCESS))
                return err;
        } while (nonEmptyBufSize > 0);
        RC err = consumer.consume(null); // reached file's end
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        err = CloseStream();
        return err;
    }


}
