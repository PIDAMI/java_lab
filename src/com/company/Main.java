package com.company;
import com.company.Config;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void f() throws IOException {
        final byte mask = 120;
        final int sz = 10;
        byte k[] = new byte[sz];
        File f = new File("a.txt");
        FileInputStream inp = new FileInputStream(f);
        int szRead = inp.read(k,0,sz);

        inp.close();


        System.out.println(szRead);
        File out = new File("b.txt");
        FileOutputStream output = new FileOutputStream(out);

        for (int i = 0; i < szRead; i++){
            k[i] = (byte) (k[i] ^ mask);
        }
        output.write(k,0,szRead);
        output.close();


        File out2 = new File("c.txt");
        FileOutputStream output2 = new FileOutputStream(out2);

        for (int i = 0; i < szRead; i++){
            k[i] = (byte) (k[i] ^ mask);
        }
        output2.write(k,0,szRead);
        output2.close();




    }


    public static void main(String[] args) {

        Config c = new Config("config.txt");
        System.out.println(c.getParams().get("BUFFER_SIZE"));

//       if (args.length != 1){
//            System.out.println("Invalid amount of command-line arguments: must be 1");
//       }
//        else{
//            Config conf = new Config(args[0]);
//        }

    }
}
