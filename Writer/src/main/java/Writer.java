import java.io.IOException;
import java.io.OutputStream;

import com.java_polytech.pipeline_interfaces.*;

public class Writer implements IWriter{



    private final static TYPE[] SUPPORTED_TYPES =
            new TYPE[]{TYPE.CHAR_ARRAY, TYPE.BYTE_ARRAY, TYPE.INT_ARRAY};
    private TYPE commonType;

    private IMediator executorMediator;
    private OutputStream outputStream;
    private Config cnfg;
    private final BaseGrammar grammar = new WriterGrammar();

    private byte[] convertFromCommonToByte(Object data){
        if (data == null)
            return null;

        byte[] res = null;
        if (commonType == TYPE.BYTE_ARRAY)
            res = (byte[])data;
        else if (commonType == TYPE.CHAR_ARRAY)
            res = Caster.charsToBytes((char[]) data,
                    ((char[])data).length);
        else
            res = Caster.intsToBytes((int[]) data,
                    ((int[])data).length);
        return res;
    }

    @Override
    public RC setProvider(IProvider provider) {
        commonType = (Caster.getCommonTypes(
                provider.getOutputTypes(),
                SUPPORTED_TYPES));
        if (commonType == null){
            return RC.RC_WRITER_TYPES_INTERSECTION_EMPTY_ERROR;
        }
        executorMediator = provider.getMediator(commonType);
        return RC.RC_SUCCESS;
    }

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
    public RC consume() {
        byte[] toWrite = convertFromCommonToByte(
                executorMediator.getData()
        );
        if (toWrite != null){
            try {
                outputStream.write(toWrite,0,toWrite.length);
            } catch (IOException e) {
                return RC.RC_WRITER_FAILED_TO_WRITE;
            }
        }
        return RC.RC_SUCCESS;
    }


}
