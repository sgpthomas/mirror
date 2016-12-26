package com.sgpthomas.mirror;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class EncryptionView extends AppCompatActivity {

    private String key;

    public final static String ENCRYPTION_KEY = "com.sgpthomas.mirror.ENCRYPTION_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption_view);

        Intent intent = getIntent();
        key = intent.getStringExtra(QrCodeReader.KEY_DATA);

        TextView textView = (TextView) findViewById(R.id.salt);
        textView.setText(key);

    }

    public void startEncrypt(View view) {

        Intent intent = new Intent(this, ConnectServer.class);
        intent.putExtra(ENCRYPTION_KEY, key);
        startActivity(intent);
    }
}
