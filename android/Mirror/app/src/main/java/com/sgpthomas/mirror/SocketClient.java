package com.sgpthomas.mirror;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Queue;

import javax.net.SocketFactory;

/**
 * Created by samthomas on 9/21/16.
 */

public class SocketClient extends Thread {

    private String host;
    private int port;
    private Resources resources;
    private Queue<String> queue;

    private SocketFactory socketFactory;
    private Socket socket;

    private BufferedWriter bufferedWriter;

     public SocketClient(String host, int port, Resources resources, Queue<String> queue) {
         this.host = host;
         this.port = port;
         this.resources = resources;
         this.queue = queue;
     }

    @Override
    public void run() {
        this.connect(this.host, this.port);

        while (true) {
            if (!queue.isEmpty()) {
                String message = queue.remove();
                this.send(message);
            }
        }
    }

    public void send(String message) {
        if (this.isAlive() && bufferedWriter != null) {
            try {
                bufferedWriter.write(message + "\n\n");
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void connect(String host, int port) {
        try {
            socketFactory = SocketFactory.getDefault();
            socket = socketFactory.createSocket();
            socket.connect(new InetSocketAddress(host, port), 5000);

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            OutputStream outputStream = socket.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            Log.d("Mirror", "Connected to Server");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
