import com.java_polytech.pipeline_interfaces.*;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class Manager implements IConfigurable {




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


    private static final RC RC_MANAGER_EXECUTOR_NAME_ERROR = new RC(
            RC.RCWho.MANAGER,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Can't make instance of executor."
    );


    private OutputStream outputStream;
    private InputStream inputStream;
    private String readerConfig;
    private String executorConfig;
    private String writerConfig;

    private String readerName;
    private String executorName;
    private String writerName;

    private IReader reader;
    private IExecutor executor;
    private IWriter writer;

    private BaseGrammar grammar = new ManagerGrammar();


    @Override
    public RC setConfig(String path) {
        Config cnfg = new Config(grammar);
        RC err = cnfg.ParseConfig(path);
        if (err != RC.RC_SUCCESS){
            return err;
        }

        // if we've made it to this stage of program
        // it's guaranteed config has all token values and nothing else
        for (ManagerGrammar.ManagerTokens token:
                ManagerGrammar.ManagerTokens.values()){
            switch (token){
                case READER_NAME:
                    this.readerName = cnfg.get(token.toString());
                    break;
                case WRITER_NAME:
                    this.writerName = cnfg.get(token.toString());
                    break;
                case EXECUTOR_NAME:
                    this.executorName = cnfg.get(token.toString());
                    break;
                case INPUT_FILE:
                    try {
                        this.inputStream = new FileInputStream(
                                cnfg.get(token.toString())
                        );
                    } catch (FileNotFoundException e) {
                        return RC.RC_MANAGER_INVALID_INPUT_FILE;
                    }
                    break;
                case OUTPUT_FILE:
                    try {
                        this.outputStream = new FileOutputStream(
                                cnfg.get(token.toString())
                        );
                    } catch (FileNotFoundException e) {
                        return RC.RC_MANAGER_INVALID_OUTPUT_FILE;
                    }
                    break;
                case READER_CONFIG_FILE:
                    this.readerConfig = cnfg.get(token.toString());
                    break;
                case WRITER_CONFIG_FILE:
                    this.writerConfig = cnfg.get(token.toString());
                    break;
                case EXECUTOR_CONFIG_FILE:
                    this.executorConfig = cnfg.get(token.toString());
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

        return reader.setConsumer(executor);
    }

    private RC setWriter(){

        RC err = this.writer.setConfig(this.writerConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return this.writer.setOutputStream(this.outputStream);
    }

    private RC setExecutor(){

        RC err = this.executor.setConfig(this.executorConfig);
        if (!err.equals(RC.RC_SUCCESS))
            return err;
        return this.executor.setConsumer(this.writer);
    }


    public RC BuildPipeline(String path) {

        RC err = setConfig(path);
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        try {
            reader = (IReader) createConfigurable(readerName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            return RC_MANAGER_READER_NAME_ERROR;
        }

        try {
            executor = (IExecutor) createConfigurable(executorName);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InvocationTargetException | InstantiationException |
                IllegalAccessException e) {
            return RC_MANAGER_EXECUTOR_NAME_ERROR;
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



        err = setExecutor();
        if (!err.equals(RC.RC_SUCCESS))
            return err;



        err = setWriter();
        if (!err.equals(RC.RC_SUCCESS))
            return err;

        return reader.run();
    }

}
