import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class AsciiArt {

    static final String CHARS = "@%#*+=-:. ";

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", AsciiArt::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("ASCII Art Converter started!");
        System.out.println("Open: http://localhost:8080");
    }

    static void handle(HttpExchange exchange)
            throws IOException {

        String ascii = "";

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            byte[] data =
                    readAllBytes(exchange.getRequestBody());

            try {

                BufferedImage image =
                        ImageIO.read(
                                new ByteArrayInputStream(data));

                if (image != null) {

                    ascii = convertToASCII(image);

                } else {

                    ascii =
                            "Invalid image file.";
                }

            } catch (Exception e) {

                ascii =
                        "Could not process image.";
            }
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>ASCII Art Converter</title>" +

                "<style>" +

                "body {" +
                "font-family: Arial;" +
                "background: #eeeeee;" +
                "padding: 40px;" +
                "}" +

                ".container {" +
                "max-width: 1000px;" +
                "margin: auto;" +
                "background: white;" +
                "padding: 30px;" +
                "border-radius: 12px;" +
                "box-shadow: 0 0 15px #bbb;" +
                "}" +

                "h1 {" +
                "text-align: center;" +
                "}" +

                ".upload {" +
                "text-align: center;" +
                "padding: 25px;" +
                "border: 2px dashed #999;" +
                "border-radius: 10px;" +
                "}" +

                "input {" +
                "margin: 15px;" +
                "}" +

                "button {" +
                "padding: 12px 25px;" +
                "background: #222;" +
                "color: white;" +
                "border: none;" +
                "border-radius: 6px;" +
                "cursor: pointer;" +
                "}" +

                ".output {" +
                "margin-top: 25px;" +
                "background: black;" +
                "color: white;" +
                "padding: 20px;" +
                "overflow: auto;" +
                "border-radius: 8px;" +
                "}" +

                "pre {" +
                "font-size: 8px;" +
                "line-height: 8px;" +
                "margin: 0;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🖼️ Image → ASCII Art</h1>" +

                "<p style='text-align:center'>" +
                "Upload an image and convert it into " +
                "ASCII characters." +
                "</p>" +

                "<div class='upload'>" +

                "<form method='POST' " +
                "enctype='application/octet-stream'>" +

                "<input type='file' " +
                "id='file' accept='image/*' required>" +

                "<br>" +

                "<button type='button' " +
                "onclick='uploadImage()'>" +
                "Convert to ASCII" +
                "</button>" +

                "</form>" +

                "</div>" +

                "<div class='output'>" +

                "<pre id='result'>" +
                escapeHTML(ascii) +
                "</pre>" +

                "</div>" +

                "</div>" +

                "<script>" +

                "async function uploadImage() {" +

                "const file = " +
                "document.getElementById('file').files[0];" +

                "if (!file) {" +
                "alert('Choose an image first.');" +
                "return;" +
                "}" +

                "const data = await file.arrayBuffer();" +

                "const response = await fetch('/', {" +
                "method: 'POST'," +
                "body: data" +
                "});" +

                "const text = await response.text();" +

                "document.getElementById('result')" +
                ".innerText = text;" +

                "}" +

                "</script>" +

                "</body>" +

                "</html>";

        // For POST requests, return only ASCII result.
        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            byte[] response =
                    ascii.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                    "Content-Type",
                    "text/plain; charset=UTF-8");

            exchange.sendResponseHeaders(
                    200,
                    response.length);

            OutputStream output =
                    exchange.getResponseBody();

            output.write(response);
            output.close();

            return;
        }

        byte[] response =
                html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
                200,
                response.length);

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    static String convertToASCII(
            BufferedImage image) {

        int originalWidth =
                image.getWidth();

        int originalHeight =
                image.getHeight();

        // Reduce image size so the ASCII
        // output remains readable.
        int width = 100;

        int height =
                (int) ((double) originalHeight /
                        originalWidth *
                        width *
                        0.45);

        if (height < 1) {
            height = 1;
        }

        StringBuilder result =
                new StringBuilder();

        for (int y = 0; y < height; y++) {

            for (int x = 0; x < width; x++) {

                int originalX =
                        x * originalWidth / width;

                int originalY =
                        y * originalHeight / height;

                int rgb =
                        image.getRGB(
                                originalX,
                                originalY);

                int red =
                        (rgb >> 16) & 255;

                int green =
                        (rgb >> 8) & 255;

                int blue =
                        rgb & 255;

                // Convert RGB to brightness.
                int brightness =
                        (int) (
                                0.299 * red +
                                0.587 * green +
                                0.114 * blue
                        );

                int index =
                        brightness *
                        (CHARS.length() - 1)
                        / 255;

                result.append(
                        CHARS.charAt(index));
            }

            result.append('\n');
        }

        return result.toString();
    }

    static byte[] readAllBytes(
            InputStream input)
            throws IOException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        byte[] buffer =
                new byte[8192];

        int bytesRead;

        while ((bytesRead =
                input.read(buffer)) != -1) {

            output.write(
                    buffer,
                    0,
                    bytesRead);
        }

        return output.toByteArray();
    }

    static String escapeHTML(
            String text) {

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}