import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class DNAAnalyzer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", DNAAnalyzer::home);
        server.createContext("/analyze", DNAAnalyzer::analyze);

        server.start();

        System.out.println("================================");
        System.out.println("       DNA SEQUENCE ANALYZER");
        System.out.println("================================");
        System.out.println("Open: http://localhost:8080");
    }

    // ================= HOME PAGE =================

    static void home(HttpExchange exchange) throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +
                "<title>DNA Analyzer</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "margin:0;" +
                "padding:40px;" +
                "}" +

                ".container{" +
                "max-width:700px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 4px 15px #aaa;" +
                "}" +

                "h1{" +
                "text-align:center;" +
                "}" +

                "textarea{" +
                "width:95%;" +
                "height:120px;" +
                "padding:12px;" +
                "font-size:18px;" +
                "resize:none;" +
                "}" +

                "button{" +
                "margin-top:15px;" +
                "padding:12px 25px;" +
                "background:#222;" +
                "color:white;" +
                "border:none;" +
                "border-radius:6px;" +
                "cursor:pointer;" +
                "font-size:16px;" +
                "}" +

                ".hint{" +
                "color:#666;" +
                "margin-top:15px;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🧬 DNA Sequence Analyzer</h1>" +

                "<p>" +
                "Enter a DNA sequence containing A, T, G and C." +
                "</p>" +

                "<form method='POST' action='/analyze'>" +

                "<textarea " +
                "name='dna' " +
                "placeholder='Example: ATGCGATACGCTT' " +
                "required></textarea>" +

                "<br>" +

                "<button type='submit'>" +
                "Analyze DNA" +
                "</button>" +

                "</form>" +

                "<p class='hint'>" +
                "Spaces and line breaks are automatically removed." +
                "</p>" +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= ANALYZE =================

    static void analyze(HttpExchange exchange)
            throws IOException {

        String data = readRequest(exchange);

        String dna = getFormValue(data, "dna");

        if (dna == null) {
            sendError(exchange, "No DNA sequence entered.");
            return;
        }

        // Remove spaces and line breaks
        dna = dna
                .replace(" ", "")
                .replace("\n", "")
                .replace("\r", "")
                .toUpperCase();

        // Validate sequence
        for (int i = 0; i < dna.length(); i++) {

            char c = dna.charAt(i);

            if (c != 'A' &&
                c != 'T' &&
                c != 'G' &&
                c != 'C') {

                sendError(
                        exchange,
                        "Invalid DNA character: " + c
                );

                return;
            }
        }

        int a = count(dna, 'A');
        int t = count(dna, 'T');
        int g = count(dna, 'G');
        int c = count(dna, 'C');

        int length = dna.length();

        double gcPercentage = 0;
        double atPercentage = 0;

        if (length > 0) {

            gcPercentage =
                    ((double) (g + c) / length) * 100;

            atPercentage =
                    ((double) (a + t) / length) * 100;
        }

        String complement =
                getComplement(dna);

        String reverse =
                new StringBuilder(dna)
                        .reverse()
                        .toString();

        String reverseComplement =
                new StringBuilder(complement)
                        .reverse()
                        .toString();

        boolean hasStartCodon =
                dna.contains("ATG");

        boolean hasStopCodon =
                dna.contains("TAA") ||
                dna.contains("TAG") ||
                dna.contains("TGA");

        // ================= RESULT =================

        String html =
                "<!DOCTYPE html>" +

                "<html>" +

                "<head>" +

                "<title>DNA Results</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "padding:40px;" +
                "}" +

                ".container{" +
                "max-width:800px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "}" +

                "h1{" +
                "text-align:center;" +
                "}" +

                ".dna{" +
                "background:#eee;" +
                "padding:15px;" +
                "word-wrap:break-word;" +
                "font-family:monospace;" +
                "font-size:17px;" +
                "}" +

                ".grid{" +
                "display:grid;" +
                "grid-template-columns:1fr 1fr;" +
                "gap:10px;" +
                "margin-top:20px;" +
                "}" +

                ".card{" +
                "background:#f5f5f5;" +
                "padding:15px;" +
                "border-radius:8px;" +
                "}" +

                ".value{" +
                "font-size:22px;" +
                "font-weight:bold;" +
                "}" +

                "a{" +
                "display:block;" +
                "text-align:center;" +
                "margin-top:25px;" +
                "padding:12px;" +
                "background:#222;" +
                "color:white;" +
                "text-decoration:none;" +
                "border-radius:6px;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🧬 DNA Analysis</h1>" +

                "<h3>Sequence</h3>" +

                "<div class='dna'>" +
                dna +
                "</div>" +

                "<div class='grid'>" +

                "<div class='card'>" +
                "Length<br>" +
                "<span class='value'>" +
                length +
                "</span>" +
                "</div>" +

                "<div class='card'>" +
                "Adenine (A)<br>" +
                "<span class='value'>" +
                a +
                "</span>" +
                "</div>" +

                "<div class='card'>" +
                "Thymine (T)<br>" +
                "<span class='value'>" +
                t +
                "</span>" +
                "</div>" +

                "<div class='card'>" +
                "Guanine (G)<br>" +
                "<span class='value'>" +
                g +
                "</span>" +
                "</div>" +

                "<div class='card'>" +
                "Cytosine (C)<br>" +
                "<span class='value'>" +
                c +
                "</span>" +
                "</div>" +

                "<div class='card'>" +
                "GC Content<br>" +
                "<span class='value'>" +
                String.format("%.2f", gcPercentage) +
                "%</span>" +
                "</div>" +

                "<div class='card'>" +
                "AT Content<br>" +
                "<span class='value'>" +
                String.format("%.2f", atPercentage) +
                "%</span>" +
                "</div>" +

                "<div class='card'>" +
                "Start Codon (ATG)<br>" +
                "<span class='value'>" +
                (hasStartCodon ? "YES" : "NO") +
                "</span>" +
                "</div>" +

                "<div class='card'>" +
                "Stop Codon<br>" +
                "<span class='value'>" +
                (hasStopCodon ? "YES" : "NO") +
                "</span>" +
                "</div>" +

                "</div>" +

                "<h3>Complement</h3>" +

                "<div class='dna'>" +
                complement +
                "</div>" +

                "<h3>Reverse</h3>" +

                "<div class='dna'>" +
                reverse +
                "</div>" +

                "<h3>Reverse Complement</h3>" +

                "<div class='dna'>" +
                reverseComplement +
                "</div>" +

                "<a href='/'>" +
                "Analyze Another Sequence" +
                "</a>" +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= COUNT =================

    static int count(String dna, char target) {

        int count = 0;

        for (int i = 0; i < dna.length(); i++) {

            if (dna.charAt(i) == target) {
                count++;
            }
        }

        return count;
    }

    // ================= COMPLEMENT =================

    static String getComplement(String dna) {

        StringBuilder result =
                new StringBuilder();

        for (int i = 0; i < dna.length(); i++) {

            char c = dna.charAt(i);

            switch (c) {

                case 'A':
                    result.append('T');
                    break;

                case 'T':
                    result.append('A');
                    break;

                case 'G':
                    result.append('C');
                    break;

                case 'C':
                    result.append('G');
                    break;
            }
        }

        return result.toString();
    }

    // ================= READ REQUEST =================

    static String readRequest(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        byte[] bytes = new byte[1024];

        int length;

        while ((length = input.read(bytes)) != -1) {

            buffer.write(
                    bytes,
                    0,
                    length
            );
        }

        return new String(
                buffer.toByteArray(),
                StandardCharsets.UTF_8
        );
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

    // ================= ERROR =================

    static void sendError(
            HttpExchange exchange,
            String message)
            throws IOException {

        String html =
                "<html>" +
                "<body style='" +
                "font-family:Arial;" +
                "text-align:center;" +
                "padding:50px;" +
                "'>" +

                "<h2>❌ " +
                message +
                "</h2>" +

                "<br>" +

                "<a href='/'>" +
                "Go Back" +
                "</a>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }
}