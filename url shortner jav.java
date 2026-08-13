import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class URLShortener {

    // Stores short code -> original URL
    static HashMap<String, String> urls = new HashMap<String, String>();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", URLShortener::handleHome);
        server.createContext("/shorten", URLShortener::handleShorten);
        server.createContext("/go", URLShortener::handleRedirect);

        server.start();

        System.out.println("==============================");
        System.out.println("     URL SHORTENER STARTED");
        System.out.println("==============================");
        System.out.println("Open: http://localhost:8080");
    }

    // ================= HOME PAGE =================

    static void handleHome(HttpExchange exchange)
            throws IOException {

        String html = createHTML();

        byte[] response =
                html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                200,
                response.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    // ================= SHORTEN URL =================

    static void handleShorten(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        // Read form data
        InputStream input =
                exchange.getRequestBody();

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        byte[] data = new byte[1024];

        int bytesRead;

        while ((bytesRead = input.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        String formData =
                new String(
                        buffer.toByteArray(),
                        StandardCharsets.UTF_8
                );

        String originalURL =
                getFormValue(formData, "url");

        if (originalURL == null ||
                originalURL.trim().isEmpty()) {

            sendMessage(exchange,
                    "Invalid URL!");

            return;
        }

        // Generate unique short code
        String code = generateCode();

        urls.put(code, originalURL);

        String shortURL =
                "http://localhost:8080/go/" + code;

        String html =
                "<html>" +
                "<head>" +
                "<title>URL Shortened</title>" +
                "<style>" +
                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "text-align:center;" +
                "padding:50px;" +
                "}" +

                ".box{" +
                "background:white;" +
                "padding:30px;" +
                "max-width:600px;" +
                "margin:auto;" +
                "border-radius:10px;" +
                "}" +

                "a{" +
                "color:blue;" +
                "}" +

                "button{" +
                "padding:10px 20px;" +
                "}" +

                "</style>" +
                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>URL Shortened!</h1>" +

                "<p>Your short URL:</p>" +

                "<h3>" +
                "<a href='" + shortURL + "'>" +
                shortURL +
                "</a>" +
                "</h3>" +

                "<br>" +

                "<a href='/'>" +
                "<button>Shorten Another URL</button>" +
                "</a>" +

                "</div>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }

    // ================= REDIRECT =================

    static void handleRedirect(HttpExchange exchange)
            throws IOException {

        String path =
                exchange.getRequestURI().getPath();

        // Example:
        // /go/Ab12X

        String code =
                path.replace("/go/", "");

        String originalURL =
                urls.get(code);

        if (originalURL == null) {

            sendMessage(
                    exchange,
                    "Short URL not found!"
            );

            return;
        }

        // Redirect to original URL
        exchange.getResponseHeaders().set(
                "Location",
                originalURL
        );

        exchange.sendResponseHeaders(
                302,
                -1
        );

        exchange.close();
    }

    // ================= GENERATE CODE =================

    static String generateCode() {

        String characters =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                "abcdefghijklmnopqrstuvwxyz" +
                "0123456789";

        Random random = new Random();

        String code;

        do {

            StringBuilder result =
                    new StringBuilder();

            for (int i = 0; i < 6; i++) {

                int index =
                        random.nextInt(
                                characters.length()
                        );

                result.append(
                        characters.charAt(index)
                );
            }

            code = result.toString();

        } while (urls.containsKey(code));

        return code;
    }

    // ================= FORM VALUE =================

    static String getFormValue(
            String data,
            String key)
            throws UnsupportedEncodingException {

        String[] pairs =
                data.split("&");

        for (String pair : pairs) {

            String[] parts =
                    pair.split("=", 2);

            if (parts.length == 2) {

                String name =
                        URLDecoder.decode(
                                parts[0],
                                "UTF-8"
                        );

                String value =
                        URLDecoder.decode(
                                parts[1],
                                "UTF-8"
                        );

                if (name.equals(key)) {
                    return value;
                }
            }
        }

        return null;
    }

    // ================= HTML PAGE =================

    static String createHTML() {

        StringBuilder html =
                new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");

        html.append("<head>");

        html.append(
                "<title>URL Shortener</title>"
        );

        html.append("<style>");

        html.append(
                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "margin:0;" +
                "padding:50px;" +
                "}"
        );

        html.append(
                ".container{" +
                "max-width:600px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "text-align:center;" +
                "}"
        );

        html.append(
                "input{" +
                "width:90%;" +
                "padding:12px;" +
                "margin:10px;" +
                "border:1px solid #ccc;" +
                "border-radius:5px;" +
                "}"
        );

        html.append(
                "button{" +
                "padding:12px 25px;" +
                "background:black;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "cursor:pointer;" +
                "}"
        );

        html.append("</style>");

        html.append("</head>");

        html.append("<body>");

        html.append(
                "<div class='container'>"
        );

        html.append(
                "<h1>🔗 URL Shortener</h1>"
        );

        html.append(
                "<p>Enter a long URL to create a short link.</p>"
        );

        html.append(
                "<form method='POST' action='/shorten'>"
        );

        html.append(
                "<input " +
                "type='url' " +
                "name='url' " +
                "placeholder='https://example.com/very/long/url' " +
                "required>"
        );

        html.append("<br>");

        html.append(
                "<button type='submit'>" +
                "Shorten URL" +
                "</button>"
        );

        html.append("</form>");

        html.append(
                "<hr>"
        );

        html.append(
                "<h3>Shortened URLs</h3>"
        );

        if (urls.isEmpty()) {

            html.append(
                    "<p>No URLs created yet.</p>"
            );

        } else {

            html.append("<ul>");

            for (String code : urls.keySet()) {

                html.append("<li>");

                html.append(
                        "<a href='/go/" +
                        code +
                        "'>"
                );

                html.append(
                        "http://localhost:8080/go/" +
                        code
                );

                html.append("</a>");

                html.append("</li>");
            }

            html.append("</ul>");
        }

        html.append("</div>");

        html.append("</body>");

        html.append("</html>");

        return html.toString();
    }

    // ================= SEND HTML =================

    static void sendHTML(
            HttpExchange exchange,
            String html)
            throws IOException {

        byte[] response =
                html.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                200,
                response.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    // ================= SEND MESSAGE =================

    static void sendMessage(
            HttpExchange exchange,
            String message)
            throws IOException {

        String html =
                "<html>" +
                "<body style='font-family:Arial;" +
                "text-align:center;padding:50px;'>" +

                "<h2>" +
                message +
                "</h2>" +

                "<a href='/'>Go Back</a>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }
}