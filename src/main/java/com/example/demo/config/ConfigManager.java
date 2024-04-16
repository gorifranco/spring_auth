package com.example.demo.config;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigManager {
    private static final String CONFIG_FILE_PATH = "src\\main\\java\\com\\example\\demo\\config\\config.conf";
    private static Configuration config;
    private static FileBasedConfigurationBuilder<FileBasedConfiguration> builder;

    static {
        Parameters params = new Parameters();
        builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                .configure(params.properties()
                    .setFileName(CONFIG_FILE_PATH)
                    .setThrowExceptionOnMissing(true)
                    .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static synchronized String getString(String key) {
        return config.getString(key);
    }

    public static synchronized long getLong(String key) {
        return config.getLong(key);
    }

    public static synchronized void setString(String key, String value) {
        config.setProperty(key, value);
        try {
            builder.save();
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failed to save configuration", e);
        }
    }

    public static synchronized Map<String, String> getAllProperties() {
        Map<String, String> allProperties = new LinkedHashMap<>();
        Iterator<String> keys = config.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            allProperties.put(key, config.getString(key));
        }
        return allProperties;
    }

    public static synchronized void updateProperties(Map<String, String> updates) {
        updates.forEach((key, value) -> {
            if (value != null) {
                config.setProperty(key, value);
            }
        });
        try {
            builder.save();
        } catch (ConfigurationException e) {
            throw new RuntimeException("Failed to save configuration", e);
        }
    }
}
