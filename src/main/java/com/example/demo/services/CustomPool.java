package com.example.demo.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.demo.config.ConfigManager;
import com.example.demo.models.PoolConfig;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

public class CustomPool {

    private PoolConfig dbconf;

    private CustomConnection c_in;
    private CustomConnection c_out;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private Runnable task;

    public CustomPool(PoolConfig dbconf) {
        this.dbconf = dbconf;
        createConnections();
    }

    private void createConnections() {
        Properties p_in = new Properties();
        p_in.setProperty("url", dbconf.getDdbb_in_url());
        p_in.setProperty("user", dbconf.getDdbb_in_user());
        p_in.setProperty("password", dbconf.getDdbb_in_password());
        p_in.setProperty("port", dbconf.getDdbb_in_port());
        p_in.setProperty("schema", dbconf.getDdbb_in_schema());
        p_in.setProperty("type", dbconf.getDdbb_in_type());
        p_in.setProperty("table", dbconf.getDdbb_in_table());

        c_in = new CustomConnection(p_in);

        Properties p_out = new Properties();
        p_out.setProperty("url", dbconf.getDdbb_out_url());
        p_out.setProperty("user", dbconf.getDdbb_out_user());
        p_out.setProperty("password", dbconf.getDdbb_out_password());
        p_out.setProperty("port", dbconf.getDdbb_out_port());
        p_out.setProperty("schema", dbconf.getDdbb_out_schema());
        p_out.setProperty("table", dbconf.getDdbb_out_table());
        p_out.setProperty("type", dbconf.getDdbb_out_type());

        c_out = new CustomConnection(p_out);
    }

    public String run() {
        try {
            if (dbconf.getPeriodically_execution() == "yes") {
                runPeriodically();
                return "{status: \"success\"," +
                        " next_execution: \"" + nextExecution() + "\"}";
            } else {
                runOnce();
                return "{status: \"success\"," +
                        " next_execution: \"no\"}";
            }
        } catch (Exception e) {
            return "Exception running service: " + e.getMessage();
        }
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
                    nextExecution();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    executor.schedule(this, delay, TimeUnit.MILLISECONDS);
                }
            }
        };
    }

    private String nextExecution() {
        String timeUnit = ConfigManager.getString("time_unit");
        int timeInterval = Integer.parseInt(ConfigManager.getString("time_interval"));

        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate;
        switch (timeUnit.toLowerCase()) {
            case "day":
                futureDate = currentDate.plusDays(timeInterval);
                break;
            case "month":
                futureDate = currentDate.plusMonths(timeInterval);
                break;
            case "year":
                futureDate = currentDate.plusYears(timeInterval);
                break;
        }

        return futureDate.toString();

    }

    public void runOnce() throws StreamWriteException, DatabindException, SQLException, IOException, Exception {
        updateDB();
        ConfigManager.setString("next_execution", "no");
    }

    public void runPeriodically() {
        createRunnable();
        task.run();
    }

    public void stopService() {
        executor.shutdown();
        ConfigManager.setString("next_execution", "no");
    }

    private void updateDB() throws StreamWriteException, DatabindException, SQLException, IOException, Exception {

        if (c_in.ping() && c_out.ping()) {
            c_out.setAutoCommit(false);

            c_in.extractData();
            c_out.insertData();
            c_out.commit();
        }
    }

    public PoolConfig getDatabaseConfig() {
        return this.dbconf;
    }

    public void updateConf(PoolConfig poolConfig){
        this.dbconf = poolConfig;
    }

}
