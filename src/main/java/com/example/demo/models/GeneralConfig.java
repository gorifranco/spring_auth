package com.example.demo.models;

public class GeneralConfig {

    private String logs_path;

    private String mail_host;
    private String mails_port;
    private String mail_user;
    private String mail_password;
    private String mail_protocol;
    private String mail_tls;
    private String mail_auth;
    private String mail_target;
    private String maiL_subject;

    
    public GeneralConfig(String logs_path, String mail_host, String mails_port, String mail_user, String mail_password,
            String mail_protocol, String mail_tls, String mail_auth, String mail_target, String maiL_subject) {
        this.logs_path = logs_path;
        this.mail_host = mail_host;
        this.mails_port = mails_port;
        this.mail_user = mail_user;
        this.mail_password = mail_password;
        this.mail_protocol = mail_protocol;
        this.mail_tls = mail_tls;
        this.mail_auth = mail_auth;
        this.mail_target = mail_target;
        this.maiL_subject = maiL_subject;
    }

    public GeneralConfig() {
    }
    

    public String getMail_target() {
        return mail_target;
    }

    public void setMail_target(String mail_target) {
        this.mail_target = mail_target;
    }

    public String getMaiL_subject() {
        return maiL_subject;
    }

    public void setMaiL_subject(String maiL_subject) {
        this.maiL_subject = maiL_subject;
    }

    public String getLogs_path() {
        return logs_path;
    }
    public void setLogs_path(String logs_path) {
        this.logs_path = logs_path;
    }
    public String getMail_host() {
        return mail_host;
    }
    public void setMail_host(String mail_host) {
        this.mail_host = mail_host;
    }
    public String getMails_port() {
        return mails_port;
    }
    public void setMails_port(String mails_port) {
        this.mails_port = mails_port;
    }
    public String getMail_user() {
        return mail_user;
    }
    public void setMail_user(String mail_user) {
        this.mail_user = mail_user;
    }
    public String getMail_password() {
        return mail_password;
    }
    public void setMail_password(String mail_password) {
        this.mail_password = mail_password;
    }
    public String getMail_protocol() {
        return mail_protocol;
    }
    public void setMail_protocol(String mail_protocol) {
        this.mail_protocol = mail_protocol;
    }
    public String getMail_tls() {
        return mail_tls;
    }
    public void setMail_tls(String mail_tls) {
        this.mail_tls = mail_tls;
    }
    public String getMail_auth() {
        return mail_auth;
    }
    public void setMail_auth(String mail_auth) {
        this.mail_auth = mail_auth;
    }
    public String getMail_sender_username() {
        return mail_target;
    }
    public void setMail_sender_username(String mail_target) {
        this.mail_target = mail_target;
    }

}