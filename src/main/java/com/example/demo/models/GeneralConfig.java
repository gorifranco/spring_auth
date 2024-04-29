package com.example.demo.models;

public class GeneralConfig {

    private String logs_path;

    private String mail_host;
    private String mail_port;
    private String mail_user;
    private String mail_password;
    private String mail_protocol;
    private String mail_tls;
    private String mail_auth;
    private String mail_target;
    private String mail_subject;

    
    public GeneralConfig(String logs_path, String mail_host, String mail_port, String mail_user, String mail_password,
            String mail_protocol, String mail_tls, String mail_auth, String mail_target, String mail_subject) {
        this.logs_path = logs_path;
        this.mail_host = mail_host;
        this.mail_port = mail_port;
        this.mail_user = mail_user;
        this.mail_password = mail_password;
        this.mail_protocol = mail_protocol;
        this.mail_tls = mail_tls;
        this.mail_auth = mail_auth;
        this.mail_target = mail_target;
        this.mail_subject = mail_subject;
    }

    public GeneralConfig() {
    }
    

    public String getMail_target() {
        return mail_target;
    }

    public void setMail_target(String mail_target) {
        this.mail_target = mail_target;
    }

    public String getMail_subject() {
        return mail_subject;
    }

    public void setMail_subject(String mail_subject) {
        this.mail_subject = mail_subject;
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
    public String getMail_port() {
        return mail_port;
    }
    public void setMail_port(String mail_port) {
        this.mail_port = mail_port;
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

    @Override
    public String toString() {
        return "GeneralConfig [logs_path=" + logs_path + ", mail_host=" + mail_host + ", mail_port=" + mail_port
                + ", mail_user=" + mail_user + ", mail_password=" + mail_password + ", mail_protocol=" + mail_protocol
                + ", mail_tls=" + mail_tls + ", mail_auth=" + mail_auth + ", mail_target=" + mail_target
                + ", mail_subject=" + mail_subject + "]";
    }
}