import com.java_polytech.pipeline_interfaces.RC;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;


public class Main {


    public static void main(String[] args) {



        if (args.length != 1){
            System.out.println("Invalid amount of command-line arguments - " +
                    "must be 1");
       } else {
            try {
                MyLogger logger = new MyLogger();
                Manager m = new Manager(logger);
                RC err = m.BuildPipeline(args[0]);
                if (!err.equals(RC.RC_SUCCESS)){
                    logger.severe(err.info);
                    System.out.println(err.info);
                } else {
                    System.out.println("Successfully finished processing");
                }

            } catch (IOException e) {
                System.out.println("Can't open log file");
            }
        }
    }
}
