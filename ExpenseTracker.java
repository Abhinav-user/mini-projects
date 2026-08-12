import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExpenseTracker {

    static ArrayList<Expense> expenses = new ArrayList<Expense>();

    // ---------------- EXPENSE CLASS ----------------

    static class Expense {
        private String name;
        private String category;
        private double amount;

        public Expense(String name, String category, double amount) {
            this.name = name;
            this.category = category;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public double getAmount() {
            return amount;
        }
    }

    // ---------------- MAIN ----------------

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", ExpenseTracker::handleRequest);

        server.start();

        System.out.println("================================");
        System.out.println("   EXPENSE TRACKER STARTED");
        System.out.println("================================");
        System.out.println("Open: http://localhost:8080");
    }

    // ---------------- REQUEST HANDLER ----------------

    static void handleRequest(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if (method.equalsIgnoreCase("POST")) {

            InputStream input = exchange.getRequestBody();

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

            Map<String, String> form =
                    parseForm(formData);

            try {

                String name = form.get("name");
                String category = form.get("category");
                double amount =
                        Double.parseDouble(form.get("amount"));

                expenses.add(
                        new Expense(name, category, amount)
                );

            } catch (Exception e) {

                System.out.println("Invalid expense data.");

            }

            exchange.getResponseHeaders().set(
                    "Location", "/"
            );

            exchange.sendResponseHeaders(302, -1);
            exchange.close();

        } else {

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
    }

    // ---------------- FORM DATA ----------------

    static Map<String, String> parseForm(String data)
            throws UnsupportedEncodingException {

        Map<String, String> result =
                new HashMap<String, String>();

        String[] pairs = data.split("&");

        for (String pair : pairs) {

            String[] parts = pair.split("=", 2);

            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(parts[0], "UTF-8");

                String value =
                        URLDecoder.decode(parts[1], "UTF-8");

                result.put(key, value);
            }
        }

        return result;
    }

    // ---------------- HTML ----------------

    static String createHTML() {

        double total = 0;

        StringBuilder rows =
                new StringBuilder();

        for (Expense expense : expenses) {

            total += expense.getAmount();

            rows.append("<tr>");

            rows.append("<td>")
                    .append(escape(expense.getName()))
                    .append("</td>");

            rows.append("<td>")
                    .append(escape(expense.getCategory()))
                    .append("</td>");

            rows.append("<td>₹")
                    .append(String.format(
                            "%.2f",
                            expense.getAmount()
                    ))
                    .append("</td>");

            rows.append("</tr>");
        }

        StringBuilder html =
                new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");

        html.append("<title>Expense Tracker</title>");

        html.append("<style>");

        html.append(
                "body {" +
                "font-family: Arial;" +
                "background: #f2f2f2;" +
                "margin: 40px;" +
                "}"
        );

        html.append(
                ".container {" +
                "max-width: 700px;" +
                "margin: auto;" +
                "background: white;" +
                "padding: 25px;" +
                "border-radius: 10px;" +
                "}"
        );

        html.append(
                "h1 {" +
                "text-align: center;" +
                "}"
        );

        html.append(
                "input, select, button {" +
                "padding: 10px;" +
                "margin: 5px;" +
                "}"
        );

        html.append(
                "button {" +
                "background: black;" +
                "color: white;" +
                "border: none;" +
                "cursor: pointer;" +
                "}"
        );

        html.append(
                "table {" +
                "width: 100%;" +
                "margin-top: 20px;" +
                "border-collapse: collapse;" +
                "}"
        );

        html.append(
                "th, td {" +
                "padding: 10px;" +
                "border-bottom: 1px solid #ddd;" +
                "text-align: left;" +
                "}"
        );

        html.append(
                ".total {" +
                "font-size: 20px;" +
                "font-weight: bold;" +
                "margin-top: 20px;" +
                "}"
        );

        html.append("</style>");
        html.append("</head>");

        html.append("<body>");

        html.append("<div class='container'>");

        html.append("<h1>Expense Tracker</h1>");

        html.append("<form method='POST'>");

        html.append(
                "<input type='text' " +
                "name='name' " +
                "placeholder='Expense name' " +
                "required>"
        );

        html.append("<select name='category'>");

        html.append("<option>Food</option>");
        html.append("<option>Travel</option>");
        html.append("<option>Shopping</option>");
        html.append("<option>Bills</option>");
        html.append("<option>Other</option>");

        html.append("</select>");

        html.append(
                "<input type='number' " +
                "name='amount' " +
                "placeholder='Amount' " +
                "step='0.01' " +
                "required>"
        );

        html.append(
                "<button type='submit'>" +
                "Add Expense" +
                "</button>"
        );

        html.append("</form>");

        html.append("<table>");

        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Category</th>");
        html.append("<th>Amount</th>");
        html.append("</tr>");

        html.append(rows.toString());

        html.append("</table>");

        html.append(
                "<div class='total'>" +
                "Total Spending: ₹" +
                String.format("%.2f", total) +
                "</div>"
        );

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    // ---------------- SECURITY ----------------

    static String escape(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}