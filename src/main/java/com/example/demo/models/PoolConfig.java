package com.example.demo.models;

import java.io.Serializable;

public class PoolConfig implements Serializable {

    private String ddbb_in_url;
    private String ddbb_in_type;
    private String ddbb_in_user;
    private String ddbb_in_password;
    private String ddbb_in_port;
    private String ddbb_in_schema;
    private String ddbb_in_table;
    private String ddbb_in_columns;

    private String ddbb_out_url;
    private String ddbb_out_type;
    private String ddbb_out_user;
    private String ddbb_out_password;
    private String ddbb_out_port;
    private String ddbb_out_schema;
    private String ddbb_out_table;
    private String ddbb_out_columns;

    private boolean periodically_execution;
    private String time_interval;
    private String time_unit;

    private boolean send_mail;
    private String name;
    private String description;
    private String nextExecution;

    public PoolConfig() {
        this.ddbb_in_url = "";
        this.ddbb_in_type = "";
        this.ddbb_in_user = "";
        this.ddbb_in_password = "";
        this.ddbb_in_port = "";
        this.ddbb_in_schema = "";
        this.ddbb_in_table = "";
        this.ddbb_out_url = "";
        this.ddbb_out_type = "";
        this.ddbb_out_user = "";
        this.ddbb_out_password = "";
        this.ddbb_out_port = "";
        this.ddbb_out_schema = "";
        this.ddbb_out_table = "";
        this.periodically_execution = false;
        this.time_interval = "";
        this.time_unit = "";
        this.send_mail = false;
        this.name = "";
        this.description = "";
        this.ddbb_in_columns = "";
        this.ddbb_out_columns = "";
        this.nextExecution = "";

    }

