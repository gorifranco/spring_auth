package com.example.demo.logs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CustomLogManager {

    private static final String logPath = "src\\main\\java\\com\\example\\demo\\logs\\app.log";

    public static String returnReverseLogHTML() throws IOException {
        StringBuilder sb = new StringBuilder();

        try (ReverseFileReader rfr = new ReverseFileReader(new File(logPath))) {
            int ch;
            StringBuilder lineBuilder = new StringBuilder();
            // Leer hasta que no haya más caracteres
            while ((ch = rfr.read()) != -1) {
                if (ch == '\n') {
                    if (lineBuilder.length() > 0) { // Asegurar que no añadimos líneas vacías
                        String currentLine = lineBuilder.toString();
                        if (currentLine.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}.*")) {
                            sb.append("<b>").append(currentLine.substring(0, 23)).append("</b>")
                                    .append(currentLine.substring(23)).append("<br />");
                        } else {
                            sb.append(currentLine).append("<br />");
                        }
                    }
                    lineBuilder = new StringBuilder(); // Resetear el constructor de strings para la siguiente línea
                } else {
                    lineBuilder.append((char) ch);
                }
            }

            if (lineBuilder.length() > 0) {
                String currentLine = lineBuilder.toString();
                if (currentLine.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}.*")) {
                    sb.append("<b>").append(currentLine.substring(0, 23)).append("</b>")
                            .append(currentLine.substring(23)).append("\n");
                } else {
                    // Agrega la línea sin formato de fecha al inicio
                    sb.append(currentLine).append("\n");
                }
            }
        }

        return sb.toString();
    }

    public static String returnLogHtml() throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(logPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    public static String returnLogHtmlFromTime(String time) throws FileNotFoundException, IOException {
        StringBuilder sb = new StringBuilder();

        try (ReverseFileReader rfr = new ReverseFileReader(new File(logPath))) {
            int ch;
            StringBuilder lineBuilder = new StringBuilder();
            // Leer hasta que no haya más caracteres
            while ((ch = rfr.read()) != -1) {
                if (ch == '\n') {
                    if (lineBuilder.length() > 0) {
                        if (lineBuilder.toString().startsWith(time))
                            break;
                        sb.insert(0, lineBuilder.toString() + "<br>");
                    }
                    lineBuilder = new StringBuilder(); // Resetear el constructor de strings para la siguiente línea
                } else {
                    lineBuilder.append((char) ch);
                }
            }
            // Añadir la última línea leída si existe
            if (lineBuilder.length() > 0) {
                sb.insert(0, lineBuilder.toString() + "<br>");
            }
        }

        return sb.toString();
    }

    public static String getLastLog() throws IOException {
        StringBuilder lineBuilder = new StringBuilder();
        int ch;
        try (ReverseFileReader rfr = new ReverseFileReader(new File(logPath))) {
            while ((ch = rfr.read()) != -1) {
                if (ch == '\n') {
                    if (lineBuilder.length() > 0) { // Asegurar que no añadimos líneas vacías
                        String currentLine = lineBuilder.toString();
                        if (currentLine.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}.*")) {
                            return currentLine.substring(0, 23);
                        }
                    }
                    lineBuilder = new StringBuilder();
                }
                lineBuilder.append((char) ch);
            }
        }
        return null;
    }

    
}