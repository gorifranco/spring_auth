package com.example.demo.config;

import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.security.MessageDigest;
import java.util.Base64;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static JavaMailSenderImpl javaMailSenderImpl = null;

    private static void createJavaMailService() {
        javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost("smtp.serviciodecorreo.es");
        javaMailSenderImpl.setPort(Integer.parseInt(ConfigManager.getString("mail_port")));
        javaMailSenderImpl.setUsername(ConfigManager.getString("mail_user"));

        String encryptedPassword = ConfigManager.getString("mail_password");
        String decryptedPassword = decryptPassword(encryptedPassword);

        javaMailSenderImpl.setPassword(decryptedPassword);

        // Configuración adicional, si es necesario
        Properties props = javaMailSenderImpl.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");

        System.out.println("SMTP Host: " + javaMailSenderImpl.getHost());
        System.out.println("SMTP Port: " + javaMailSenderImpl.getPort());
        System.out.println("SMTP Username: " + javaMailSenderImpl.getUsername());
        System.out.println("Pass: " + javaMailSenderImpl.getPassword());
        System.out.println("props: " + javaMailSenderImpl.getJavaMailProperties().toString());
    }

    public static void sendEmail(String body) {
        try {
            if (javaMailSenderImpl == null)
                createJavaMailService();

            MimeMessage message = javaMailSenderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(ConfigManager.getString("mail_target"));
            helper.setSubject(ConfigManager.getString("mail_subject"));
            helper.setText(body, true);

            javaMailSenderImpl.send(message);
        } catch (MessagingException e) {
            logger.error("Error enviant el correu electronic: " + e.getMessage());
        }
    }
    private static String decryptPassword(String encryptedPassword) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
        byte[] hashedBytes = digest.digest(decodedBytes);
        return Base64.getEncoder().encodeToString(hashedBytes);
    } catch (NoSuchAlgorithmException e) {
        // Manejar la excepción de algoritmo no encontrado
        e.printStackTrace();
        return null;
    }
}
}