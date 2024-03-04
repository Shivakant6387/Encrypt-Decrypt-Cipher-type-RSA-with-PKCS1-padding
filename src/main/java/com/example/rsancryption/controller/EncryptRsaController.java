package com.example.rsancryption.controller;

import com.example.rsancryption.service.EncryptAndDecryptService;
import com.example.rsancryption.service.EncryptRsaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class EncryptRsaController {
    @Autowired
    private EncryptRsaService encryptRsaService;
    @Autowired
    private EncryptAndDecryptService encryptAndDecryptService;

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
