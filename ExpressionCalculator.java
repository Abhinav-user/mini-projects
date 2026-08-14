import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

public class ExpressionCalculator {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", ExpressionCalculator::home);
        server.createContext("/calculate", ExpressionCalculator::calculate);

        server.start();

        System.out.println("================================");
        System.out.println("     EXPRESSION CALCULATOR");
        System.out.println("================================");
        System.out.println("Open: http://localhost:8080");
    }

    // ================= HOME PAGE =================

    static void home(HttpExchange exchange) throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Expression Calculator</title>" +

                "<style>" +
                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "text-align:center;" +
                "padding:50px;" +
                "}" +

                ".calculator{" +
                "background:white;" +
                "width:500px;" +
                "margin:auto;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 4px 15px #aaa;" +
                "}" +

                "input{" +
                "width:90%;" +
                "padding:14px;" +
                "font-size:18px;" +
                "margin:15px 0;" +
                "}" +

                "button{" +
                "padding:12px 30px;" +
                "background:black;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "cursor:pointer;" +
                "font-size:16px;" +
                "}" +

                ".examples{" +
                "color:#555;" +
                "margin-top:20px;" +
                "}" +
                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='calculator'>" +

                "<h1>Expression Calculator</h1>" +

                "<p>Enter a mathematical expression</p>" +

                "<form method='POST' action='/calculate'>" +

                "<input " +
                "type='text' " +
                "name='expression' " +
                "placeholder='Example: 2 + 3 * (4 - 1)'" +
                "required>" +

                "<br>" +

                "<button type='submit'>Calculate</button>" +

                "</form>" +

                "<div class='examples'>" +
                "<b>Examples:</b><br><br>" +
                "10 + 5 * 2<br>" +
                "(20 + 10) / 5<br>" +
                "2 * (5 + 3) - 4<br>" +
                "100 / (5 + 5)" +
                "</div>" +

                "</div>" +

                "</body>" +
                "</html>";

        sendHTML(exchange, html);
    }

    // ================= CALCULATE =================

    static void calculate(HttpExchange exchange)
            throws IOException {

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

        String expression =
                getFormValue(formData, "expression");

        String result;

        try {

            double answer =
                    evaluate(expression);

            result =
                    "<h2>Result</h2>" +
                    "<div class='result'>" +
                    expression +
                    " = " +
                    answer +
                    "</div>";

        } catch (Exception e) {

            result =
                    "<h2>Error</h2>" +
                    "<div class='error'>" +
                    e.getMessage() +
                    "</div>";
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>Calculator Result</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#f2f2f2;" +
                "text-align:center;" +
                "padding:50px;" +
                "}" +

                ".box{" +
                "background:white;" +
                "width:500px;" +
                "margin:auto;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "}" +

                ".result{" +
                "font-size:25px;" +
                "margin:20px;" +
                "}" +

                ".error{" +
                "color:red;" +
                "margin:20px;" +
                "}" +

                "button{" +
                "padding:12px 25px;" +
                "background:black;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='box'>" +

                result +

                "<br>" +

                "<a href='/'><button>" +
                "Try Again" +
                "</button></a>" +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= EXPRESSION EVALUATOR =================

    static double evaluate(String expression) {

        if (expression == null ||
                expression.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Expression cannot be empty."
            );
        }

        Stack<Double> numbers =
                new Stack<Double>();

        Stack<Character> operators =
                new Stack<Character>();

        int i = 0;

        while (i < expression.length()) {

            char current =
                    expression.charAt(i);

            // Ignore spaces
            if (Character.isWhitespace(current)) {
                i++;
                continue;
            }

            // Number
            if (Character.isDigit(current)
                    || current == '.') {

                StringBuilder number =
                        new StringBuilder();

                while (i < expression.length()) {

                    char c =
                            expression.charAt(i);

                    if (Character.isDigit(c)
                            || c == '.') {

                        number.append(c);
                        i++;

                    } else {
                        break;
                    }
                }

                numbers.push(
                        Double.parseDouble(
                                number.toString()
                        )
                );

                continue;
            }

            // Opening bracket
            if (current == '(') {

                operators.push(current);
                i++;
                continue;
            }

            // Closing bracket
            if (current == ')') {

                while (!operators.isEmpty()
                        && operators.peek() != '(') {

                    applyOperation(
                            numbers,
                            operators
                    );
                }

                if (operators.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Mismatched parentheses."
                    );
                }

                operators.pop();

                i++;
                continue;
            }

            // Operator
            if (isOperator(current)) {

                while (!operators.isEmpty()
                        && operators.peek() != '('
                        && precedence(
                                operators.peek()
                        ) >= precedence(current)) {

                    applyOperation(
                            numbers,
                            operators
                    );
                }

                operators.push(current);

                i++;
                continue;
            }

            throw new IllegalArgumentException(
                    "Invalid character: " + current
            );
        }

        // Apply remaining operations
        while (!operators.isEmpty()) {

            if (operators.peek() == '(') {

                throw new IllegalArgumentException(
                        "Mismatched parentheses."
                );
            }

            applyOperation(
                    numbers,
                    operators
            );
        }

        if (numbers.size() != 1) {

            throw new IllegalArgumentException(
                    "Invalid expression."
            );
        }

        return numbers.pop();
    }

    // ================= APPLY OPERATION =================

    static void applyOperation(
            Stack<Double> numbers,
            Stack<Character> operators) {

        if (numbers.size() < 2) {

            throw new IllegalArgumentException(
                    "Invalid expression."
            );
        }

        double second =
                numbers.pop();

        double first =
                numbers.pop();

        char operator =
                operators.pop();

        double result;

        switch (operator) {

            case '+':

                result = first + second;
                break;

            case '-':

                result = first - second;
                break;

            case '*':

                result = first * second;
                break;

            case '/':

                if (second == 0) {

                    throw new ArithmeticException(
                            "Cannot divide by zero."
                    );
                }

                result = first / second;
                break;

            default:

                throw new IllegalArgumentException(
                        "Unknown operator."
                );
        }

        numbers.push(result);
    }

    // ================= OPERATOR CHECK =================

    static boolean isOperator(char c) {

        return c == '+' ||
                c == '-' ||
                c == '*' ||
                c == '/';
    }

    // ================= PRECEDENCE =================

    static int precedence(char operator) {

        if (operator == '+' ||
                operator == '-') {

            return 1;
        }

        if (operator == '*' ||
                operator == '/') {

            return 2;
        }

        return 0;
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
}