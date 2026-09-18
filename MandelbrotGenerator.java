import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;

public class MandelbrotGenerator {

    static final int WIDTH = 700;
    static final int HEIGHT = 500;

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", MandelbrotGenerator::homePage);
        server.createContext("/fractal", MandelbrotGenerator::generateFractal);

        server.setExecutor(null);
        server.start();

        System.out.println("Mandelbrot Generator running...");
        System.out.println("Open: http://localhost:8080");
    }

    // ---------------- HOME PAGE ----------------

    static void homePage(HttpExchange exchange) throws IOException {

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Mandelbrot Fractal Generator</title>

                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #111;
                            color: white;
                            text-align: center;
                            padding: 30px;
                        }

                        .box {
                            max-width: 850px;
                            margin: auto;
                            background: #222;
                            padding: 25px;
                            border-radius: 15px;
                        }

                        input {
                            padding: 10px;
                            margin: 5px;
                            width: 100px;
                            border-radius: 6px;
                            border: none;
                        }

                        button {
                            padding: 11px 20px;
                            background: #00b894;
                            color: white;
                            border: none;
                            border-radius: 6px;
                            cursor: pointer;
                        }

                        button:hover {
                            background: #019875;
                        }

                        img {
                            margin-top: 25px;
                            width: 700px;
                            max-width: 100%;
                            border-radius: 10px;
                        }

                        .info {
                            color: #aaa;
                            margin-top: 15px;
                        }
                    </style>
                </head>

                <body>

                    <div class="box">

                        <h1>🌀 Mandelbrot Fractal Generator</h1>

                        <p>
                            Generate a fractal using complex-number mathematics.
                        </p>

                        <form onsubmit="generateFractal(event)">

                            <label>Center X:</label>
                            <input id="cx" type="number"
                                   value="-0.5" step="0.01">

                            <label>Center Y:</label>
                            <input id="cy" type="number"
                                   value="0" step="0.01">

                            <br><br>

                            <label>Zoom:</label>
                            <input id="zoom" type="number"
                                   value="1" step="0.1" min="0.1">

                            <label>Iterations:</label>
                            <input id="iterations" type="number"
                                   value="200" min="20" max="1000">

                            <br><br>

                            <button type="submit">
                                Generate Fractal
                            </button>

                        </form>

                        <div>
                            <img id="fractal"
                                 src="/fractal?cx=-0.5&cy=0&zoom=1&iterations=200">
                        </div>

                        <p class="info">
                            Higher zoom and iterations reveal more detail.
                        </p>

                    </div>

                    <script>

                        function generateFractal(event) {

                            event.preventDefault();

                            let cx = document.getElementById("cx").value;
                            let cy = document.getElementById("cy").value;
                            let zoom = document.getElementById("zoom").value;
                            let iterations =
                                document.getElementById("iterations").value;

                            let url =
                                "/fractal?cx=" + cx +
                                "&cy=" + cy +
                                "&zoom=" + zoom +
                                "&iterations=" + iterations;

                            document.getElementById("fractal").src = url;
                        }

                    </script>

                </body>
                </html>
                """;

        sendResponse(exchange, html, "text/html");
    }


    // ---------------- FRACTAL GENERATION ----------------

    static void generateFractal(HttpExchange exchange)
            throws IOException {

        String query = exchange.getRequestURI().getQuery();

        double cx = getDouble(query, "cx", -0.5);
        double cy = getDouble(query, "cy", 0);
        double zoom = getDouble(query, "zoom", 1);
        int maxIterations = getInt(query, "iterations", 200);

        BufferedImage image =
                new BufferedImage(
                        WIDTH,
                        HEIGHT,
                        BufferedImage.TYPE_INT_RGB
                );

        /*
         * Each pixel is converted into a point
         * on the complex plane.
         */

        for (int py = 0; py < HEIGHT; py++) {

            for (int px = 0; px < WIDTH; px++) {

                double x =
                        cx + (px - WIDTH / 2.0)
                        / (250.0 * zoom);

                double y =
                        cy + (py - HEIGHT / 2.0)
                        / (250.0 * zoom);

                double zx = 0;
                double zy = 0;

                int iteration = 0;

                /*
                 * Mandelbrot formula:
                 *
                 * z = z² + c
                 *
                 * where z and c are complex numbers.
                 */

                while (zx * zx + zy * zy <= 4
                        && iteration < maxIterations) {

                    double newZx =
                            zx * zx - zy * zy + x;

                    double newZy =
                            2 * zx * zy + y;

                    zx = newZx;
                    zy = newZy;

                    iteration++;
                }

                int rgb;

                if (iteration == maxIterations) {

                    // Point belongs to the Mandelbrot set
                    rgb = 0x000000;

                } else {

                    /*
                     * Convert iteration count
                     * into a color.
                     */

                    float hue =
                            (float) iteration / maxIterations;

                    rgb = java.awt.Color.HSBtoRGB(
                            hue,
                            0.8f,
                            1.0f
                    );
                }

                image.setRGB(px, py, rgb);
            }
        }

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        ImageIO.write(image, "PNG", output);

        byte[] data = output.toByteArray();

        exchange.getResponseHeaders()
                .set("Content-Type", "image/png");

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        OutputStream os = exchange.getResponseBody();

        os.write(data);
        os.close();
    }


    // ---------------- QUERY PARAMETERS ----------------

    static double getDouble(
            String query,
            String key,
            double defaultValue) {

        try {

            for (String parameter : query.split("&")) {

                String[] parts = parameter.split("=");

                if (parts.length == 2
                        && parts[0].equals(key)) {

                    return Double.parseDouble(
                            URLDecoder.decode(
                                    parts[1],
                                    "UTF-8"
                            )
                    );
                }
            }

        } catch (Exception e) {

            return defaultValue;
        }

        return defaultValue;
    }


    static int getInt(
            String query,
            String key,
            int defaultValue) {

        try {

            return (int) getDouble(
                    query,
                    key,
                    defaultValue
            );

        } catch (Exception e) {

            return defaultValue;
        }
    }


    // ---------------- RESPONSE ----------------

    static void sendResponse(
            HttpExchange exchange,
            String response,
            String contentType)
            throws IOException {

        byte[] data =
                response.getBytes("UTF-8");

        exchange.getResponseHeaders()
                .set("Content-Type",
                        contentType + "; charset=UTF-8");

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        OutputStream os =
                exchange.getResponseBody();

        os.write(data);
        os.close();
    }
}