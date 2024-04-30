package com.example.demo.services;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.models.PoolConfig;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;

public class CustomPool {

    private PoolConfig dbconf;

    private CustomConnection c_in;
    private CustomConnection c_out;

    private static final Logger logger = LoggerFactory.getLogger(CustomPool.class);
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
        p_in.setProperty("password", dbconf.getDdbb_in_password() != null ? CryptService.decrypt(dbconf.getDdbb_in_password()) : "");
        p_in.setProperty("port", dbconf.getDdbb_in_port());
        p_in.setProperty("schema", dbconf.getDdbb_in_schema());
        p_in.setProperty("type", dbconf.getDdbb_in_type());
        p_in.setProperty("table", dbconf.getDdbb_in_table());
        p_in.setProperty("columns", dbconf.getDdbb_in_columns());

        c_in = new CustomConnection(p_in);

        Properties p_out = new Properties();
        p_out.setProperty("url", dbconf.getDdbb_out_url());
        p_out.setProperty("user", dbconf.getDdbb_out_user());
        p_out.setProperty("password", dbconf.getDdbb_out_password() != null ? CryptService.decrypt(dbconf.getDdbb_out_password()) : "");
        p_out.setProperty("port", dbconf.getDdbb_out_port());
        p_out.setProperty("schema", dbconf.getDdbb_out_schema());
        p_out.setProperty("type", dbconf.getDdbb_out_type());
        p_out.setProperty("table", dbconf.getDdbb_out_table());
        p_out.setProperty("columns", dbconf.getDdbb_out_columns());

        c_out = new CustomConnection(p_out);
    }

    public String run() {

        if(c_in == null || c_out == null) reconect();
        
        try {
            if (dbconf.getPeriodically_execution()) {
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
        try {
            task = new Runnable() {
                @Override
                public void run() {
                    long delay = 0;
                    try {
                        ZonedDateTime now = ZonedDateTime.now();
                        String time_unit = dbconf.getTime_unit();
                        long time_interval = Long.parseLong(dbconf.getTime_interval());
                        switch (time_unit) {
                            case "day":
                                delay = now.until(now.plusDays(time_interval), ChronoUnit.MILLIS);
                            case "month":
                                delay = now.until(now.plusMonths(time_interval), ChronoUnit.MILLIS);
                            case "year":
                                delay = now.until(now.plusYears(time_interval), ChronoUnit.MILLIS);
                        }
                        updateDB();
                        dbconf.setNextExecution(nextExecution());
                    } catch (Exception e) {
                        logger.error("Error creating runnable: " + e.getMessage());
                    } finally {
                        executor.schedule(this, delay, TimeUnit.MILLISECONDS);
                    }
                }
            };
        } catch (Exception e) {
            logger.error("error creating runnable: " + e.getMessage());
        }

    }

    private String nextExecution() {
        String timeUnit = dbconf.getTime_unit();
        int timeInterval = Integer.parseInt(dbconf.getTime_interval());

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
        logger.info("Proxima execucio: " + futureDate);
        return futureDate.toString();

    }

    private void runOnce() throws StreamWriteException, DatabindException, SQLException, IOException, Exception {
        dbconf.setNextExecution("");
        updateDB();
    }

    private void runPeriodically() {
        createRunnable();
        task.run();
    }

    public void stopService() {
        executor.shutdown();
        dbconf.setNextExecution("");
        dbconf.setPeriodically_execution(false);
        logger.info("Servei " + dbconf.getName() + " aturat.");
    }

    private void updateDB() throws StreamWriteException, DatabindException, SQLException, IOException, Exception {

        if (c_in.ping() && c_out.ping()) {
            c_out.setAutoCommit(false);

            ArrayList<HashMap<String, String>> data = c_in.extractData();
            c_out.dropData();
            long inserts = c_out.insertData(data);
            c_out.commit();

            if (dbconf.getSend_mail()) {
                EmailService.sendEmail("Servei executat correctament. Tuples rebudes: " + data.size()
                        + ". Tuples insertades: " + inserts);
            }
        }
    }
    public PoolConfig getDatabaseConfig() {
        return this.dbconf;
    }

    public void updateConf(PoolConfig poolConfig) {
        this.dbconf = poolConfig;
        reconect();
    }

    private void reconect(){
        createConnections();
    }

}
