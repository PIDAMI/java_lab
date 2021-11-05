package com.company;


import com.java_polytech.pipeline_interfaces.RC;

public class Main {



    public static void main(String[] args) {

       if (args.length != 1){
            System.out.println("Invalid amount of command-line arguments: must be 1");
       } else {
            Manager m = new Manager();
            RC err = m.BuildPipeline(args[0]);
            if (!err.equals(RC.RC_SUCCESS))
                System.out.println(err.info);
        }
    }
}
