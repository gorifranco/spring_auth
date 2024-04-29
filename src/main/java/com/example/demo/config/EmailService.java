package com.example.demo.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

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
        javaMailSenderImpl.setPassword(ConfigManager.getString("mail_password"));

        // Configuración adicional, si es necesario
        Properties props = javaMailSenderImpl.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");
    }

    public static void sendEmail(String body) throws MessagingException {
        if(javaMailSenderImpl == null) createJavaMailService();

        MimeMessage message = javaMailSenderImpl.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(ConfigManager.getString("mail_target"));
        helper.setSubject(ConfigManager.getString("mail_subject"));
        helper.setText(body, true);
        
        javaMailSenderImpl.send(message);
    }
}