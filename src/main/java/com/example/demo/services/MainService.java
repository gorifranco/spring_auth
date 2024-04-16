package com.example.demo.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.demo.config.ConfigManager;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

public class MainService {

    CustomConnection c_in;
    CustomConnection c_out;

    Properties prop_in;
    Properties prop_out;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Runnable task;

    public MainService() {
        createProperties();
        c_in = new CustomConnection(prop_in);
        c_out = new CustomConnection(prop_out);

    }

    private void createProperties() {
        this.prop_in = new Properties();
        this.prop_in.setProperty("host", ConfigManager.getString("ddbb_in_host"));
        this.prop_in.setProperty("user", ConfigManager.getString("ddbb_in_user"));
        this.prop_in.setProperty("password", ConfigManager.getString("ddbb_in_password"));
        this.prop_in.setProperty("port", ConfigManager.getString("ddbb_in_port"));
        this.prop_in.setProperty("schema", ConfigManager.getString("ddbb_in_schema"));
        this.prop_in.setProperty("type", ConfigManager.getString("ddbb_in_type"));

        this.prop_out = new Properties();
        this.prop_out.setProperty("url", ConfigManager.getString("ddbb_out_url"));
        this.prop_out.setProperty("user", ConfigManager.getString("ddbb_out_user"));
        this.prop_out.setProperty("password", ConfigManager.getString("ddbb_out_password"));
        this.prop_out.setProperty("port", ConfigManager.getString("ddbb_out_port"));
        this.prop_out.setProperty("table", ConfigManager.getString("ddbb_out_table"));
        this.prop_in.setProperty("type", ConfigManager.getString("ddbb_out_type"));
    }

    private void createRunnable() {
        task = new Runnable() {
            @Override
            public void run() {
                long delay = 0;
                try {
                    ZonedDateTime now = ZonedDateTime.now();
                    String time_unit = ConfigManager.getString("time_unit");
                    long time_interval = ConfigManager.getLong("time_interval");
                    switch (time_unit) {
                        case "day":
                            delay = now.until(now.plusDays(time_interval), ChronoUnit.MILLIS);
                        case "month":
                            delay = now.until(now.plusMonths(time_interval), ChronoUnit.MILLIS);
                        case "year":
                            delay = now.until(now.plusYears(time_interval), ChronoUnit.MILLIS);
                    }
                    updateDB();
                }catch(Exception e){
                    
                }finally {
                    executor.schedule(this, delay, TimeUnit.MILLISECONDS);
                }
            }
        };
    }

    public void runOnce() throws StreamWriteException, DatabindException, SQLException, IOException, Exception{
        updateDB();
    }

    public void runPeriodically() {
        createRunnable();
        task.run();
    }

    public void stopService() {
        executor.shutdown();
    }

    private void updateDB() throws StreamWriteException, DatabindException, SQLException, IOException, Exception {

        if (c_in.ping() && c_out.ping()) {
            c_out.setAutoCommit(false);

            c_in.extractData();
            c_out.insertData();
            c_out.commit();
        }
    }
}
