import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Manager implements IConfigurable {


    private final static RC RC_MANAGER_CLOSE_OUTSTREAM_ERROR =  new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Couldn't close output stream.");

    private final static RC RC_MANAGER_CLOSE_INSTREAM_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Couldn't close input stream.");

    private static final RC RC_MANAGER_READER_NAME_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Can't make instance of reader."
    );


    private static final RC RC_MANAGER_WRITER_NAME_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Can't make instance of writer."
    );





    private final MyLogger logger;

    private OutputStream outputStream;
    private InputStream inputStream;

    private String readerConfig;
    private String[] executorConfigs;
    private String writerConfig;

    private String readerName;
    private String[] executorNames;
    private String writerName;

    private IReader reader;
    private IExecutor[] executors;
    private IWriter writer;


    private BaseGrammar grammar = new ManagerGrammar();

    public Manager(MyLogger logger){
        this.logger = logger;
    }


    @Override
    public RC setConfig(String path) {


        logger.info("parsing manager config...");
        Config cnfg = new Config(grammar);
        RC err = cnfg.ParseConfig(path);
        if (err != RC.RC_SUCCESS){
            logger.severe(err.info);
            return err;
        }

        // if we've made it to this stage of program
        // it's guaranteed config has all token values and nothing else
        for (ManagerGrammar.ManagerTokens token:
                ManagerGrammar.ManagerTokens.values()){

            String[] tokenValues = cnfg.get(token.toString());
            if (token.hasSingleValue && tokenValues.length != 1){
                err = RC.RC_MANAGER_CONFIG_GRAMMAR_ERROR;
                logger.severe(err.info);
                return err;
            }

            switch (token){
                case READER_NAME:
                    this.readerName = tokenValues[0];
                    break;
                case WRITER_NAME:
                    this.writerName = tokenValues[0];
                    break;
                case EXECUTOR_NAMES:
                    this.executorNames = tokenValues;
                    break;
                case INPUT_FILE:
                    try {
                        this.inputStream = new FileInputStream(tokenValues[0]);
                    } catch (FileNotFoundException e) {
                        return RC.RC_MANAGER_INVALID_INPUT_FILE;
                    }
                    break;
                case OUTPUT_FILE:
                    try {
                        this.outputStream = new FileOutputStream(tokenValues[0]);
                    } catch (FileNotFoundException e) {
                        return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
                    }
                    break;
                case READER_CONFIG_FILE:
                    this.readerConfig = tokenValues[0];
                    break;
                case WRITER_CONFIG_FILE:
                    this.writerConfig = tokenValues[0];
                    break;
                case EXECUTOR_CONFIG_FILES:
                    this.executorConfigs = tokenValues;
                    break;
            }
        }
        return RC.RC_SUCCESS;
    }


    private IConfigurable createConfigurable(String className)
            throws ClassNotFoundException, NoSuchMethodException,
            InvocationTargetException, InstantiationException, IllegalAccessException {


        Class<?> c = Class.forName(className);
        Constructor<?> cons = c.getConstructor();
        return (IConfigurable) cons.newInstance();

    }

    private RC setReader(){

        RC err = reader.setConfig(readerConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        err = reader.setInputStream(inputStream);
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        reader.setConsumer(executors[0]);
        return err;
//        return reader.setConsumer(executors[0]);

    }

    private RC setWriter(){

        RC err = writer.setConfig(writerConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        err = writer.setOutputStream(outputStream);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
//        return writer.setProvider(executors[executors.length-1]);
        return err;
    }

    private RC setExecutors(){

        RC err = RC.RC_SUCCESS;
        if (executorNames.length != executorConfigs.length)
            return RC.RC_MANAGER_CONFIG_SEMANTIC_ERROR;

        for (int i = 0; i < executors.length; i++){
            err = executors[i].setConfig(executorConfigs[i]);
            if (!err.equals(RC.RC_SUCCESS))
                return err;

            if (i == executors.length - 1)
                err = executors[i].setConsumer(writer);
            else
                err = executors[i].setConsumer(executors[i+1]);

            if (!err.equals(RC.RC_SUCCESS))
                return err;

//            if (i == 0)
//                err = executors[i].setProvider(reader);
//            else
//                err = executors[i].setProvider(executors[i-1]);
//
//            if (!err.equals(RC.RC_SUCCESS))
//                return err;
        }


        return err;
    }


    public RC CloseStreams(){
        RC err = RC.RC_SUCCESS;
        try {
            inputStream.close();
        } catch (IOException e) {
            err = RC_MANAGER_CLOSE_INSTREAM_ERROR;
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            err = RC_MANAGER_CLOSE_OUTSTREAM_ERROR;
        }
        return err;
    }

    public RC BuildPipeline(String path) {

        RC err = setConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        logger.info("Creating pipeline components...");
        try {
            reader = (IReader) createConfigurable(readerName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            err = RC_MANAGER_READER_NAME_ERROR;
            logger.severe(err.info);
            return err;
        }

        executors = new IExecutor[executorNames.length];
        for (int i = 0; i < executorNames.length; i++){
            try {
                executors[i] = (IExecutor) createConfigurable(executorNames[i]);
            } catch (ClassNotFoundException | NoSuchMethodException |
                    InvocationTargetException | InstantiationException |
                    IllegalAccessException e) {
                err = new RC(RC.RCWho.MANAGER, RC.RCType.CODE_CUSTOM_ERROR,
                        "Can't make instance of executor named " + executorNames[i]);
                return err;
            }
        }


        try {
            writer = (IWriter) createConfigurable(writerName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            return RC_MANAGER_WRITER_NAME_ERROR;
        }



        err = setReader();
        if (!err.equals(RC.RC_SUCCESS))
            return err;



        err = setExecutors();
        if (!err.equals(RC.RC_SUCCESS))
            return err;



        err = setWriter();
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        logger.info("Starting processing...");
        err = reader.run();
        if (err == RC.RC_SUCCESS)
            logger.info("Successfully finished processing");

        return err;
    }

}
