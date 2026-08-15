import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class SmartFileOrganizer {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", SmartFileOrganizer::home);
        server.createContext("/organize", SmartFileOrganizer::organize);

        server.start();

        System.out.println("================================");
        System.out.println("     SMART FILE ORGANIZER");
        System.out.println("================================");
        System.out.println("Open: http://localhost:8080");
    }

    // ================= HOME PAGE =================

    static void home(HttpExchange exchange) throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Smart File Organizer</title>" +

                "<style>" +
                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "padding:50px;" +
                "text-align:center;" +
                "}" +

                ".box{" +
                "background:white;" +
                "max-width:600px;" +
                "margin:auto;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 4px 15px #aaa;" +
                "}" +

                "input{" +
                "width:90%;" +
                "padding:12px;" +
                "margin:15px 0;" +
                "font-size:16px;" +
                "}" +

                "button{" +
                "padding:12px 25px;" +
                "background:#222;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "cursor:pointer;" +
                "font-size:16px;" +
                "}" +

                ".info{" +
                "text-align:left;" +
                "margin-top:25px;" +
                "line-height:1.8;" +
                "}" +
                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>Smart File Organizer</h1>" +

                "<p>" +
                "Automatically organize files into folders " +
                "based on their extensions." +
                "</p>" +

                "<form method='POST' action='/organize'>" +

                "<input " +
                "type='text' " +
                "name='path' " +
                "placeholder='Enter folder path' " +
                "required>" +

                "<br>" +

                "<button type='submit'>" +
                "Organize Files" +
                "</button>" +

                "</form>" +

                "<div class='info'>" +

                "<b>Supported categories:</b><br>" +
                "Images → JPG, PNG, GIF<br>" +
                "Documents → PDF, DOC, DOCX, TXT<br>" +
                "Videos → MP4, AVI, MKV<br>" +
                "Audio → MP3, WAV<br>" +
                "Archives → ZIP, RAR, 7Z<br>" +
                "Others → Everything else" +

                "</div>" +

                "</div>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }

    // ================= ORGANIZE =================

    static void organize(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendMessage(
                    exchange,
                    "Invalid request."
            );

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

            buffer.write(
                    data,
                    0,
                    bytesRead
            );
        }

        String formData =
                new String(
                        buffer.toByteArray(),
                        StandardCharsets.UTF_8
                );

        String pathText =
                getFormValue(
                        formData,
                        "path"
                );

        if (pathText == null ||
                pathText.trim().isEmpty()) {

            sendMessage(
                    exchange,
                    "Folder path cannot be empty."
            );

            return;
        }

        Path folder =
                Paths.get(pathText);

        // Check whether folder exists
        if (!Files.exists(folder)) {

            sendMessage(
                    exchange,
                    "Folder does not exist."
            );

            return;
        }

        if (!Files.isDirectory(folder)) {

            sendMessage(
                    exchange,
                    "The path is not a folder."
            );

            return;
        }

        int movedFiles = 0;

        int skippedFiles = 0;

        Map<String, Integer> categories =
                new LinkedHashMap<String, Integer>();

        try {

            DirectoryStream<Path> files =
                    Files.newDirectoryStream(folder);

            for (Path file : files) {

                if (!Files.isRegularFile(file)) {
                    continue;
                }

                String fileName =
                        file.getFileName()
                                .toString();

                String extension =
                        getExtension(fileName);

                String category =
                        getCategory(extension);

                Path targetFolder =
                        folder.resolve(category);

                // Don't move files already inside
                // category folders
                if (file.getParent()
                        .equals(targetFolder)) {

                    continue;
                }

                try {

                    Files.createDirectories(
                            targetFolder
                    );

                    Path target =
                            targetFolder.resolve(
                                    fileName
                            );

                    // Avoid overwriting existing files
                    target =
                            getUniqueFileName(
                                    target
                            );

                    Files.move(
                            file,
                            target
                    );

                    movedFiles++;

                    if (!categories.containsKey(
                            category)) {

                        categories.put(
                                category,
                                0
                        );
                    }

                    categories.put(
                            category,
                            categories.get(
                                    category
                            ) + 1
                    );

                } catch (Exception e) {

                    skippedFiles++;
                }
            }

            files.close();

        } catch (Exception e) {

            sendMessage(
                    exchange,
                    "Error reading folder: " +
                    e.getMessage()
            );

            return;
        }

        // ================= RESULT PAGE =================

        StringBuilder result =
                new StringBuilder();

        result.append(
                "<h1>Organization Complete</h1>"
        );

        result.append(
                "<h2>Files moved: "
        );

        result.append(movedFiles);

        result.append("</h2>");

        result.append(
                "<h3>Categories</h3>"
        );

        result.append("<ul>");

        for (Map.Entry<String, Integer> entry :
                categories.entrySet()) {

            result.append("<li>");

            result.append(
                    entry.getKey()
            );

            result.append(
                    " → "
            );

            result.append(
                    entry.getValue()
            );

            result.append(
                    " file(s)"
            );

            result.append("</li>");
        }

        result.append("</ul>");

        if (skippedFiles > 0) {

            result.append(
                    "<p>Skipped files: "
            );

            result.append(
                    skippedFiles
            );

            result.append("</p>");
        }

        result.append(
                "<br><a href='/'>" +
                "<button>Organize Another Folder</button>" +
                "</a>"
        );

        String html =
                "<!DOCTYPE html>" +

                "<html>" +

                "<head>" +

                "<title>Organization Complete</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "text-align:center;" +
                "padding:50px;" +
                "}" +

                ".box{" +
                "background:white;" +
                "max-width:600px;" +
                "margin:auto;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "}" +

                "li{" +
                "list-style:none;" +
                "padding:8px;" +
                "}" +

                "button{" +
                "padding:12px 25px;" +
                "background:#222;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='box'>" +

                result.toString() +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= CATEGORY =================

    static String getCategory(String extension) {

        extension =
                extension.toLowerCase();

        if (extension.equals("jpg") ||
                extension.equals("jpeg") ||
                extension.equals("png") ||
                extension.equals("gif") ||
                extension.equals("bmp") ||
                extension.equals("webp")) {

            return "Images";
        }

        if (extension.equals("pdf") ||
                extension.equals("doc") ||
                extension.equals("docx") ||
                extension.equals("txt") ||
                extension.equals("xls") ||
                extension.equals("xlsx") ||
                extension.equals("ppt") ||
                extension.equals("pptx")) {

            return "Documents";
        }

        if (extension.equals("mp4") ||
                extension.equals("avi") ||
                extension.equals("mkv") ||
                extension.equals("mov") ||
                extension.equals("wmv")) {

            return "Videos";
        }

        if (extension.equals("mp3") ||
                extension.equals("wav") ||
                extension.equals("aac") ||
                extension.equals("flac")) {

            return "Audio";
        }

        if (extension.equals("zip") ||
                extension.equals("rar") ||
                extension.equals("7z") ||
                extension.equals("tar") ||
                extension.equals("gz")) {

            return "Archives";
        }

        if (extension.equals("java") ||
                extension.equals("py") ||
                extension.equals("cpp") ||
                extension.equals("c") ||
                extension.equals("js") ||
                extension.equals("html") ||
                extension.equals("css")) {

            return "Code";
        }

        return "Others";
    }

    // ================= EXTENSION =================

    static String getExtension(String fileName) {

        int position =
                fileName.lastIndexOf(".");

        if (position == -1 ||
                position == fileName.length() - 1) {

            return "";
        }

        return fileName.substring(
                position + 1
        );
    }

    // ================= UNIQUE FILE NAME =================

    static Path getUniqueFileName(Path target) {

        if (!Files.exists(target)) {
            return target;
        }

        String fileName =
                target.getFileName().toString();

        String extension =
                getExtension(fileName);

        String name;

        if (extension.isEmpty()) {

            name = fileName;

        } else {

            name =
                    fileName.substring(
                            0,
                            fileName.length()
                                    - extension.length()
                                    - 1
                    );
        }

        int counter = 1;

        Path newTarget;

        do {

            String newName;

            if (extension.isEmpty()) {

                newName =
                        name +
                        "_" +
                        counter;

            } else {

                newName =
                        name +
                        "_" +
                        counter +
                        "." +
                        extension;
            }

            newTarget =
                    target.getParent()
                            .resolve(newName);

            counter++;

        } while (Files.exists(newTarget));

        return newTarget;
    }

    // ================= FORM DATA =================

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

    // ================= ERROR MESSAGE =================

    static void sendMessage(
            HttpExchange exchange,
            String message)
            throws IOException {

        String html =
                "<html>" +
                "<head>" +
                "<title>Error</title>" +
                "</head>" +

                "<body style='" +
                "font-family:Arial;" +
                "text-align:center;" +
                "padding:50px;" +
                "'>" +

                "<h2>" +
                message +
                "</h2>" +

                "<br>" +

                "<a href='/'>" +
                "Go Back" +
                "</a>" +

                "</body>" +
                "</html>";

        sendHTML(
                exchange,
                html
        );
    }
}