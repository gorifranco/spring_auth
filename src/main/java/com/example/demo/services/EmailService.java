package com.example.demo.services;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.example.demo.config.ConfigManager;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static JavaMailSenderImpl javaMailSenderImpl = null;

    private static void createJavaMailService() {
        javaMailSenderImpl = new JavaMailSenderImpl();
        javaMailSenderImpl.setHost(ConfigManager.getString("mail_host"));
        javaMailSenderImpl.setPort(Integer.parseInt(ConfigManager.getString("mail_port")));
        javaMailSenderImpl.setUsername(ConfigManager.getString("mail_user"));
        javaMailSenderImpl.setPassword(CryptService.decrypt(ConfigManager.getString("mail_password")));

        Properties props = javaMailSenderImpl.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.enable", "true");

    }

    public static void sendEmail(String body) {
        try {
            logger.info("Enviant correu d'estat a " + ConfigManager.getString("mail_target"));
            if (javaMailSenderImpl == null)
                createJavaMailService();

            MimeMessage message = javaMailSenderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(new InternetAddress(javaMailSenderImpl.getUsername()));
            helper.setTo(ConfigManager.getString("mail_target"));
            helper.setSubject(ConfigManager.getString("mail_subject"));
            helper.setText(body, true);

            javaMailSenderImpl.send(message);
        } catch (MessagingException e) {
            logger.error("Error enviant el correu electronic: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error 2 enviant el correu: " + e.getMessage());
        }
    }
}