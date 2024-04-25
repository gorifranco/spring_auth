package com.example.demo.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(ConfigManager.getString("mail_host")); // Configura el servidor SMTP adecuado
        mailSender.setPort(Integer.parseInt(ConfigManager.getString("mail_port"))); // Configura el puerto SMTP adecuado
        mailSender.setUsername(ConfigManager.getString("mail_user")); // Configura tu dirección de correo electrónico
        mailSender.setPassword(ConfigManager.getString("mail_password")); // Configura la contraseña de tu correo electrónico

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", ConfigManager.getString("mail_protocol"));
        props.put("mail.smtp.auth", ConfigManager.getString("mail_auth") == "yes" ? "true" : "false");
        props.put("mail.smtp.starttls.enable", ConfigManager.getString("mail_tls") == "yes" ? "true" : "false");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
