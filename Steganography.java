import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Steganography {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", Steganography::home);
        server.createContext("/hide", Steganography::hide);
        server.createContext("/extract", Steganography::extract);

        server.start();

        System.out.println("================================");
        System.out.println("       STEGANOGRAPHY TOOL");
        System.out.println("================================");
        System.out.println("Open: http://localhost:8080");
    }

    // ================= HOME =================

    static void home(HttpExchange exchange)
            throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Steganography Tool</title>" +

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
                "}" +

                ".box{" +
                "background:white;" +
                "padding:25px;" +
                "margin-bottom:25px;" +
                "border-radius:12px;" +
                "box-shadow:0 3px 10px #aaa;" +
                "}" +

                "input,textarea{" +
                "width:95%;" +
                "padding:12px;" +
                "margin:10px 0;" +
                "font-size:16px;" +
                "}" +

                "textarea{" +
                "height:100px;" +
                "resize:none;" +
                "}" +

                "button{" +
                "padding:12px 25px;" +
                "background:#222;" +
                "color:white;" +
                "border:none;" +
                "border-radius:6px;" +
                "cursor:pointer;" +
                "}" +

                "h1{text-align:center;}" +

                ".note{" +
                "color:#666;" +
                "font-size:14px;" +
                "}" +

                "</style>" +
                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🕵️ Steganography Tool</h1>" +

                "<div class='box'>" +

                "<h2>Hide Message</h2>" +

                "<form method='POST' " +
                "action='/hide' " +
                "enctype='multipart/form-data'>" +

                "<label>Choose PNG image:</label>" +
                "<input type='file' name='image' " +
                "accept='.png' required>" +

                "<textarea name='message' " +
                "placeholder='Enter secret message...' " +
                "required></textarea>" +

                "<button type='submit'>" +
                "Hide Message" +
                "</button>" +

                "</form>" +

                "<p class='note'>" +
                "Use PNG images for best results." +
                "</p>" +

                "</div>" +

                "<div class='box'>" +

                "<h2>Extract Message</h2>" +

                "<form method='POST' " +
                "action='/extract' " +
                "enctype='multipart/form-data'>" +

                "<label>Choose encoded PNG:</label>" +

                "<input type='file' name='image' " +
                "accept='.png' required>" +

                "<br>" +

                "<button type='submit'>" +
                "Extract Message" +
                "</button>" +

                "</form>" +

                "</div>" +

                "</div>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }

    // ================= HIDE MESSAGE =================

    static void hide(HttpExchange exchange)
            throws IOException {

        String contentType =
                exchange.getRequestHeaders()
                        .getFirst("Content-Type");

        if (contentType == null ||
                !contentType.contains("multipart/form-data")) {

            sendError(
                    exchange,
                    "Please upload an image."
            );

            return;
        }

        String boundary =
                contentType.substring(
                        contentType.indexOf("boundary=")
                                + 9
                );

        byte[] body =
                readBytes(exchange);

        String bodyText =
                new String(
                        body,
                        StandardCharsets.ISO_8859_1
                );

        String message =
                extractField(
                        bodyText,
                        "message"
                );

        byte[] imageData =
                extractFile(
                        body,
                        bodyText,
                        "image"
                );

        if (imageData == null) {

            sendError(
                    exchange,
                    "Image could not be read."
            );

            return;
        }

        if (message == null ||
                message.length() == 0) {

            sendError(
                    exchange,
                    "Message cannot be empty."
            );

            return;
        }

        ByteArrayInputStream imageInput =
                new ByteArrayInputStream(
                        imageData
                );

        BufferedImage image =
                ImageIO.read(imageInput);

        if (image == null) {

            sendError(
                    exchange,
                    "Invalid image."
            );

            return;
        }

        byte[] messageBytes =
                message.getBytes(
                        StandardCharsets.UTF_8
                );

        /*
         * Store:
         *
         * 4 bytes = message length
         * remaining = message
         */

        int totalBits =
                32 +
                messageBytes.length * 8;

        int availableBits =
                image.getWidth() *
                image.getHeight() *
                3;

        if (totalBits > availableBits) {

            sendError(
                    exchange,
                    "Message is too large for this image."
            );

            return;
        }

        int bitPosition = 0;

        // Store message length
        for (int i = 31; i >= 0; i--) {

            int bit =
                    (messageBytes.length >> i) & 1;

            bitPosition =
                    writeBit(
                            image,
                            bitPosition,
                            bit
                    );
        }

        // Store message
        for (byte b : messageBytes) {

            for (int i = 7; i >= 0; i--) {

                int bit =
                        (b >> i) & 1;

                bitPosition =
                        writeBit(
                                image,
                                bitPosition,
                                bit
                        );
            }
        }

        File output =
                new File(
                        System.getProperty("java.io.tmpdir"),
                        "secret_image.png"
                );

        ImageIO.write(
                image,
                "png",
                output
        );

        String base64 =
                Base64.getEncoder()
                        .encodeToString(
                                readFile(output)
                        );

        String html =
                "<html>" +
                "<head>" +
                "<title>Message Hidden</title>" +

                "<style>" +
                "body{" +
                "font-family:Arial;" +
                "text-align:center;" +
                "padding:50px;" +
                "background:#f2f2f2;" +
                "}" +

                ".box{" +
                "background:white;" +
                "padding:30px;" +
                "max-width:600px;" +
                "margin:auto;" +
                "border-radius:12px;" +
                "}" +

                "img{" +
                "max-width:100%;" +
                "margin:20px;" +
                "}" +

                "a,button{" +
                "padding:12px 20px;" +
                "background:#222;" +
                "color:white;" +
                "text-decoration:none;" +
                "border:none;" +
                "border-radius:5px;" +
                "}" +

                "</style>" +
                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>✅ Message Hidden</h1>" +

                "<p>" +
                "Your secret message has been " +
                "hidden inside the image." +
                "</p>" +

                "<img src='data:image/png;base64," +
                base64 +
                "'>" +

                "<br><br>" +

                "<a download='secret_image.png' " +
                "href='data:image/png;base64," +
                base64 +
                "'>" +
                "Download Image" +
                "</a>" +

                "<br><br>" +

                "<a href='/'>" +
                "Back" +
                "</a>" +

                "</div>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }

    // ================= EXTRACT MESSAGE =================

    static void extract(HttpExchange exchange)
            throws IOException {

        byte[] body =
                readBytes(exchange);

        String bodyText =
                new String(
                        body,
                        StandardCharsets.ISO_8859_1
                );

        byte[] imageData =
                extractFile(
                        body,
                        bodyText,
                        "image"
                );

        if (imageData == null) {

            sendError(
                    exchange,
                    "Image could not be read."
            );

            return;
        }

        BufferedImage image =
                ImageIO.read(
                        new ByteArrayInputStream(
                                imageData
                        )
                );

        if (image == null) {

            sendError(
                    exchange,
                    "Invalid image."
            );

            return;
        }

        int bitPosition = 0;

        int messageLength = 0;

        // Read 32-bit length
        for (int i = 31; i >= 0; i--) {

            int bit =
                    readBit(
                            image,
                            bitPosition
                    );

            bitPosition++;

            messageLength =
                    messageLength |
                    (bit << i);
        }

        int maxLength =
                (image.getWidth() *
                 image.getHeight() *
                 3 - 32) / 8;

        if (messageLength < 0 ||
                messageLength > maxLength) {

            sendError(
                    exchange,
                    "No valid hidden message found."
            );

            return;
        }

        byte[] messageBytes =
                new byte[messageLength];

        for (int i = 0;
             i < messageLength;
             i++) {

            int value = 0;

            for (int j = 7; j >= 0; j--) {

                int bit =
                        readBit(
                                image,
                                bitPosition
                        );

                bitPosition++;

                value =
                        value |
                        (bit << j);
            }

            messageBytes[i] =
                    (byte) value;
        }

        String message =
                new String(
                        messageBytes,
                        StandardCharsets.UTF_8
                );

        String html =
                "<html>" +
                "<head>" +
                "<title>Secret Message</title>" +

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

                ".message{" +
                "background:#eee;" +
                "padding:20px;" +
                "margin:20px;" +
                "font-size:20px;" +
                "word-wrap:break-word;" +
                "}" +

                "a{" +
                "display:inline-block;" +
                "padding:12px 20px;" +
                "background:#222;" +
                "color:white;" +
                "text-decoration:none;" +
                "border-radius:5px;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>🔓 Secret Message</h1>" +

                "<div class='message'>" +
                escapeHTML(message) +
                "</div>" +

                "<a href='/'>" +
                "Back" +
                "</a>" +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= WRITE BIT =================

    static int writeBit(
            BufferedImage image,
            int position,
            int bit) {

        int pixelIndex =
                position / 3;

        int channel =
                position % 3;

        int x =
                pixelIndex %
                image.getWidth();

        int y =
                pixelIndex /
                image.getWidth();

        int rgb =
                image.getRGB(x, y);

        int alpha =
                (rgb >> 24) & 255;

        int red =
                (rgb >> 16) & 255;

        int green =
                (rgb >> 8) & 255;

        int blue =
                rgb & 255;

        if (channel == 0) {

            red =
                    (red & 254) | bit;

        } else if (channel == 1) {

            green =
                    (green & 254) | bit;

        } else {

            blue =
                    (blue & 254) | bit;
        }

        int newRGB =
                (alpha << 24) |
                (red << 16) |
                (green << 8) |
                blue;

        image.setRGB(
                x,
                y,
                newRGB
        );

        return position + 1;
    }

    // ================= READ BIT =================

    static int readBit(
            BufferedImage image,
            int position) {

        int pixelIndex =
                position / 3;

        int channel =
                position % 3;

        int x =
                pixelIndex %
                image.getWidth();

        int y =
                pixelIndex /
                image.getWidth();

        int rgb =
                image.getRGB(x, y);

        if (channel == 0) {

            return (rgb >> 16) & 1;

        } else if (channel == 1) {

            return (rgb >> 8) & 1;

        } else {

            return rgb & 1;
        }
    }

    // ================= READ BYTES =================

    static byte[] readBytes(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        byte[] buffer =
                new byte[4096];

        int length;

        while ((length =
                input.read(buffer)) != -1) {

            output.write(
                    buffer,
                    0,
                    length
            );
        }

        return output.toByteArray();
    }

    // ================= EXTRACT FIELD =================

    static String extractField(
            String body,
            String field) {

        String marker =
                "name=\"" + field + "\"";

        int start =
                body.indexOf(marker);

        if (start == -1) {
            return null;
        }

        start =
                body.indexOf(
                        "\r\n\r\n",
                        start
                );

        if (start == -1) {
            return null;
        }

        start += 4;

        int end =
                body.indexOf(
                        "\r\n--",
                        start
                );

        if (end == -1) {
            return null;
        }

        String value =
                body.substring(
                        start,
                        end
                );

        try {

            return URLDecoder.decode(
                    value,
                    "UTF-8"
            );

        } catch (Exception e) {

            return value;
        }
    }

    // ================= EXTRACT FILE =================

    static byte[] extractFile(
            byte[] body,
            String text,
            String field) {

        String marker =
                "name=\"" +
                field +
                "\"";

        int header =
                text.indexOf(marker);

        if (header == -1) {
            return null;
        }

        int start =
                text.indexOf(
                        "\r\n\r\n",
                        header
                );

        if (start == -1) {
            return null;
        }

        start += 4;

        int end =
                text.indexOf(
                        "\r\n--",
                        start
                );

        if (end == -1) {
            return null;
        }

        return Arrays.copyOfRange(
                body,
                start,
                end
        );
    }

    // ================= READ FILE =================

    static byte[] readFile(File file)
            throws IOException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        FileInputStream input =
                new FileInputStream(file);

        byte[] buffer =
                new byte[4096];

        int length;

        while ((length =
                input.read(buffer)) != -1) {

            output.write(
                    buffer,
                    0,
                    length
            );
        }

        input.close();

        return output.toByteArray();
    }

    // ================= HTML =================

    static String escapeHTML(String text) {

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
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
                escapeHTML(message) +
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