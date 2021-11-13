
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import com.java_polytech.pipeline_interfaces.*;

public class Writer implements IWriter{



    private OutputStream outputStream;
    private Config cnfg;
    private final AbstractGrammar grammar = new WriterGrammar();

    private final static RC RC_WRITER_CLOSE_STREAM_ERROR =  new RC(
            RC.RCWho.WRITER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Writer couldn't close stream.");

    @Override
    public RC setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC setConfig(String path) {
        this.cnfg = new Config(grammar);
        RC err = this.cnfg.ParseConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return RC.RC_SUCCESS;
    }

    @Override
    public RC consume(byte[] bytes) {
        if (bytes == null)
            return CloseStream();
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            return RC.RC_WRITER_FAILED_TO_WRITE;
        }

        return RC.RC_SUCCESS;
    }

    private RC CloseStream(){
        RC err;
        try {
            outputStream.close();
           err = RC.RC_SUCCESS;
        } catch (IOException e) {
            err = RC_WRITER_CLOSE_STREAM_ERROR;
        }
        return err;
    }



}
