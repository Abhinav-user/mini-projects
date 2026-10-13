
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TextSimilarity {

    static final int PORT = 8080;

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(PORT), 0
        );

        server.createContext("/", exchange -> {
            try {
                if ("GET".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    sendHTML(exchange, buildPage(
                            "Text Similarity Checker",
                            "Compare two texts to see how similar they are.",
                            "", "", null
                    ));

                } else if ("POST".equalsIgnoreCase(
                        exchange.getRequestMethod())) {

                    String body = readRequestBody(
                            exchange.getRequestBody()
                    );

                    Map<String, String> form = parseForm(body);

                    String text1 = form.get("text1");
                    String text2 = form.get("text2");

                    if (text1 == null) {
                        text1 = "";
                    }

                    if (text2 == null) {
                        text2 = "";
                    }

                    if (text1.trim().isEmpty()
                            || text2.trim().isEmpty()) {

                        sendHTML(exchange, buildPage(
                                "Text Similarity Checker",
                                "Please enter both texts before comparing.",
                                text1, text2, null
                        ));

                    } else {
                        double similarity =
                                calculateSimilarity(text1, text2);

                        sendHTML(exchange, buildPage(
                                "Text Similarity Checker",
                                "Comparison completed successfully.",
                                text1, text2, similarity
                        ));
                    }

                } else {
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }

            } catch (Exception e) {
                e.printStackTrace();

                sendHTML(exchange, buildPage(
                        "Text Similarity Checker",
                        "An error occurred while comparing the texts.",
                        "", "", null
                ));
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Text Similarity Checker is running.");
        System.out.println("Open http://localhost:" + PORT);
    }

    // =========================================================
    // BUILD HTML PAGE
    // =========================================================

    static String buildPage(
            String title,
            String message,
            String text1,
            String text2,
            Double similarity
    ) {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"en\">");
        html.append("<head>");

        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>");
        html.append(escapeHTML(title));
        html.append("</title>");

        html.append("<style>");

        html.append("* { box-sizing: border-box; }");

        html.append("body {");
        html.append("font-family: Arial, sans-serif;");
        html.append("background: #f2f4f8;");
        html.append("margin: 0;");
        html.append("padding: 30px 15px;");
        html.append("color: #222;");
        html.append("}");

        html.append(".container {");
        html.append("max-width: 900px;");
        html.append("margin: auto;");
        html.append("background: white;");
        html.append("padding: 30px;");
        html.append("border-radius: 12px;");
        html.append("box-shadow: 0 5px 20px rgba(0,0,0,0.10);");
        html.append("}");

        html.append("h1 { text-align: center; margin-top: 0; }");

        html.append(".message {");
        html.append("text-align: center;");
        html.append("color: #555;");
        html.append("margin-bottom: 25px;");
        html.append("}");

        html.append(".text-area { margin-bottom: 20px; }");

        html.append("label {");
        html.append("display: block;");
        html.append("font-weight: bold;");
        html.append("margin-bottom: 8px;");
        html.append("}");

        html.append("textarea {");
        html.append("width: 100%;");
        html.append("min-height: 160px;");
        html.append("padding: 12px;");
        html.append("border: 1px solid #ccc;");
        html.append("border-radius: 6px;");
        html.append("font-family: Arial, sans-serif;");
        html.append("font-size: 15px;");
        html.append("resize: vertical;");
        html.append("}");

        html.append("textarea:focus {");
        html.append("outline: none;");
        html.append("border-color: #356ae6;");
        html.append("}");

        html.append(".actions {");
        html.append("text-align: center;");
        html.append("margin-top: 20px;");
        html.append("}");

        html.append("button {");
        html.append("background: #356ae6;");
        html.append("color: white;");
        html.append("border: none;");
        html.append("border-radius: 6px;");
        html.append("padding: 13px 25px;");
        html.append("font-size: 16px;");
        html.append("cursor: pointer;");
        html.append("}");

        html.append("button:hover { background: #2454c6; }");

        html.append(".result {");
        html.append("margin-top: 30px;");
        html.append("padding: 25px;");
        html.append("background: #eef4ff;");
        html.append("border-radius: 8px;");
        html.append("text-align: center;");
        html.append("}");

        html.append(".score {");
        html.append("font-size: 38px;");
        html.append("font-weight: bold;");
        html.append("color: #2454c6;");
        html.append("margin: 12px 0;");
        html.append("}");

        html.append(".bar {");
        html.append("height: 12px;");
        html.append("background: #d7deeb;");
        html.append("border-radius: 10px;");
        html.append("overflow: hidden;");
        html.append("margin-top: 15px;");
        html.append("}");

        html.append(".fill {");
        html.append("height: 100%;");
        html.append("background: #356ae6;");
        html.append("}");

        html.append(".hint {");
        html.append("font-size: 13px;");
        html.append("color: #666;");
        html.append("text-align: center;");
        html.append("margin-top: 20px;");
        html.append("}");

        html.append("@media (max-width: 600px) {");
        html.append(".container { padding: 20px; }");
        html.append("body { padding: 15px 10px; }");
        html.append("}");

        html.append("</style>");
        html.append("</head>");

        html.append("<body>");
        html.append("<div class=\"container\">");

        html.append("<h1>");
        html.append(escapeHTML(title));
        html.append("</h1>");

        html.append("<div class=\"message\">");
        html.append(escapeHTML(message));
        html.append("</div>");

        html.append("<form method=\"POST\" action=\"/\">");

        html.append("<div class=\"text-area\">");
        html.append("<label for=\"text1\">First Text</label>");
        html.append("<textarea id=\"text1\" name=\"text1\" ");
        html.append("placeholder=\"Enter the first text...\" required>");
        html.append(escapeHTML(text1));
        html.append("</textarea>");
        html.append("</div>");

        html.append("<div class=\"text-area\">");
        html.append("<label for=\"text2\">Second Text</label>");
        html.append("<textarea id=\"text2\" name=\"text2\" ");
        html.append("placeholder=\"Enter the second text...\" required>");
        html.append(escapeHTML(text2));
        html.append("</textarea>");
        html.append("</div>");

        html.append("<div class=\"actions\">");
        html.append("<button type=\"submit\">Compare Texts</button>");
        html.append("</div>");

        html.append("</form>");

        if (similarity != null) {

            double percentage = similarity * 100.0;

            html.append("<div class=\"result\">");

            html.append("<h2>Similarity Result</h2>");

            html.append("<div class=\"score\">");
            html.append(String.format(
                    Locale.US, "%.2f%%", percentage
            ));
            html.append("</div>");

            html.append("<div>");
            html.append(getSimilarityDescription(percentage));
            html.append("</div>");

            html.append("<div class=\"bar\">");
            html.append("<div class=\"fill\" style=\"width:");
            html.append(String.format(
                    Locale.US, "%.2f%%", percentage
            ));
            html.append(";\"></div>");
            html.append("</div>");

            html.append("<p>");
            html.append("The score is based on the words shared by both texts.");
            html.append("</p>");

            html.append("</div>");
        }

        html.append("<div class=\"hint\">");
        html.append("Method: Jaccard Similarity");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    // =========================================================
    // CALCULATE JACCARD SIMILARITY
    // =========================================================

    static double calculateSimilarity(
            String text1,
            String text2
    ) {

        Set<String> words1 = getWords(text1);
        Set<String> words2 = getWords(text2);

        Set<String> union = new HashSet<String>(words1);
        union.addAll(words2);

        Set<String> intersection =
                new HashSet<String>(words1);

        intersection.retainAll(words2);

        if (union.isEmpty()) {
            return 1.0;
        }

        return (double) intersection.size() / union.size();
    }

    // =========================================================
    // EXTRACT UNIQUE WORDS
    // =========================================================

    static Set<String> getWords(String text) {

        Set<String> words = new HashSet<String>();

        if (text == null || text.trim().isEmpty()) {
            return words;
        }

        String cleaned = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^\\p{L}\\p{N}\\s]", " ")
                .trim();

        if (cleaned.isEmpty()) {
            return words;
        }

        String[] tokens = cleaned.split("\\s+");

        for (String word : tokens) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }

        return words;
    }

    // =========================================================
    // SIMILARITY DESCRIPTION
    // =========================================================

    static String getSimilarityDescription(double percentage) {

        if (percentage >= 80) {
            return "Very high similarity";
        } else if (percentage >= 60) {
            return "High similarity";
        } else if (percentage >= 40) {
            return "Moderate similarity";
        } else if (percentage >= 20) {
            return "Low similarity";
        } else {
            return "Very low similarity";
        }
    }

    // =========================================================
    // READ REQUEST BODY - JAVA 8 COMPATIBLE
    // =========================================================

    static String readRequestBody(InputStream input)
            throws IOException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        return new String(
                output.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    // =========================================================
    // FORM PARSER - FIXED FOR JAVA 8
    // =========================================================

    static Map<String, String> parseForm(String body)
            throws UnsupportedEncodingException {

        Map<String, String> result =
                new HashMap<String, String>();

        if (body == null || body.isEmpty()) {
            return result;
        }

        String[] pairs = body.split("&");

        for (String pair : pairs) {

            String[] parts = pair.split("=", 2);

            // FIX: use the String encoding overload.
            String key = URLDecoder.decode(
                    parts[0],
                    "UTF-8"
            );

            String value = "";

            if (parts.length > 1) {
                // FIX: use the String encoding overload.
                value = URLDecoder.decode(
                        parts[1],
                        "UTF-8"
                );
            }

            result.put(key, value);
        }

        return result;
    }

    // =========================================================
    // HTML ESCAPING
    // =========================================================

    static String escapeHTML(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // =========================================================
    // SEND HTML RESPONSE
    // =========================================================

    static void sendHTML(
            HttpExchange exchange,
            String html
    ) throws IOException {

        byte[] data = html.getBytes(
                StandardCharsets.UTF_8
        );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(200, data.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(data);
        }
    }
}
