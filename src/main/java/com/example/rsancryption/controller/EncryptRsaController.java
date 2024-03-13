package com.example.rsancryption.controller;

import com.example.rsancryption.EncryptionUtils;
import com.example.rsancryption.service.EncryptAndDecryptService;
import com.example.rsancryption.service.EncryptRsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RestController
public class EncryptRsaController {
    @Autowired
    private EncryptRsaService encryptRsaService;
    @Autowired
    private EncryptAndDecryptService encryptAndDecryptService;
    @Autowired
    private EncryptionUtils encryptionUtils;
    @PostMapping("/msg")
    public ResponseEntity<String> encryptMsg(@RequestBody String message) {
        String encryptedMessage = encryptionUtils.encryptMessage(message);
        if (encryptedMessage != null) {
            return ResponseEntity.ok(encryptedMessage);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Encryption failed");
        }
    }
    @GetMapping("/jwt/decode")
    public String decodeJwt() throws UnsupportedEncodingException {
        return encryptAndDecryptService.decodeJwt();
    }

    @GetMapping("/createKeys")
    public void createPrivatePublicKey() throws NoSuchAlgorithmException {
        encryptRsaService.createKeys();

    }

    @PostMapping("/encrypt")
    public String encryptMessage(@RequestBody String plain) {
        return encryptRsaService.encryptMessage(plain);

    }

    @PostMapping("/decrypt")
    public String decryptMessage(@RequestBody String encrypt) {
        return encryptRsaService.decryptMessage(encrypt);
    }
}
