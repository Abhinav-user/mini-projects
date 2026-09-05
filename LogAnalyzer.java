import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LogAnalyzer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", LogAnalyzer::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Log Analyzer started!");
        System.out.println("Open http://localhost:8080");
    }

    static void handle(HttpExchange exchange) throws IOException {

        String logs = "";

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    exchange.getRequestBody(),
                                    StandardCharsets.UTF_8));

            StringBuilder data = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                data.append(line);
            }

            String request = data.toString();

            if (request.startsWith("logs=")) {
                logs = URLDecoder.decode(
                        request.substring(5),
                        StandardCharsets.UTF_8);
            }
        }

        String result = "";

        if (!logs.trim().isEmpty()) {
            result = analyze(logs);
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Log File Analyzer</title>" +

                "<style>" +

                "body {" +
                "font-family:Arial;" +
                "background:#eeeeee;" +
                "padding:40px;" +
                "}" +

                ".container {" +
                "max-width:800px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:25px;" +
                "border-radius:12px;" +
                "box-shadow:0 0 15px #bbb;" +
                "}" +

                "h1 {" +
                "text-align:center;" +
                "}" +

                "textarea {" +
                "width:97%;" +
                "height:220px;" +
                "padding:10px;" +
                "font-family:monospace;" +
                "font-size:14px;" +
                "}" +

                "button {" +
                "margin-top:15px;" +
                "padding:12px 25px;" +
                "background:#222;" +
                "color:white;" +
                "border:0;" +
                "border-radius:6px;" +
                "cursor:pointer;" +
                "}" +

                ".result {" +
                "margin-top:25px;" +
                "padding:20px;" +
                "background:#f7f7f7;" +
                "border-radius:10px;" +
                "line-height:1.8;" +
                "}" +

                "</style>" +
                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>📊 Log File Analyzer</h1>" +

                "<p>" +
                "Paste your server logs below:" +
                "</p>" +

                "<form method='POST'>" +

                "<textarea name='logs' " +
                "placeholder='192.168.1.10 GET /index.html 200'>" +
                "</textarea>" +

                "<br>" +

                "<button type='submit'>" +
                "Analyze Logs" +
                "</button>" +

                "</form>" +

                result +

                "</div>" +

                "</body>" +
                "</html>";

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8");

        byte[] response =
                html.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                200,
                response.length);

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    static String analyze(String logs) {

        String[] lines = logs.split("\\r?\\n");

        int total = 0;
        int success = 0;
        int clientErrors = 0;
        int serverErrors = 0;

        Set<String> ips = new HashSet<>();

        Map<String, Integer> methods =
                new HashMap<>();

        Map<String, Integer> pages =
                new HashMap<>();

        for (String line : lines) {

            line = line.trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");

            /*
             Expected format:

             IP METHOD PAGE STATUS

             Example:
             192.168.1.10 GET /home 200
            */

            if (parts.length < 4) {
                continue;
            }

            String ip = parts[0];
            String method = parts[1];
            String page = parts[2];

            int status;

            try {
                status = Integer.parseInt(parts[3]);
            } catch (Exception e) {
                continue;
            }

            total++;

            ips.add(ip);

            methods.put(
                    method,
                    methods.getOrDefault(method, 0) + 1);

            pages.put(
                    page,
                    pages.getOrDefault(page, 0) + 1);

            if (status >= 200 && status < 400) {
                success++;
            }
            else if (status >= 400 && status < 500) {
                clientErrors++;
            }
            else if (status >= 500 && status < 600) {
                serverErrors++;
            }
        }

        String commonMethod = getMostCommon(methods);
        String commonPage = getMostCommon(pages);

        double errorPercentage = 0;

        if (total > 0) {
            errorPercentage =
                    ((double)(clientErrors + serverErrors)
                            / total) * 100;
        }

        return
                "<div class='result'>" +

                "<h2>📈 Analysis Result</h2>" +

                "<b>Total Requests:</b> " +
                total + "<br>" +

                "<b>Successful Requests:</b> " +
                success + "<br>" +

                "<b>4xx Errors:</b> " +
                clientErrors + "<br>" +

                "<b>5xx Errors:</b> " +
                serverErrors + "<br>" +

                "<b>Unique IP Addresses:</b> " +
                ips.size() + "<br>" +

                "<b>Most Used HTTP Method:</b> " +
                commonMethod + "<br>" +

                "<b>Most Requested Page:</b> " +
                commonPage + "<br>" +

                "<b>Error Percentage:</b> " +
                String.format("%.2f", errorPercentage) +
                "%<br>" +

                "</div>";
    }

    static String getMostCommon(
            Map<String, Integer> map) {

        if (map.isEmpty()) {
            return "None";
        }

        String result = "";
        int highest = 0;

        for (Map.Entry<String, Integer> entry :
                map.entrySet()) {

            if (entry.getValue() > highest) {

                highest = entry.getValue();
                result = entry.getKey();
            }
        }

        return result;
    }
}