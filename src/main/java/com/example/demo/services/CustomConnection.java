package com.example.demo.services;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.config.ConfigManager;
import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomConnection {

    private static final Logger logger = LoggerFactory.getLogger(CustomConnection.class);

    private Properties properties;
    private String ddbbType;
    private Connection connection;
    private String jsonUrl;
    private boolean jsonIsFile;
    private String[] columns;

    public CustomConnection(Properties properties) {
        this.properties = properties;

        columns = properties.getProperty("columns").split(",");

        for (int i = 0; i < columns.length; i++) {
            columns[i] = columns[i].trim();
        }

        this.ddbbType = properties.getProperty("type");
        try {
            connect();
        } catch (Exception e) {
        }
    }

    private void connect() throws SQLException {
        String url = "";
        switch (ddbbType) {
            case "mariadb": {
                url = "jdbc:mariadb://" + properties.getProperty("url") + ":" +
                        properties.getProperty("port")
                        + (properties.getProperty("schema") != "" ? "/" + properties.getProperty("schema") : "") +
                        "?user=" + properties.getProperty("user") + "&password=" + properties.getProperty("password");

                logger.info("Intentant connectar a " + url);
                this.connection = DriverManager.getConnection(url);
            }
                break;
            case "postgresql": {
                url = "jdbc:postgresql://" + properties.getProperty("url") + ":" +
                        properties.getProperty("port") + "/" + properties.getProperty("schema") +
                        "?user=" + properties.getProperty("user") + "&password=" + properties.getProperty("password");
                this.connection = DriverManager.getConnection(url);
            }
                break;
            case "json": {
                this.jsonUrl = properties.getProperty("url");
                this.jsonIsFile = !isURL(this.jsonUrl);
            }
                break;
            default:
                System.out.println("Fallo garrafal");
        }
    }

    public boolean ping() throws Exception {
        boolean tmp = false;
        try {
            switch (ddbbType) {
                case "mariadb":
                case "postgresql":
                    tmp = connection.isValid(2000);
                    return tmp;
                case "json": {
                    if (jsonIsFile) {
                        File file = new File(jsonUrl);
                        tmp = file.exists() ? true : false;
                        return tmp;
                    } else {
                        tmp = pingURL(jsonUrl, 2000);
                        return tmp;
                    }
                }
                default:
                    throw new Exception("Fallo garrafal");
            }
        } finally {
            logger.info("Connection to " + properties.getProperty("url") + " : " + tmp);
        }
    }

    public ArrayList<HashMap<String, String>> extractData() throws SQLException {
        switch (ddbbType) {
            case "mariadb":
            case "postgresql":
                return getAllDataSql();
            case "json":
                return getAllDataJSON();
            default:
                break;
        }
        return null;
    }

    public long insertData(ArrayList<HashMap<String, String>> data)
            throws StreamWriteException, DatabindException, IOException, SQLException {
        switch (ddbbType) {
            case "mariadb":
            case "postgresql":
                return insertDataSql(data);
            case "json":
                return insertDataJson(data);
            default:
                return -1;
        }
    }

    private ArrayList<HashMap<String, String>> getAllDataSql() throws SQLException {
        try (Statement st = connection.createStatement()) {
            logger.info("Informació extreta de " + properties.get("name"));
            return rsToList(st.executeQuery("select * from " + properties.getProperty("table")));
        }
    }

    public boolean dropData() throws SQLException {
        try (Statement st = connection.createStatement()) {
            boolean result = st.execute("delete * from " + properties.getProperty("table"));
            logger.info("Dropped data from " + properties.getProperty("url") + ": " + result);
            return result;
        } catch (SQLException e) {
            logger.error("Error deleting data from " + properties.getProperty("url") + ": " + e.getMessage());
            throw e;
        }
    }

    private ArrayList<HashMap<String, String>> rsToList(ResultSet rs) throws SQLException {
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        String[] columns = new String[rsmd.getColumnCount()];
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columns[i] = rsmd.getColumnName(i);
        }

        while (rs.next()) {
            HashMap<String, String> tmp = new HashMap<String, String>();
            for (String col : columns) {
                tmp.put(col, rs.getString(col));
            }
            data.add(tmp);
        }
        return data;
    }

    private ArrayList<HashMap<String, String>> getAllDataJSON() {

        ObjectMapper mapper = new ObjectMapper();

        try {
            ArrayList<HashMap<String, String>> data = mapper.readValue(new File(jsonUrl),
                    new TypeReference<ArrayList<HashMap<String, String>>>() {
                    });
            logger.info("Extracted data from json " + properties.getProperty("url"));
            return data;
        } catch (IOException e) {
            logger.error("Error extracting data from JSON (CustomConnection ln 161): " + e.getMessage());
        }

        return null;
    }

    private long insertDataJson(ArrayList<HashMap<String, String>> data)
            throws StreamWriteException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(jsonUrl), data);
        return data.size();
    }

    private long insertDataSql(ArrayList<HashMap<String, String>> data) throws SQLException {
        try (Statement st = connection.createStatement()) {
            logger.info("Trying to insert data in sql " + properties.getProperty("url"));
            logger.info(data.get(0).toString());
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");
            sb.append(properties.getProperty("table"));
            sb.append("(");
            sb.append(properties.getProperty("columns"));
            sb.append(") values(");
            for (HashMap<String, String> m : data) {
                sb.append("(");
                for (String col : columns) {
                    String value = m.get(col).replace("'", "\\'"); // Escapar las comillas simples
                    sb.append("'");
                    sb.append(value);
                    sb.append("',");
                }
                sb.deleteCharAt(sb.length() - 1); // Eliminar la coma final
                sb.append("),");
            }
            sb.deleteCharAt(sb.length() - 1); // Eliminar la coma final

            logger.info("Sentència SQL: " + sb.toString());
            long executed = st.executeLargeUpdate(st.toString());
            logger.info("Inserts executats amb èxit: " + executed);
            return executed;
        }catch(SQLException e){
            logger.error("error creant inserts: " + e.getMessage());
            throw e;
        }
    }

    private static boolean isURL(String input) {
        try {
            URL url = new URL(input);
            return url.getProtocol().equals("http") || url.getProtocol().equals("https");
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean pingURL(String url, int timeout) {
        try {
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 299);
        } catch (Exception e) {
            logger.error("Error al hacer ping a la URL " + url + ": " + e.getMessage());
            return false;
        }
    }

    public void setAutoCommit(boolean b) throws SQLException {
        switch (this.ddbbType) {
            case "mariadb":
            case "postgresql":
                this.connection.setAutoCommit(b);
                break;
            default:
                break;
        }

    }

    public void commit() throws SQLException {
        switch (this.ddbbType) {
            case "mariadb":
            case "postgresql":
                this.connection.commit();
                break;
            default:
                break;
        }
    }

    public void rollback() throws SQLException {
        switch (this.ddbbType) {
            case "mariadb":
            case "postgresql":
                this.connection.rollback();
                break;
            default:
                break;
        }
    }
}
