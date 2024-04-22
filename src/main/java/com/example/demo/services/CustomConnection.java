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

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomConnection {

    private Properties properties;
    private String ddbbType;
    private Connection connection;
    private String jsonUrl;
    private ArrayList<HashMap<String, String>> data;
    private boolean jsonIsFile;

    public CustomConnection(Properties properties) {
        this.properties = properties;
        this.ddbbType = properties.getProperty("type");
        try {
            connect();
        } catch (Exception e) {
        }
    }

    private void connect() throws SQLException {
        String url = "";
        switch (ddbbType) {
            case "mysql": {
                url = "jdbc:mysql://" + properties.getProperty("host") + ":" +
                        properties.getProperty("port") + "/" + properties.getProperty("schema") +
                        "?user=" + properties.getProperty("user") + "&password=" + properties.getProperty("password");
                this.connection = DriverManager.getConnection(url);
            }
                break;
            case "postgresql": {
                url = "jdbc:postgresql://" + properties.getProperty("host") + ":" +
                        properties.getProperty("port") + "/" + properties.getProperty("schema") +
                        "?user=" + properties.getProperty("user") + "&password=" + properties.getProperty("password");
                this.connection = DriverManager.getConnection(url);
            }
                break;
            case "json": {
                this.jsonUrl = properties.getProperty("url");
                this.jsonIsFile = isURL(this.jsonUrl);
            }
                break;
            default:
                System.out.println("Fallo garrafal");
        }
    }

    public boolean ping() throws Exception {
        switch (ddbbType) {
            case "mysql":
            case "postgresql":
                return connection.isValid(2000);
            case "json": {
                if (jsonIsFile) {
                    File file = new File(jsonUrl);
                    return (file.exists() ? true : false);
                } else {
                    return pingURL(jsonUrl, 2000);
                }
            }
            default:
                throw new Exception("Fallo garrafal");
        }
    }

    public void extractData() throws SQLException {
        switch (ddbbType) {
            case "mysql":
            case "postgresql":
                this.data = getAllDataSql();
                break;
            case "json":
                this.data = getAllDataJSON();
            default:
                break;
        }
    }

    public long insertData() throws StreamWriteException, DatabindException, IOException, SQLException {
        switch (ddbbType) {
            case "mysql":
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
            return rsToList(st.executeQuery("select * from " + properties.getProperty("table")));
        }
    }

    public boolean dropData() throws SQLException {
        try (Statement st = connection.createStatement()) {
            return st.execute("delete * from " + properties.getProperty("table"));
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
            return mapper.readValue(new File(jsonUrl), new TypeReference<ArrayList<HashMap<String, String>>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
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
            String[] columns = data.get(0).keySet().toArray(new String[0]);
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");
            sb.append(properties.getProperty("table("));
            for (String col : columns) {
                sb.append(col);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length());
            sb.append(" values(");
            for (HashMap<String, String> m : data) {
                for (String col : columns) {
                    sb.append(m.get(col));
                    sb.append(",");
                }
                sb.deleteCharAt(sb.length());
                sb.append("),(");
            }
            sb.delete(sb.length() - 1, sb.length());

            return st.executeLargeUpdate(st.toString());
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
            System.err.println("Error al hacer ping a la URL: " + e.getMessage());
            return false;
        }
    }

    public void setAutoCommit(boolean b) throws SQLException {
        switch (this.ddbbType) {
            case "mysql":
            case "postgresql":
                this.connection.setAutoCommit(b);
                break;
            default:
                break;
        }

    }

    public void commit() throws SQLException {
        switch (this.ddbbType) {
            case "mysql":
            case "postgresql":
                this.connection.commit();
                break;
            default:
                break;
        }
    }

    public void rollback() throws SQLException {
        switch (this.ddbbType) {
            case "mysql":
            case "postgresql":
                this.connection.rollback();
                break;
            default:
                break;
        }
    }

    public void clearData(){
        this.data = null;
    }
}
