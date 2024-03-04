package com.example.rsancryption;

import java.security.*;
import javax.crypto.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionUtils {
    private static final String PUBLIC_KEY_STRING = "-----BEGIN PUBLIC KEY-----\r\n" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTCpq86HNKMcWrrf4kJ/l1YBRd\r\n" +
            "yin4mVDrybj2xQ1APDM2ONJbEz6j6/mv/qcbFeJsq5fHOU7nXD6lF64yo2FWbXwp\r\n" +
            "4ImuJ4pi6GfQqlNLdrlax90QxvYXw776ZcWAkomrvSofikbOxwyoXhnxlpM9Bt+H\r\n" +
            "FBecYGEMNEvCMFdmawIDAQAB\r\n" +
            "-----END PUBLIC KEY-----";

    public static String encryptMessage(String plain) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            PublicKey publicKey = getPublicKeyFromString(PUBLIC_KEY_STRING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypt = cipher.doFinal(plain.getBytes());
            return Base64.getEncoder().encodeToString(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PublicKey getPublicKeyFromString(String keyString) throws Exception {
        String publicKeyPEM = keyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }

    public static void main(String[] args) {
        String plaintext = "02-10-1869";
        String encryptedMessage = encryptMessage(plaintext);
        System.out.println("Encrypted message: " + encryptedMessage);
    }
}
