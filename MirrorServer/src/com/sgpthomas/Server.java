package com.sgpthomas;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;

/**
 * Created by samthomas on 12/24/16.
 */
public class Server extends Thread{

    private String address;
    private int port;
    private Encryptor encryptor;

    private ServerSocket serverSocket;
    private Socket socket = null;
    private InputStream inputStream;
    private Scanner scanner;

    public Server(String address, int port, Encryptor encryptor) {
        this.address = address;
        this.port = port;
        this.encryptor = encryptor;

//        initServerSocket();
    }

    @Override
    public void run() {
        connect();

        while (true) {
            String string = scanner.next().replace("\n", "");
            System.out.println(encryptor.decrypt(string));
        }
    }

//    private void initServerSocket() {
//        try {
//            serverSocket = new ServerSocket(0);
//            port = serverSocket.getLocalPort();
//
//            JmDNS jmdns = JmDNS.create();
//            System.out.println(port + " " + jmdns.);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void connect() {
        try {

            ServerSocketFactory ssf = ServerSocketFactory.getDefault();
            ServerSocket serverSocket = ssf.createServerSocket();

            InetSocketAddress inetSocket = new InetSocketAddress(address, port);
            serverSocket.bind(inetSocket);

            System.out.println("Starting Server: " + serverSocket.getLocalSocketAddress().toString());

            socket = serverSocket.accept();
            System.out.println("Connected to: " + socket.getRemoteSocketAddress().toString());

            inputStream = socket.getInputStream();
            scanner = new Scanner(inputStream);
            scanner.useDelimiter("\n\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Listener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }
}
