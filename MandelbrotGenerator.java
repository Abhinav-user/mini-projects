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

        String html =
                "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Mandelbrot Fractal Generator</title>\n" +
                "\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            background: #111;\n" +
                "            color: white;\n" +
                "            text-align: center;\n" +
                "            padding: 30px;\n" +
                "        }\n" +
                "\n" +
                "        .box {\n" +
                "            max-width: 850px;\n" +
                "            margin: auto;\n" +
                "            background: #222;\n" +
                "            padding: 25px;\n" +
                "            border-radius: 15px;\n" +
                "        }\n" +
                "\n" +
                "        input {\n" +
                "            padding: 10px;\n" +
                "            margin: 5px;\n" +
                "            width: 100px;\n" +
                "            border-radius: 6px;\n" +
                "            border: none;\n" +
                "        }\n" +
                "\n" +
                "        button {\n" +
                "            padding: 11px 20px;\n" +
                "            background: #00b894;\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            border-radius: 6px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "\n" +
                "        button:hover {\n" +
                "            background: #019875;\n" +
                "        }\n" +
                "\n" +
                "        img {\n" +
                "            margin-top: 25px;\n" +
                "            width: 700px;\n" +
                "            max-width: 100%;\n" +
                "            border-radius: 10px;\n" +
                "        }\n" +
                "\n" +
                "        .info {\n" +
                "            color: #aaa;\n" +
                "            margin-top: 15px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "    <div class=\"box\">\n" +
                "\n" +
                "        <h1>🌀 Mandelbrot Fractal Generator</h1>\n" +
                "\n" +
                "        <p>\n" +
                "            Generate a fractal using complex-number mathematics.\n" +
                "        </p>\n" +
                "\n" +
                "        <form onsubmit=\"generateFractal(event)\">\n" +
                "\n" +
                "            <label>Center X:</label>\n" +
                "            <input id=\"cx\" type=\"number\"\n" +
                "                   value=\"-0.5\" step=\"0.01\">\n" +
                "\n" +
                "            <label>Center Y:</label>\n" +
                "            <input id=\"cy\" type=\"number\"\n" +
                "                   value=\"0\" step=\"0.01\">\n" +
                "\n" +
                "            <br><br>\n" +
                "\n" +
                "            <label>Zoom:</label>\n" +
                "            <input id=\"zoom\" type=\"number\"\n" +
                "                   value=\"1\" step=\"0.1\" min=\"0.1\">\n" +
                "\n" +
                "            <label>Iterations:</label>\n" +
                "            <input id=\"iterations\" type=\"number\"\n" +
                "                   value=\"200\" min=\"20\" max=\"1000\">\n" +
                "\n" +
                "            <br><br>\n" +
                "\n" +
                "            <button type=\"submit\">\n" +
                "                Generate Fractal\n" +
                "            </button>\n" +
                "\n" +
                "        </form>\n" +
                "\n" +
                "        <div>\n" +
                "            <img id=\"fractal\"\n" +
                "                 src=\"/fractal?cx=-0.5&cy=0&zoom=1&iterations=200\">\n" +
                "        </div>\n" +
                "\n" +
                "        <p class=\"info\">\n" +
                "            Higher zoom and iterations reveal more detail.\n" +
                "        </p>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "\n" +
                "        function generateFractal(event) {\n" +
                "\n" +
                "            event.preventDefault();\n" +
                "\n" +
                "            let cx = document.getElementById(\"cx\").value;\n" +
                "            let cy = document.getElementById(\"cy\").value;\n" +
                "            let zoom = document.getElementById(\"zoom\").value;\n" +
                "            let iterations =\n" +
                "                document.getElementById(\"iterations\").value;\n" +
                "\n" +
                "            let url =\n" +
                "                \"/fractal?cx=\" + cx +\n" +
                "                \"&cy=\" + cy +\n" +
                "                \"&zoom=\" + zoom +\n" +
                "                \"&iterations=\" + iterations;\n" +
                "\n" +
                "            document.getElementById(\"fractal\").src = url;\n" +
                "        }\n" +
                "\n" +
                "    </script>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";;

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