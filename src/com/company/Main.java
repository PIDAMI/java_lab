package com.company;
import com.company.Config;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;




public class Main {



    public static void main(String[] args) {

       if (args.length != 1){
            System.out.println("Invalid amount of command-line arguments: must be 1");
       } else {
            Config conf = new Config(args[0]);
            Manager m = new Manager(conf);
            m.Run();
        }

    }
}
