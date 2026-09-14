import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TextSimilarity {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", TextSimilarity::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Text Similarity Checker started!");
        System.out.println("Open: http://localhost:8080");
    }

    static void handle(HttpExchange exchange)
            throws IOException {

        String text1 = "";
        String text2 = "";
        String result = "";

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

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

            Map<String, String> form =
                    parseForm(data.toString());

            text1 = form.getOrDefault("text1", "");
            text2 = form.getOrDefault("text2", "");

            if (!text1.trim().isEmpty() &&
                    !text2.trim().isEmpty()) {

                result = analyze(text1, text2);
            }
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>Text Similarity Checker</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#eeeeee;" +
                "padding:40px;" +
                "}" +

                ".container{" +
                "max-width:850px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 0 15px #bbb;" +
                "}" +

                "h1{" +
                "text-align:center;" +
                "}" +

                ".texts{" +
                "display:flex;" +
                "gap:20px;" +
                "}" +

                ".area{" +
                "width:50%;" +
                "}" +

                "textarea{" +
                "width:94%;" +
                "height:180px;" +
                "padding:12px;" +
                "font-size:15px;" +
                "resize:vertical;" +
                "}" +

                "button{" +
                "width:100%;" +
                "padding:13px;" +
                "margin-top:20px;" +
                "background:#222;" +
                "color:white;" +
                "border:0;" +
                "border-radius:6px;" +
                "font-size:16px;" +
                "cursor:pointer;" +
                "}" +

                ".result{" +
                "margin-top:25px;" +
                "padding:20px;" +
                "background:#f5f5f5;" +
                "border-radius:10px;" +
                "}" +

                ".percentage{" +
                "font-size:35px;" +
                "font-weight:bold;" +
                "text-align:center;" +
                "}" +

                "@media(max-width:650px){" +
                ".texts{flex-direction:column;}" +
                ".area{width:100%;}" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🧠 Text Similarity Checker</h1>" +

                "<p>" +
                "Compare two texts based on their unique words." +
                "</p>" +

                "<form method='POST'>" +

                "<div class='texts'>" +

                "<div class='area'>" +
                "<h3>Text 1</h3>" +

                "<textarea name='text1' " +
                "placeholder='Enter first text...'>" +
                escapeHTML(text1) +
                "</textarea>" +

                "</div>" +

                "<div class='area'>" +
                "<h3>Text 2</h3>" +

                "<textarea name='text2' " +
                "placeholder='Enter second text...'>" +
                escapeHTML(text2) +
                "</textarea>" +

                "</div>" +

                "</div>" +

                "<button type='submit'>" +
                "Compare Texts" +
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

    static String analyze(
            String text1,
            String text2) {

        Set<String> words1 =
                getWords(text1);

        Set<String> words2 =
                getWords(text2);

        Set<String> intersection =
                new HashSet<>(words1);

        intersection.retainAll(words2);

        Set<String> union =
                new HashSet<>(words1);

        union.addAll(words2);

        double similarity = 0;

        if (!union.isEmpty()) {

            similarity =
                    ((double) intersection.size()
                            / union.size()) * 100;
        }

        String level;

        if (similarity >= 70) {
            level = "Very Similar";
        }
        else if (similarity >= 40) {
            level = "Moderately Similar";
        }
        else if (similarity >= 20) {
            level = "Slightly Similar";
        }
        else {
            level = "Mostly Different";
        }

        return
                "<div class='result'>" +

                "<h2>Result</h2>" +

                "<div class='percentage'>" +
                String.format("%.2f", similarity) +
                "%</div>" +

                "<p style='text-align:center;'>" +
                "<b>" + level + "</b>" +
                "</p>" +

                "<hr>" +

                "<b>Unique words in Text 1:</b> " +
                words1.size() +

                "<br>" +

                "<b>Unique words in Text 2:</b> " +
                words2.size() +

                "<br>" +

                "<b>Common words:</b> " +
                intersection.size() +

                "<br>" +

                "<b>Total unique words:</b> " +
                union.size() +

                "</div>";
    }

    static Set<String> getWords(String text) {

        text = text.toLowerCase();

        text = text.replaceAll(
                "[^a-z0-9\\s]", " ");

        String[] words =
                text.split("\\s+");

        Set<String> result =
                new HashSet<>();

        for (String word : words) {

            if (!word.isEmpty()) {
                result.add(word);
            }
        }

        return result;
    }

    static Map<String, String> parseForm(
            String data) {

        Map<String, String> result =
                new HashMap<>();

        String[] pairs =
                data.split("&");

        for (String pair : pairs) {

            String[] parts =
                    pair.split("=", 2);

            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8);

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8);

                result.put(key, value);
            }
        }

        return result;
    }

    static String escapeHTML(String text) {

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}