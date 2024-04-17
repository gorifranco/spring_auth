package com.example.demo.services;

import com.example.demo.config.ConfigManager;
import com.example.demo.email.EmailServiceImpl;
import com.example.demo.models.EmailDetails;

public class CustomEmailManager {


    public static void sendMail(String body){
        EmailServiceImpl esi = new EmailServiceImpl();
        EmailDetails ed = new EmailDetails();
        ed.setRecipient(ConfigManager.getString("mail_target_username"));
        ed.setSubject(ConfigManager.getString("mail_subject"));
        ed.setMsgBody(body);

        esi.sendSimpleMail(ed);
    }
}
