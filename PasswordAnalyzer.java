import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PasswordAnalyzer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", PasswordAnalyzer::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Password Analyzer running...");
        System.out.println("Open: http://localhost:8080");
    }

    static void handle(HttpExchange exchange) throws IOException {

        String password = "";

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            InputStream input = exchange.getRequestBody();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(input, StandardCharsets.UTF_8));

            String data = reader.readLine();

            if (data != null && data.startsWith("password=")) {
                password = URLDecoder.decode(
                        data.substring(9),
                        StandardCharsets.UTF_8);
            }
        }

        String result = "";

        if (!password.isEmpty()) {
            result = analyze(password);
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Password Analyzer</title>" +

                "<style>" +
                "body {" +
                "font-family: Arial;" +
                "background:#f2f2f2;" +
                "display:flex;" +
                "justify-content:center;" +
                "padding-top:60px;" +
                "}" +

                ".box {" +
                "background:white;" +
                "padding:30px;" +
                "width:500px;" +
                "border-radius:12px;" +
                "box-shadow:0 0 15px #ccc;" +
                "}" +

                "h1 {" +
                "text-align:center;" +
                "}" +

                "input {" +
                "width:95%;" +
                "padding:12px;" +
                "font-size:16px;" +
                "margin:10px 0;" +
                "}" +

                "button {" +
                "width:100%;" +
                "padding:12px;" +
                "background:#222;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "font-size:16px;" +
                "cursor:pointer;" +
                "}" +

                ".result {" +
                "margin-top:20px;" +
                "padding:15px;" +
                "background:#f7f7f7;" +
                "border-radius:8px;" +
                "line-height:1.7;" +
                "}" +

                "</style>" +
                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>🔐 Password Analyzer</h1>" +

                "<form method='POST'>" +

                "<input type='password' " +
                "name='password' " +
                "placeholder='Enter password' required>" +

                "<button type='submit'>Analyze Password</button>" +

                "</form>" +

                result +

                "</div>" +

                "</body>" +
                "</html>";

        exchange.getResponseHeaders()
                .set("Content-Type", "text/html; charset=UTF-8");

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

    static String analyze(String password) {

        int score = 0;

        StringBuilder suggestions =
                new StringBuilder();

        int length = password.length();

        // Length
        if (length >= 8) {
            score += 2;
        } else {
            suggestions.append(
                    "• Use at least 8 characters<br>");
        }

        if (length >= 12) {
            score += 1;
        }

        // Lowercase
        if (password.matches(".*[a-z].*")) {
            score++;
        } else {
            suggestions.append(
                    "• Add lowercase letters<br>");
        }

        // Uppercase
        if (password.matches(".*[A-Z].*")) {
            score++;
        } else {
            suggestions.append(
                    "• Add uppercase letters<br>");
        }

        // Number
        if (password.matches(".*[0-9].*")) {
            score++;
        } else {
            suggestions.append(
                    "• Add numbers<br>");
        }

        // Special character
        if (password.matches(".*[^a-zA-Z0-9].*")) {
            score++;
        } else {
            suggestions.append(
                    "• Add special characters like @, #, !<br>");
        }

        String strength;

        if (score <= 2) {
            strength = "❌ Weak";
        } else if (score <= 4) {
            strength = "⚠️ Medium";
        } else {
            strength = "✅ Strong";
        }

        String suggestionsText;

        if (suggestions.length() == 0) {
            suggestionsText =
                    "🎉 Your password meets all the basic requirements!";
        } else {
            suggestionsText =
                    suggestions.toString();
        }

        return
                "<div class='result'>" +

                "<h2>" + strength + "</h2>" +

                "<b>Password Length:</b> " +
                length +

                "<br><b>Score:</b> " +
                score + "/7" +

                "<h3>Suggestions</h3>" +

                suggestionsText +

                "</div>";
    }
}