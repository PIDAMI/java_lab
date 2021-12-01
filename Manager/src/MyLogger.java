import com.java_polytech.pipeline_interfaces.RC;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;

public class MyLogger {
    private final Logger logger;
    private final static String propertiesPath = "log.properties";
    private final static RC RC_LOGFILE_IO_EXCEPTION = new RC(
            RC.RCWho.UNKNOWN,
            RC.RCType.CODE_CUSTOM_ERROR,
            "Couldn't open file for logging"
    );
    private final static SimpleFormatter defaultFormatter =
            new SimpleFormatter() {
        private static final String format =
                "[%1$tF %1$tT] [%2$-7s] %3$s %n";

        @Override
        public synchronized String format(LogRecord lr) {
            return String.format(format,
                    new Date(lr.getMillis()),
                    lr.getLevel().getLocalizedName(),
                    lr.getMessage()
            );
        }
    };

    public MyLogger() throws IOException {
        logger = Logger.getLogger(getClass().getName());
        logger.setUseParentHandlers(false);
        initLogger();
    }


    private void initLogger() throws IOException {
        try {
            LogManager.getLogManager().readConfiguration(
                    new FileInputStream("log.properties")
            );
            info("logger initialized");
        } catch (IOException e) {
            defaultInit();
        }

    }

    private void defaultInit() throws IOException {
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.SEVERE);
        consoleHandler.setFormatter(defaultFormatter);
        logger.addHandler(consoleHandler);

        Handler fileHandler = new FileHandler("main.log",true);
        fileHandler.setLevel(Level.INFO);
        fileHandler.setFormatter(defaultFormatter);
        logger.addHandler(fileHandler);


        logger.setLevel(Level.INFO);
        warning("Logger properties file not found -" +
                " using default settings");
    }

    public void warning(String message){
        logger.warning(message);
    }
    public void info(String message){
        logger.info(message);
    }
    public void severe(String message){
        logger.severe(message);
    }

}
