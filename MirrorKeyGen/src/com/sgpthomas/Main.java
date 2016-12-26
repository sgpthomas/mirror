package com.sgpthomas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.crypto.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String filename = readLine("Enter Filename");

        SecretKey key = generateKey();
        System.out.println("Generated Key: " + Base64.getEncoder().encodeToString(key.getEncoded()));

        char[] password = readPassword("Enter a password for KeyStore");
        saveKey(key, password, filename);

        System.out.println("Generating QR Code");
        generateQRCode(key.getEncoded(), filename);

        System.out.println("Generation Complete");
    }

    /* Generate Functions */
    private static SecretKey generateKey() {

        SecretKey key;

        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            key = keyGen.generateKey();

            return key;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void saveKey(SecretKey key, char[] password, String filename) {
        try {

            // open the keystore
            KeyStore ks = KeyStore.getInstance("JCEKS");

            ks.load(null, password);

            // save key to the keystore
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(password);

            KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(key);
            ks.setEntry("secretKey", skEntry, protParam);

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(filename + ".ks");
                ks.store(fos, password);
            } finally {
                if (fos != null)
                    fos.close();
            }

        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void generateQRCode(byte[] key, String filename) {
        String fileType = "png";
        int size = 250;

        File file = Paths.get(System.getProperty("user.dir"), filename + "." + fileType).toFile();

        try {
            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);

            hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix byteMatrix = qrCodeWriter.encode(Base64.getEncoder().encodeToString(key), BarcodeFormat.QR_CODE, size, size, hintMap);

            int width = byteMatrix.getWidth();
            BufferedImage image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
            image.createGraphics();

            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, width);
            graphics.setColor(Color.BLACK);

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < width; j++) {
                    if (byteMatrix.get(i, j)) {
                        graphics.fillRect(i, j, 1, 1);
                    }
                }
            }

            ImageIO.write(image, fileType, file);
        } catch (WriterException |IOException e) {
            e.printStackTrace();
        }
    }

    /* Reading Functions */
    private static String readLine(String message) {
        Console console = System.console();

        if (console != null) {
            return console.readLine(message + ": ");
        }

        System.out.print(message + ": ");
        return (new Scanner(System.in)).next();
    }

    private static char[] readPassword(String message) {
        Console console = System.console();

        if (console != null) {
            return console.readPassword(message + ": ");
        }

        return readLine(message).toCharArray();
    }
}
