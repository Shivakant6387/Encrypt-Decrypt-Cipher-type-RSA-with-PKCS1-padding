package com.example.rsancryption;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import javax.crypto.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
public class EncryptionUtils {
    static String tokenUrl = "https://ilesbsanity.insurancearticlez.com/cerberus/connect/token";

    @Autowired
    private RestTemplate restTemplate;

    public ResponseEntity<Map> tokenResponse() {
        String tokenRequest = null;
        try {
            tokenRequest = "grant_type=" + URLEncoder.encode("password", "UTF-8") +
                    "&username=" + URLEncoder.encode("Spectrum_Ins", "UTF-8") +
                    "&password=" + URLEncoder.encode("tnKyq5YcDLES6pA", "UTF-8") +
                    "&scope=" + URLEncoder.encode("esb-kyc", "UTF-8") +
                    "&client_id=" + URLEncoder.encode("Spectrum_Ins", "UTF-8") +
                    "&client_secret=" + URLEncoder.encode("T6ksEW6Yd9dHnx8TzSRajc2x", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HttpHeaders headerToken = new HttpHeaders();
        headerToken.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> requestEntity = new HttpEntity<>(tokenRequest, headerToken);
        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, requestEntity, Map.class);

        if (tokenResponse.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(tokenResponse.getStatusCode()).build();
        }

        return ResponseEntity.ok(tokenResponse.getBody());
    }

    public String encryptMessage(String plain) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            String accessToken = "Bearer " + Objects.requireNonNull(tokenResponse().getBody()).get("access_token");
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String payload=accessToken.split("\\.")[1];
            Gson gson=new Gson();
            String json=new String(org.apache.commons.codec.binary.Base64.decodeBase64(payload),"UTF-8");
            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
            String pbk = jsonObject.get("pbk").getAsString();
            PublicKey publicKey = getPublicKeyFromString(pbk);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypt = cipher.doFinal(plain.getBytes());
            return Base64.getEncoder().encodeToString(encrypt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PublicKey getPublicKeyFromString(String keyString) throws Exception {
        String publicKeyPEM = keyString
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PUBLIC KEY-----", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }
}
