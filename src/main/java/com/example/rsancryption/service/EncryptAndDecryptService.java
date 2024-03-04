package com.example.rsancryption.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Service
public class EncryptAndDecryptService {
    String tokenUrl = "https://ilesbsanity.insurancearticlez.com/cerberus/connect/token";
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

}
