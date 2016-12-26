package com.sgpthomas;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

/**
 * Created by samthomas on 12/24/16.
 */
public class Encryptor {

    private SecretKey key;

    public Encryptor(String filename) {
        loadKeyStore(filename);
        System.out.println(Base64.getEncoder().encodeToString(key.getEncoded()));
    }

    private void loadKeyStore(String filename) {
        if (!(new File(filename + ".ks").exists())) {
            System.out.println(filename + " was not found!");
            System.exit(1);
        }

        try {
            char[] password = Main.readPassword("Enter KeyStore Password");
            KeyStore ks = KeyStore.getInstance("JCEKS");

            // load key store
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(filename + ".ks");
                ks.load(fis, password);
            } finally {
                if (fis != null)
                    fis.close();
            }

            // retrieve key from keystore
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);
            KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry) ks.getEntry("secretKey", protParam);
            key = skEntry.getSecretKey();

        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableEntryException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String message) {

        byte[] cipherText;
        try {
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS7Padding");

            byte[] messageBytes = message.getBytes();

            byte[] iv = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            aes.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

            cipherText = aes.doFinal(messageBytes);

            return Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(cipherText);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String decrypt(String message) {

        if (message.length() < 1) {
            return "No Message";
        }

        String[] parts = message.split(":");
        String decryptedMessage = "";

        try {
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");

            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] messageBytes = Base64.getDecoder().decode(parts[1].getBytes());

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            aes.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

            decryptedMessage = new String(aes.doFinal(messageBytes));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedMessage;
    }
}
