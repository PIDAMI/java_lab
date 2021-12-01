import com.java_polytech.pipeline_interfaces.RC;
import org.apache.log4j.*;

import java.io.FileInputStream;
import java.io.IOException;

public class MyLogger {
    private final org.apache.log4j.Logger logger;
    private final static String propertiesPath = "log.properties";
    private final static RC RC_LOGFILE_IO_EXCEPTION = new RC(
            RC.RCWho.UNKNOWN,
            RC.RCType.CODE_CUSTOM_ERROR,
            "couldn't open file for logging"
    );

    public MyLogger() throws IOException {
        initLogger();

        logger = Logger.getLogger(getClass().getName());


    }


    private void initLogger() throws IOException {

        PropertyConfigurator.configure("log4j.properties");

        //defaultInit();

    }

    private void defaultInit() throws IOException {
        FileAppender fileAppender = new FileAppender(
                new SimpleLayout(),
                "log.properties",
                true
        );

        ConsoleAppender consoleAppender = new ConsoleAppender(
                new SimpleLayout()
        );


        logger.addAppender(fileAppender);
        logger.addAppender(consoleAppender);
        info("logger properties file not found -" +
                " using default settings");
    }

    public void warning(String message){
        logger.warn(message);
    }
    public void info(String message){
        logger.info(message);
    }
    public void severe(String message){
        logger.fatal(message);
    }

}
