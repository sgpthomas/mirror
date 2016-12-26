package com.sgpthomas.mirror;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ConnectServer extends AppCompatActivity {

    private SocketClient client;
    private Queue<String> queue;

    private byte[] encryptionKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_server);

        encryptionKey = Base64.decode(getIntent().getStringExtra(EncryptionView.ENCRYPTION_KEY), Base64.DEFAULT);
    }

    public void connectServer(View view) {
        EditText editHost = (EditText) findViewById(R.id.edit_host);
        EditText editPort = (EditText) findViewById(R.id.edit_port);

        String host = "";
        int port = 0;

        try {
            host = editHost.getText().toString();
            port = Integer.parseInt(editPort.getText().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("Mirror", "Host: " + host + ", Port: " + port);

        queue = new PriorityQueue<>();
        client = new SocketClient(host, port, getResources(), queue);
        client.start();
    }

    public void sendMessage(View view) {
        EditText editMessage = (EditText) findViewById(R.id.message);

        String message = editMessage.getText().toString();
        editMessage.setText("");

        Log.d("Mirror", message);
        queue.add(encrypt(message));
    }

    private String encrypt(String message) {

        byte[] cipherText;
        byte[] iv = new byte[16];
        String garbled = "";

        try {
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS7Padding");

            byte[] messageBytes = message.getBytes();

            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKey, "AES");
            aes.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

            cipherText = aes.doFinal(messageBytes);
            garbled = Base64.encodeToString(iv, Base64.DEFAULT) + ":" + Base64.encodeToString(cipherText, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return garbled;
    }

    private String decrypt(String cipher) {

        String[] parts = cipher.split(":");
        String message = "";

        try {
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = Base64.decode(parts[0], Base64.DEFAULT);
            byte[] messageBytes = Base64.decode(parts[1].getBytes(), Base64.DEFAULT);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(encryptionKey, "AES");
            aes.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            message = new String(aes.doFinal(messageBytes));
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(message);
        return message;
    }
}
