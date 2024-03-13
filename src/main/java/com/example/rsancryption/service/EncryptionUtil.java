package com.example.rsancryption.service;

import com.example.rsancryption.mode.KycDetails;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
public class EncryptionUtil {

    private final RestTemplate restTemplate;

    @Autowired
    public EncryptionUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String encryptMessage(KycDetails kycDetails) {
        try {
            // Retrieve JWT token
            ResponseEntity<Map> tokenResponse = getTokenResponse();
            if (tokenResponse.getStatusCode() != HttpStatus.OK) {
                System.err.println("Failed to retrieve token: " + tokenResponse.getStatusCode());
                return null;
            }

            // Extract public key from token
            String accessToken = "Bearer " + Objects.requireNonNull(tokenResponse.getBody()).get("access_token");
            String publicKeyString = extractPublicKeyFromToken(accessToken);
            PublicKey publicKey = getPublicKeyFromString(publicKeyString);

            // Encrypt the PAN and DOB separately using RSA algorithm
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedPAN = cipher.doFinal(kycDetails.getPan_details().getPan().getBytes());
            byte[] encryptedDOB = cipher.doFinal(kycDetails.getPan_details().getDob().getBytes());

            // Encode the encrypted PAN and DOB as Base64 strings and return them
            String encryptedPANBase64 = Base64.getEncoder().encodeToString(encryptedPAN);
            String encryptedDOBBase64 = Base64.getEncoder().encodeToString(encryptedDOB);

            // Return the encrypted PAN and DOB as a single string in a structured format
            return "{\"encrypted_pan\": \"" + encryptedPANBase64 + "\", \"encrypted_dob\": \"" + encryptedDOBBase64 + "\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResponseEntity<Map> getTokenResponse() throws UnsupportedEncodingException {
        // Prepare token request parameters
        String tokenRequest = "grant_type=" + URLEncoder.encode("password", "UTF-8") +
                "&username=" + URLEncoder.encode("Spectrum_Ins", "UTF-8") +
                "&password=" + URLEncoder.encode("tnKyq5YcDLES6pA", "UTF-8") +
                "&scope=" + URLEncoder.encode("esb-kyc", "UTF-8") +
                "&client_id=" + URLEncoder.encode("Spectrum_Ins", "UTF-8") +
                "&client_secret=" + URLEncoder.encode("T6ksEW6Yd9dHnx8TzSRajc2x", "UTF-8");

        // Prepare HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Make token request
        HttpEntity<String> requestEntity = new HttpEntity<>(tokenRequest, headers);
        return restTemplate.postForEntity("https://ilesbsanity.insurancearticlez.com/cerberus/connect/token", requestEntity, Map.class);
    }

    private String extractPublicKeyFromToken(String accessToken) throws UnsupportedEncodingException {
        String payload = accessToken.split("\\.")[1];
        Gson gson = new Gson();
        String json = new String(Base64.getDecoder().decode(payload), "UTF-8");
        JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
        return jsonObject.get("pbk").getAsString();
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
