package com.sgpthomas;

import com.sun.org.apache.xml.internal.security.Init;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Init.init();

        String filename = readLine("Enter Filename");
        Encryptor encryptor = new Encryptor(filename);

        Server server = new Server("192.168.0.26", 1111, encryptor);
        server.start();

    }

    /* Reading Functions */
    public static String readLine(String message) {
        Console console = System.console();

        if (console != null) {
            return console.readLine(message + ": ");
        }

        System.out.print(message + ": ");
        return (new Scanner(System.in)).next();
    }

    public static char[] readPassword(String message) {
        Console console = System.console();

        if (console != null) {
            return console.readPassword(message + ": ");
        }

        return readLine(message).toCharArray();
    }
}
