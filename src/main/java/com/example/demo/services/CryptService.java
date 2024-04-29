package com.example.demo.services;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptService {

    private static final Logger logger = LoggerFactory.getLogger(CryptService.class);

    private static final String AES_ALGORITHM = "AES";
    private static final String key = "Clau privada1234";

    // Método para cifrar una cadena con AES
    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            logger.error("Error encriptant la contrassenya");
            return null;
        }
    }

    // Método para descifrar una cadena cifrada con AES
    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes);
        } catch (Exception e) {
            logger.error("Error desencriptant la contrassenya");
            return null;
        }
    }
}