    public PoolConfig(String ddbb_in_url, String ddbb_in_type, String ddbb_in_user, String ddbb_in_password,
            String ddbb_in_port, String ddbb_in_schema, String ddbb_in_table, String ddbb_out_url, String ddbb_out_type,
            String ddbb_out_user, String ddbb_out_password, String ddbb_out_port, String ddbb_out_schema,
            String ddbb_out_table, boolean periodically_execution, String time_interval, String time_unit,
            boolean send_mail, String name, String description) {
        this.ddbb_in_url = ddbb_in_url;
        this.ddbb_in_type = ddbb_in_type;
        this.ddbb_in_user = ddbb_in_user;
        this.ddbb_in_password = ddbb_in_password;
        this.ddbb_in_port = ddbb_in_port;
        this.ddbb_in_schema = ddbb_in_schema;
        this.ddbb_in_table = ddbb_in_table;
        this.ddbb_out_url = ddbb_out_url;
        this.ddbb_out_type = ddbb_out_type;
        this.ddbb_out_user = ddbb_out_user;
        this.ddbb_out_password = ddbb_out_password;
        this.ddbb_out_port = ddbb_out_port;
        this.ddbb_out_schema = ddbb_out_schema;
        this.ddbb_out_table = ddbb_out_table;
        this.periodically_execution = periodically_execution;
        this.time_interval = time_interval;
        this.time_unit = time_unit;
        this.send_mail = send_mail;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void update(PoolConfig poolConfig) {
        this.ddbb_in_url = poolConfig.ddbb_in_url;
        this.ddbb_in_type = poolConfig.ddbb_in_type;
        this.ddbb_in_user = poolConfig.ddbb_in_user;
        this.ddbb_in_port = poolConfig.ddbb_in_port;
        this.ddbb_in_schema = poolConfig.ddbb_in_schema;
        this.ddbb_in_table = poolConfig.ddbb_in_table;
        this.ddbb_out_url = poolConfig.ddbb_out_url;
        this.ddbb_out_type = poolConfig.ddbb_out_type;
        this.ddbb_out_user = poolConfig.ddbb_out_user;
        this.ddbb_out_port = poolConfig.ddbb_out_port;
        this.ddbb_out_schema = poolConfig.ddbb_out_schema;
        this.ddbb_out_table = poolConfig.ddbb_out_table;
        this.periodically_execution = poolConfig.periodically_execution;
        this.time_interval = poolConfig.time_interval;
        this.time_unit = poolConfig.time_unit;
        this.send_mail = poolConfig.send_mail;
        this.name = poolConfig.name;
        this.description = poolConfig.description;
        this.ddbb_in_columns = poolConfig.ddbb_in_columns;
        this.ddbb_out_columns = poolConfig.ddbb_out_columns;
        this.nextExecution = poolConfig.nextExecution;

        if (poolConfig.ddbb_in_password != null && !poolConfig.ddbb_in_password.isEmpty())
            this.ddbb_in_password = poolConfig.ddbb_in_password;
        if (poolConfig.ddbb_out_password != null && !poolConfig.ddbb_out_password.isEmpty())
            this.ddbb_out_password = poolConfig.ddbb_out_password;
    }

    public String getDdbb_in_columns() {
        return ddbb_in_columns;
    }

    public void setDdbb_in_columns(String ddbb_in_columns) {
        this.ddbb_in_columns = ddbb_in_columns;
    }

    public String getDdbb_out_columns() {
        return ddbb_out_columns;
    }

    public void setDdbb_out_columns(String ddbb_out_columns) {
        this.ddbb_out_columns = ddbb_out_columns;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDdbb_in_table() {
        return ddbb_in_table;
    }

    public void setDdbb_in_table(String ddbb_in_table) {
        this.ddbb_in_table = ddbb_in_table;
    }

    public String getDdbb_out_table() {
        return ddbb_out_table;
    }

    public void setDdbb_out_table(String ddbb_out_table) {
        this.ddbb_out_table = ddbb_out_table;
    }

    public boolean getSend_mail() {
        return send_mail;
    }

    public void setSend_mail(boolean send_mail) {
        this.send_mail = send_mail;
    }

    public String getDdbb_in_url() {
        return ddbb_in_url;
    }

    public void setDdbb_in_url(String ddbb_in_url) {
        this.ddbb_in_url = ddbb_in_url;
    }

    public String getDdbb_in_type() {
        return ddbb_in_type;
    }

    public void setDdbb_in_type(String ddbb_in_type) {
        this.ddbb_in_type = ddbb_in_type;
    }

    public String getDdbb_in_user() {
        return ddbb_in_user;
    }

    public void setDdbb_in_user(String ddbb_in_user) {
        this.ddbb_in_user = ddbb_in_user;
    }

    public String getDdbb_in_password() {
        return ddbb_in_password;
    }

    public void setDdbb_in_password(String ddbb_in_password) {
        this.ddbb_in_password = ddbb_in_password;
    }

    public String getDdbb_in_port() {
        return ddbb_in_port;
    }

    public void setDdbb_in_port(String ddbb_in_port) {
        this.ddbb_in_port = ddbb_in_port;
    }

    public String getDdbb_in_schema() {
        return ddbb_in_schema;
    }

    public void setDdbb_in_schema(String ddbb_in_schema) {
        this.ddbb_in_schema = ddbb_in_schema;
    }

    public String getDdbb_out_url() {
        return ddbb_out_url;
    }

    public void setDdbb_out_url(String ddbb_out_url) {
        this.ddbb_out_url = ddbb_out_url;
    }

    public String getDdbb_out_type() {
        return ddbb_out_type;
    }

    public void setDdbb_out_type(String ddbb_out_type) {
        this.ddbb_out_type = ddbb_out_type;
    }

    public String getDdbb_out_user() {
        return ddbb_out_user;
    }

    public void setDdbb_out_user(String ddbb_out_user) {
        this.ddbb_out_user = ddbb_out_user;
    }

    public String getDdbb_out_password() {
        return ddbb_out_password;
    }

    public void setDdbb_out_password(String ddbb_out_password) {
        this.ddbb_out_password = ddbb_out_password;
    }

    public String getDdbb_out_port() {
        return ddbb_out_port;
    }

    public void setDdbb_out_port(String ddbb_out_port) {
        this.ddbb_out_port = ddbb_out_port;
    }

    public String getDdbb_out_schema() {
        return ddbb_out_schema;
    }

    public void setDdbb_out_schema(String ddbb_out_schema) {
        this.ddbb_out_schema = ddbb_out_schema;
    }

    public boolean getPeriodically_execution() {
        return periodically_execution;
    }

    public void setPeriodically_execution(boolean periodically_execution) {
        this.periodically_execution = periodically_execution;
    }

    public String getTime_interval() {
        return time_interval;
    }

    public void setTime_interval(String time_interval) {
        this.time_interval = time_interval;
    }

    public String getTime_unit() {
        return time_unit;
    }

    public void setTime_unit(String time_unit) {
        this.time_unit = time_unit;
    }

    @Override
    public String toString() {
        return "PoolConfig [ddbb_in_url=" + ddbb_in_url + ", ddbb_in_type=" + ddbb_in_type + ", ddbb_in_user="
                + ddbb_in_user + ", ddbb_in_password=" + ddbb_in_password + ", ddbb_in_port=" + ddbb_in_port
                + ", ddbb_in_schema=" + ddbb_in_schema + ", ddbb_in_table=" + ddbb_in_table + ", ddbb_in_columns="
                + ddbb_in_columns + ", ddbb_out_url=" + ddbb_out_url + ", ddbb_out_type=" + ddbb_out_type
                + ", ddbb_out_user=" + ddbb_out_user + ", ddbb_out_password=" + ddbb_out_password + ", ddbb_out_port="
                + ddbb_out_port + ", ddbb_out_schema=" + ddbb_out_schema + ", ddbb_out_table=" + ddbb_out_table
                + ", ddbb_out_columns=" + ddbb_out_columns + ", periodically_execution=" + periodically_execution
                + ", time_interval=" + time_interval + ", time_unit=" + time_unit + ", send_mail=" + send_mail
                + ", name=" + name + ", description=" + description + "]";
    }

    public String getNextExecution() {
        return nextExecution;
    }

    public void setNextExecution(String nextExecution) {
        this.nextExecution = nextExecution;
    }

}