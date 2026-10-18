
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

public class FileIntegrityChecker {

    static String html() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
        "<title>File Integrity Checker</title><style>" +
        "body{font-family:Arial;background:#10151d;color:#eee;padding:25px}" +
        ".box{max-width:700px;margin:auto;background:#202a38;padding:24px;" +
        "border-radius:14px}h1{color:#63e6be}input,button,textarea{" +
        "box-sizing:border-box;padding:12px;margin:8px 0;width:100%;" +
        "border-radius:7px;border:1px solid #526176;background:#111923;" +
        "color:white}button{background:#20a879;border:0;cursor:pointer}" +
        "textarea{height:100px;overflow-wrap:anywhere}" +
        ".hint{color:#aebdce;font-size:14px}" +
        "</style></head><body><div class='box'>" +
        "<h1>File Integrity Checker</h1>" +
        "<p>Calculate a SHA-256 fingerprint for a file and compare it " +
        "with a previously recorded fingerprint.</p>" +
        "<form id='form'>" +
        "<input type='file' id='file' required>" +
        "<button type='submit'>Calculate SHA-256</button></form>" +
        "<h3>File information</h3><div id='info'>Choose a file above.</div>" +
        "<h3>SHA-256 fingerprint</h3>" +
        "<textarea id='hash' readonly placeholder='Hash appears here'></textarea>" +
        "<button onclick='copyHash()'>Copy fingerprint</button>" +
        "<hr><h3>Verify a saved fingerprint</h3>" +
        "<textarea id='saved' placeholder='Paste the original SHA-256 hash here'></textarea>" +
        "<button onclick='compareHash()'>Compare fingerprints</button>" +
        "<p id='result'></p>" +
        "<p class='hint'>Files are processed locally by this Java server. " +
        "Only the selected file's hash and metadata are displayed.</p>" +
        "</div><script>" +
        "let currentHash='';" +
        "document.getElementById('form').onsubmit=async function(e){" +
        "e.preventDefault();let f=document.getElementById('file').files[0];" +
        "if(!f)return;let data=new FormData();data.append('file',f);" +
        "document.getElementById('info').textContent='Calculating...';" +
        "let response=await fetch('/hash',{method:'POST',body:data});" +
        "let result=await response.json();" +
        "if(!response.ok){document.getElementById('info').textContent=" +
        "result.error||'Failed';return;}" +
        "currentHash=result.hash;" +
        "document.getElementById('hash').value=result.hash;" +
        "document.getElementById('info').textContent=" +
        "'Name: '+result.name+' | Size: '+result.size+' bytes | Last modified: '+f.lastModified;" +
        "document.getElementById('result').textContent='';};" +
        "function compareHash(){" +
        "let old=document.getElementById('saved').value.trim().toLowerCase();" +
        "let out=document.getElementById('result');" +
        "if(!currentHash){out.textContent='Calculate a file hash first.';return;}" +
        "if(!/^[0-9a-f]{64}$/.test(old)){" +
        "out.textContent='Enter a valid 64-character SHA-256 hash.';return;}" +
        "out.textContent=old===currentHash?" +
        "'MATCH: the fingerprints are identical.' :" +
        "'MISMATCH: the fingerprints differ.';}" +
        "async function copyHash(){" +
        "let h=document.getElementById('hash').value;if(!h)return;" +
        "try{await navigator.clipboard.writeText(h);" +
        "document.getElementById('result').textContent='Fingerprint copied.';" +
        "}catch(e){document.getElementById('hash').select();" +
        "document.execCommand('copy');}}" +
        "</script></body></html>";
    }

    static String sha256(InputStream input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        int read;

        while ((read = input.read(buffer)) != -1) {
            digest.update(buffer, 0, read);
        }

        byte[] bytes = digest.digest();
        StringBuilder hex = new StringBuilder();

        for (byte b : bytes) {
            hex.append(String.format(Locale.ROOT, "%02x", b & 0xff));
        }

        return hex.toString();
    }

    static void send(HttpExchange ex, int status, String body,
                     String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        ex.sendResponseHeaders(status, bytes.length);

        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    static void home(HttpExchange ex) throws IOException {
        send(ex, 200, html(), "text/html; charset=UTF-8");
    }

    static void hashFile(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"POST required\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        /*
         * This simple implementation accepts a single file upload.
         * Limit upload size to reduce accidental memory exhaustion.
         */
        final int maxSize = 10 * 1024 * 1024;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int total = 0;
        int count;

        try (InputStream in = ex.getRequestBody()) {
            while ((count = in.read(chunk)) != -1) {
                total += count;

                if (total > maxSize) {
                    send(ex, 413, "{\"error\":\"File exceeds 10 MB limit\"}",
                         "application/json; charset=UTF-8");
                    return;
                }

                buffer.write(chunk, 0, count);
            }
        }

        String contentType = ex.getRequestHeaders()
                .getFirst("Content-Type");

        if (contentType == null ||
            !contentType.toLowerCase(Locale.ROOT)
                .startsWith("multipart/form-data")) {
            send(ex, 400, "{\"error\":\"Upload a file using the form\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        /*
         * Multipart parsing below extracts the uploaded file's bytes.
         * Browser uploads use CRLF line endings and a boundary.
         */
        String boundary = null;
        for (String part : contentType.split(";")) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                boundary = part.substring("boundary=".length());
                if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
                    boundary = boundary.substring(1, boundary.length() - 1);
                }
            }
        }

        if (boundary == null) {
            send(ex, 400, "{\"error\":\"Missing multipart boundary\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        byte[] body = buffer.toByteArray();
        byte[] marker = ("--" + boundary).getBytes(StandardCharsets.UTF_8);
        byte[] separator = "\r\n\r\n".getBytes(StandardCharsets.UTF_8);

        int headerStart = indexOf(body, marker, 0);
        int headerEnd = indexOf(body, separator, headerStart);

        if (headerStart < 0 || headerEnd < 0) {
            send(ex, 400, "{\"error\":\"Invalid upload\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        String headers = new String(body, headerStart,
                headerEnd - headerStart, StandardCharsets.UTF_8);

        if (!headers.contains("filename=")) {
            send(ex, 400, "{\"error\":\"No file found\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        int fileStart = headerEnd + separator.length;
        int fileEnd = indexOf(body, marker, fileStart);

        if (fileEnd < 0 || fileEnd < fileStart + 2) {
            send(ex, 400, "{\"error\":\"Invalid file data\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        fileEnd -= 2; // Exclude CRLF before the multipart boundary.

        byte[] fileBytes = new byte[fileEnd - fileStart];
        System.arraycopy(body, fileStart, fileBytes, 0, fileBytes.length);

        String filename = "uploaded-file";
        int nameStart = headers.indexOf("filename=\"");

        if (nameStart >= 0) {
            nameStart += "filename=\"".length();
            int nameEnd = headers.indexOf("\"", nameStart);

            if (nameEnd > nameStart) {
                filename = headers.substring(nameStart, nameEnd);
                filename = filename.replace("\\", "/");
                int slash = filename.lastIndexOf("/");
                if (slash >= 0) filename = filename.substring(slash + 1);
                filename = filename.replace("\"", "");
            }
        }

        try {
            String hash = sha256(new ByteArrayInputStream(fileBytes));

            String json = "{\"name\":\"" + escapeJson(filename) +
                    "\",\"size\":" + fileBytes.length +
                    ",\"hash\":\"" + hash + "\"}";

            send(ex, 200, json, "application/json; charset=UTF-8");
        } catch (Exception e) {
            send(ex, 500, "{\"error\":\"Could not calculate hash\"}",
                 "application/json; charset=UTF-8");
        }
    }

    static int indexOf(byte[] data, byte[] target, int from) {
        for (int i = Math.max(0, from);
             i <= data.length - target.length; i++) {
            boolean match = true;

            for (int j = 0; j < target.length; j++) {
                if (data[i + j] != target[j]) {
                    match = false;
                    break;
                }
            }

            if (match) return i;
        }

        return -1;
    }

    static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8080), 0);

        server.createContext("/", FileIntegrityChecker::home);
        server.createContext("/hash", FileIntegrityChecker::hashFile);
        server.setExecutor(null);
        server.start();

        System.out.println("File Integrity Checker started.");
        System.out.println("Open http://127.0.0.1:8080");
        System.out.println("Keep this terminal open while using it.");
    }
}
