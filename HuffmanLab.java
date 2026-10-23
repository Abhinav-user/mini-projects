
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.HashMap;
import java.util.Map;

public class HuffmanLab {

    static class Node {
        int symbol;
        int frequency;
        int minSymbol;
        Node left, right;

        Node(int symbol, int frequency) {
            this.symbol = symbol;
            this.frequency = frequency;
            this.minSymbol = symbol;
        }

        Node(Node left, Node right) {
            this.symbol = -1;
            this.frequency = left.frequency + right.frequency;
            this.left = left;
            this.right = right;
            this.minSymbol = Math.min(left.minSymbol, right.minSymbol);
        }

        boolean isLeaf() {
            return left == null && right == null;
        }
    }

    static class Compression {
        String bits;
        String[] codes;
        int originalBytes;

        Compression(String bits, String[] codes, int originalBytes) {
            this.bits = bits;
            this.codes = codes;
            this.originalBytes = originalBytes;
        }
    }

    static void buildCodes(Node node, String prefix, String[] codes) {
        if (node == null) return;

        if (node.isLeaf()) {
            codes[node.symbol] = prefix.isEmpty() ? "0" : prefix;
            return;
        }

        buildCodes(node.left, prefix + "0", codes);
        buildCodes(node.right, prefix + "1", codes);
    }

    static Compression compress(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        int[] frequencies = new int[256];

        for (byte b : data) {
            frequencies[b & 255]++;
        }

        PriorityQueue<Node> queue = new PriorityQueue<>(
            Comparator.comparingInt((Node n) -> n.frequency)
                .thenComparingInt(n -> n.minSymbol)
        );

        for (int i = 0; i < 256; i++) {
            if (frequencies[i] > 0) {
                queue.add(new Node(i, frequencies[i]));
            }
        }

        String[] codes = new String[256];

        if (queue.isEmpty()) {
            return new Compression("", codes, 0);
        }

        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            queue.add(new Node(left, right));
        }

        buildCodes(queue.poll(), "", codes);

        StringBuilder bits = new StringBuilder();

        for (byte b : data) {
            bits.append(codes[b & 255]);
        }

