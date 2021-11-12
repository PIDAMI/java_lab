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

    public byte[] getBuffer() {
        return buffer;
    }


    public Reader(){

    }

    private static IConfigurable createConfigurable(String className, String configPath)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {

        Class<?> c = Class.forName(className);
        Constructor<?> cons = c.getConstructor();
        Object object = cons.newInstance();
        return (IConfigurable) object;
    }

    private static IReader setReader() {
        IReader reader;
        try {
            reader = (IReader) createConfigurable("Reader", "zxc.txt");
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return reader;
    }


    public static void main(String[] args) {

            IReader r = setReader();
            if (r==null)
                System.out.println("null reader");
            else {
                r.setConsumer(null);
                try {
                    r.setInputStream(new FileInputStream(new File("zxc.txt")));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                RC err = r.run();
                System.out.println(err.info);


            }


    }

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



    //
//    public ReturnCode SetPath(FileInputStream file){
//        this.inputStream = file;
//        this.validPath = true;
//        this.errorState = ReturnCode.SUCCESS;
//        return this.errorState;
//    }
//
//    public ReturnCode SetBuffer(int bufferSize){
//        if (bufferSize < 1){
//            System.out.println("Buffer size must be a positive integer");
//            this.errorState = ReturnCode.INVALID_BUFFER_SIZE;
//        } else {
//            buffer = new byte[bufferSize];
//            this.validBuffer = true;
//            this.errorState = ReturnCode.SUCCESS;
//        }
//        return this.errorState;
//    }
//
//    public boolean isValidInitialization(){
//        boolean validInit = this.validBuffer && this.validPath;
//        if (!validInit){
//            this.errorState = ReturnCode.INVALID_INITIALIZATION;
//        }
//        return validInit;
//    }

//    public int ReadBatch(){
//
//        int nonEmptyBufferSize = 0;
//        try {
//            nonEmptyBufferSize = inputStream.read(buffer,0,buffer.length);
////            if (nonEmptyBufferSize == -1){ // if reached file's end
////                nonEmptyBufferSize = 0;
////            }
//            this.errorState = ReturnCode.SUCCESS;
//        } catch (IOException e) {
//            System.out.println("Error occurred while reading file");
//            this.errorState = ReturnCode.READ_ERROR;
//            nonEmptyBufferSize = 0; // in both scenarios return 0 as it doesnt matter
//
//        }
//
//        return nonEmptyBufferSize;
//    }
}
