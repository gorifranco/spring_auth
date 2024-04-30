package com.example.demo.services;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;

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

                logger.info("Intentant connectar a " + properties.getProperty("url"));
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
                return (jsonIsFile) ? getAllDataJSONFile() : getAllDataJSONUrl();
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
            String sentence = "delete from " + properties.getProperty("table") + ";";
            int result = st.executeUpdate(sentence);
            logger.info("Dropped data from " + properties.getProperty("url") + ": " + result + " tuples");
            return true;
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

    private ArrayList<HashMap<String, String>> getAllDataJSONUrl() {
        UncheckedObjectMapper objectMapper = new UncheckedObjectMapper();

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(properties.getProperty("url")))
                    .header("Accept", "application/json")
                    .build();

            CompletableFuture<String> responseFuture = HttpClient.newHttpClient()
                    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body);

            return objectMapper.readValue(responseFuture.join(), new TypeReference<>() {
            });
        } catch (IOException e) {
            logger.error("Error extracting data from URL: " + e.getMessage());
            throw new CompletionException(e);
        }
    }

    private ArrayList<HashMap<String, String>> getAllDataJSONFile() {

        ObjectMapper mapper = new ObjectMapper();

        try {
            ArrayList<HashMap<String, String>> data = mapper.readValue(new File(jsonUrl),
                    new TypeReference<ArrayList<HashMap<String, String>>>() {
                    });
            logger.info("Extracted data from json " + properties.getProperty("url"));
            return data;
        } catch (IOException e) {
            logger.error("Error extracting data from JSON: " + e.getMessage());
        }

        return null;
    }

    private long insertDataJson(ArrayList<HashMap<String, String>> data)
            throws StreamWriteException, DatabindException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(jsonUrl), data);
        return data.size();
    }

    /*
     * private long insertDataSql(ArrayList<HashMap<String, String>> data) throws
     * SQLException {
     * long contador = 0;
     * try (Statement st = connection.createStatement()) {
     * logger.info("Trying to insert data in sql " + properties.getProperty("url"));
     * StringBuilder sb = new StringBuilder();
     * for (HashMap<String, String> m : data) {
     * sb.append("insert into ");
     * sb.append(properties.getProperty("table"));
     * sb.append("(");
     * sb.append(properties.getProperty("columns"));
     * sb.append(") value(");
     * for (String col : columns) {
     * sb.append("\"");
     * sb.append(m.get(col));
     * sb.append("\",");
     * }
     * sb.append(")");
     * sb.deleteCharAt(sb.length() - 2); // Eliminar la coma final
     * sb.append(";");
     * System.out.println(sb.toString());
     * st.executeUpdate(sb.toString());
     * }
     * return contador;
     * }catch(SQLException e){
     * logger.error("error creant inserts: " + e.getMessage());
     * throw e;
     * }catch(Exception e){
     * logger.error("Error del bucle: " + e.getMessage());
     * return 1;
     * }
     * }
     */

    private long insertDataSql(ArrayList<HashMap<String, String>> data) throws SQLException {
        try (Statement st = connection.createStatement()) {
            logger.info("Trying to insert data in sql " + properties.getProperty("url"));
            logger.info("Tuples: " + data.size());
            StringBuilder sb = new StringBuilder();
            sb.append("insert into ");
            sb.append(properties.getProperty("table"));
            sb.append("(");
            sb.append(properties.getProperty("columns"));
            sb.append(") values");
            for (HashMap<String, String> m : data) {
                sb.append("(");
                for (String col : columns) {
                    sb.append("\"");
                    sb.append(m.get(col));
                    sb.append("\",");
                    if (m.get(col) == null) {
                        sb.deleteCharAt(sb.length() - 7);
                        sb.deleteCharAt(sb.length() - 1);
                    }
                }
                sb.deleteCharAt(sb.length() - 1); // Eliminar la coma final
                sb.append("),\n");
            }
            sb.delete(sb.length() - 2, sb.length() - 1);
            sb.append(";");
            long executed = st.executeLargeUpdate(sb.toString());
            logger.info("Inserts executats amb èxit: " + executed);
            return executed;
        } catch (SQLException e) {
            rollback();
            logger.error("error creant inserts: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            rollback();
            logger.error("Error del bucle: " + e.getMessage());
            return 1;
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
                logger.info("Rollback");
                break;
            default:
                break;
        }
    }

    class UncheckedObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {
        /** Parses the given JSON string into a Map. */
        ArrayList<HashMap<String, String>> readValue(String content) {
            try {
                return this.readValue(content, new TypeReference<>() {
                });
            } catch (IOException ioe) {
                throw new CompletionException(ioe);
            }
        }
    }
}