        return new Compression(bits.toString(), codes, data.length);
    }

    static String serializeCodes(String[] codes) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < codes.length; i++) {
            if (codes[i] != null) {
                if (result.length() > 0) result.append(";");
                result.append(i).append(":").append(codes[i]);
            }
        }

        return result.toString();
    }

    static byte[] decompress(String bits, String serializedCodes)
            throws Exception {

        Map<String, Integer> reverse = new HashMap<>();

        if (!serializedCodes.isEmpty()) {
            String[] entries = serializedCodes.split(";");

            for (String entry : entries) {
                String[] parts = entry.split(":", 2);

                if (parts.length != 2) {
                    throw new Exception("Invalid code table.");
                }

                int symbol = Integer.parseInt(parts[0]);

                if (symbol < 0 || symbol > 255) {
                    throw new Exception("Invalid symbol.");
                }

                reverse.put(parts[1], symbol);
            }
        }

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < bits.length(); i++) {
            char bit = bits.charAt(i);

            if (bit != '0' && bit != '1') {
                throw new Exception("Encoded data must contain only 0s and 1s.");
            }

            current.append(bit);
            Integer symbol = reverse.get(current.toString());

            if (symbol != null) {
                output.write(symbol);
                current.setLength(0);
            }
        }

        if (current.length() != 0) {
            throw new Exception("Incomplete encoded data.");
        }

        return output.toByteArray();
    }

    static String readBody(HttpExchange exchange) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        InputStream input = exchange.getRequestBody();
        byte[] buffer = new byte[4096];
        int count;

        while ((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }

        return new String(output.toByteArray(), StandardCharsets.UTF_8);
    }

    static Map<String, String> parseForm(String body)
            throws UnsupportedEncodingException {

        Map<String, String> values = new HashMap<>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(parts[0], "UTF-8");
            String value = parts.length > 1
                ? URLDecoder.decode(parts[1], "UTF-8")
                : "";

            values.put(key, value);
        }

        return values;
    }

    static String escape(String value) {
        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
    }

    static void respond(HttpExchange exchange, String html)
            throws IOException {

        byte[] data = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
            "Content-Type", "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(200, data.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(data);
        }
    }

    static String pageStart(String title) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
            + "<title>" + escape(title) + "</title>"
            + "<style>"
            + "*{box-sizing:border-box}"
            + "body{margin:0;background:#101522;color:#edf2ff;"
            + "font-family:Arial,sans-serif;padding:28px 16px}"
            + ".container{max-width:850px;margin:auto}"
            + ".panel{background:#1a2233;border:1px solid #303c53;"
            + "border-radius:15px;padding:22px;margin:18px 0}"
            + "h1{font-size:30px;margin-bottom:8px}"
            + "h2{font-size:20px}"
            + ".muted{color:#aab6ce;line-height:1.6}"
            + "textarea,input{width:100%;padding:12px;border-radius:8px;"
            + "border:1px solid #43516c;background:#111827;color:#fff;"
            + "font:14px monospace;margin:8px 0 14px}"
            + "textarea{min-height:130px;resize:vertical}"
            + "button{background:#7c9cff;color:#101522;border:0;"
            + "padding:12px 18px;border-radius:8px;font-weight:bold;cursor:pointer}"
            + ".stats{display:flex;gap:12px;flex-wrap:wrap}"
            + ".stat{background:#111827;padding:14px;border-radius:10px;flex:1;min-width:150px}"
            + ".number{font-size:24px;font-weight:bold;margin-top:8px}"
            + ".bits{background:#111827;padding:14px;border-radius:8px;"
            + "overflow-wrap:anywhere;line-height:1.8;font-family:monospace}"
            + "table{width:100%;border-collapse:collapse}"
            + "th,td{text-align:left;padding:10px;border-bottom:1px solid #303c53}"
            + "code{color:#9fc2ff;overflow-wrap:anywhere}"
            + "a{color:#9fc2ff}"
            + "</style></head><body><div class='container'>";
    }

    static String pageEnd() {
        return "</div></body></html>";
    }

    static String home() {
        return pageStart("Huffman Compression Lab")
            + "<h1>Huffman Compression Lab</h1>"
            + "<p class='muted'>Explore lossless compression. "
            + "Huffman coding assigns shorter bit sequences to frequent bytes "
            + "and longer sequences to less frequent bytes.</p>"
            + "<div class='panel'><h2>Enter text to compress</h2>"
            + "<form method='POST' action='/compress'>"
            + "<textarea name='text' required "
            + "placeholder='Type or paste text here...'></textarea>"
            + "<button type='submit'>Compress Text</button>"
            + "</form></div>"
            + "<div class='panel'><h2>How it works</h2>"
            + "<p class='muted'>1. Count how often each byte appears.</p>"
            + "<p class='muted'>2. Build a Huffman tree by repeatedly joining "
            + "the two least frequent nodes.</p>"
            + "<p class='muted'>3. Use 0 for a left branch and 1 for a right branch.</p>"
            + "<p class='muted'>4. Decode by matching bit sequences to the code table.</p>"
            + "</div>" + pageEnd();
    }

    static String compressPage(String text) {
        Compression result = compress(text);
        int originalBits = result.originalBytes * 8;
        int compressedBits = result.bits.length();

        double reduction = originalBits == 0 ? 0
            : 100.0 * (originalBits - compressedBits) / originalBits;

        StringBuilder html = new StringBuilder(pageStart("Compression Results"));

        html.append("<h1>Compression Results</h1>")
            .append("<p class='muted'>The encoded payload is shown below. "
                + "The code-table overhead is not included in the bit comparison.</p>")
            .append("<div class='panel'><div class='stats'>")
            .append("<div class='stat'>Original data<div class='number'>")
            .append(originalBits).append(" bits</div></div>")
            .append("<div class='stat'>Encoded payload<div class='number'>")
            .append(compressedBits).append(" bits</div></div>")
            .append("<div class='stat'>Payload reduction<div class='number'>")
            .append(String.format(java.util.Locale.US, "%.1f%%", reduction))
            .append("</div></div></div></div>");

        html.append("<div class='panel'><h2>Original text</h2><div class='bits'>")
            .append(escape(text)).append("</div></div>");

        html.append("<div class='panel'><h2>Encoded bits</h2><div class='bits'>")
            .append(result.bits.isEmpty() ? "(empty input)" : result.bits)
            .append("</div></div>");

        html.append("<div class='panel'><h2>Huffman code table</h2>")
            .append("<table><tr><th>Byte</th><th>Frequency</th><th>Code</th></tr>");

        int[] frequencies = new int[256];
        for (byte b : text.getBytes(StandardCharsets.UTF_8)) {
            frequencies[b & 255]++;
        }

        for (int i = 0; i < 256; i++) {
            if (result.codes[i] != null) {
                String label = i >= 32 && i <= 126
                    ? "'" + (char) i + "'"
                    : String.format("0x%02X", i);

                html.append("<tr><td>").append(label)
                    .append("</td><td>").append(frequencies[i])
                    .append("</td><td><code>").append(result.codes[i])
                    .append("</code></td></tr>");
            }
        }

        String encoded = result.bits;
        String table = serializeCodes(result.codes);

        html.append("</table></div>")
            .append("<div class='panel'><h2>Decompress</h2>")
            .append("<form method='POST' action='/decompress'>")
            .append("<label>Encoded bits</label><textarea name='bits'>")
            .append(escape(encoded)).append("</textarea>")
            .append("<label>Code table (required to decode)</label>")
            .append("<textarea name='codes'>").append(escape(table))
            .append("</textarea><button type='submit'>Decompress Text</button>")
            .append("</form></div>")
            .append("<p><a href='/'>Try another input</a></p>");

        return html.append(pageEnd()).toString();
    }

    static String decompressPage(String bits, String codes) throws Exception {
        byte[] decoded = decompress(bits, codes);
        String text = new String(decoded, StandardCharsets.UTF_8);

        return pageStart("Decompression Results")
            + "<h1>Decompression Results</h1>"
            + "<div class='panel'><h2>Recovered text</h2><div class='bits'>"
            + escape(text) + "</div></div>"
            + "<div class='panel'><h2>Verification details</h2>"
            + "<p class='muted'>Recovered " + decoded.length
            + " bytes from " + bits.length() + " encoded bits.</p>"
            + "<p class='muted'>For valid data produced by this tool, the recovered "
            + "text should match the original.</p></div>"
            + "<p><a href='/'>Compress more text</a></p>"
            + pageEnd();
    }

    static String errorPage(String message) {
        return pageStart("Error")
            + "<h1>Could not process input</h1>"
            + "<div class='panel'>" + escape(message) + "</div>"
            + "<p><a href='/'>Return home</a></p>"
            + pageEnd();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/", exchange -> {
            try {
                if (!exchange.getRequestURI().getPath().equals("/")) {
                    respond(exchange, errorPage("Page not found."));
                    return;
                }

                if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                    respond(exchange, errorPage("Use the form to submit data."));
                    return;
                }

                respond(exchange, home());
            } catch (Exception e) {
                respond(exchange, errorPage(e.getMessage() == null
                    ? "Unexpected error." : e.getMessage()));
            }
        });

        server.createContext("/compress", exchange -> {
            try {
                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    respond(exchange, errorPage("Please submit the form."));
                    return;
                }

                Map<String, String> form = parseForm(readBody(exchange));
                String input = form.get("text");

                if (input == null) {
                    input = "";
                }

                respond(exchange, compressPage(input));
            } catch (Exception e) {
                respond(exchange, errorPage(e.getMessage() == null
                    ? "Compression failed." : e.getMessage()));
            }
        });

        server.createContext("/decompress", exchange -> {
            try {
                if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                    respond(exchange, errorPage("Please submit the form."));
                    return;
                }

                Map<String, String> form = parseForm(readBody(exchange));

                String bits = form.get("bits");
                String codes = form.get("codes");

                if (bits == null || codes == null) {
                    respond(exchange, errorPage("Missing encoded data or code table."));
                    return;
                }

                respond(exchange, decompressPage(bits, codes));
            } catch (Exception e) {
                respond(exchange, errorPage(e.getMessage() == null
                    ? "Decompression failed." : e.getMessage()));
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Huffman Compression Lab is running.");
        System.out.println("Open http://localhost:8080 in your browser.");
    }
}
