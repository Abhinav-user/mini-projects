import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FlashcardQuiz {

    static ArrayList<String[]> cards = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        // Default flashcards
        cards.add(new String[]{"What does CPU stand for?",
                "Central Processing Unit"});

        cards.add(new String[]{"What does RAM stand for?",
                "Random Access Memory"});

        cards.add(new String[]{"What does HTML stand for?",
                "HyperText Markup Language"});

        cards.add(new String[]{"What does SQL stand for?",
                "Structured Query Language"});

        cards.add(new String[]{"What does JVM stand for?",
                "Java Virtual Machine"});

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", FlashcardQuiz::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Flashcard Quiz started!");
        System.out.println("Open: http://localhost:8080");
    }

    static void handle(HttpExchange exchange)
            throws IOException {

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    exchange.getRequestBody(),
                                    StandardCharsets.UTF_8));

            String data = reader.readLine();

            if (data != null) {

                Map<String, String> form =
                        parseForm(data);

                String question =
                        form.get("question");

                String answer =
                        form.get("answer");

                if (question != null &&
                        answer != null &&
                        !question.trim().isEmpty() &&
                        !answer.trim().isEmpty()) {

                    cards.add(new String[]{
                            question,
                            answer
                    });
                }
            }
        }

        StringBuilder cardData =
                new StringBuilder();

        for (String[] card : cards) {

            cardData.append("[")
                    .append("\"")
                    .append(escape(card[0]))
                    .append("\",\"")
                    .append(escape(card[1]))
                    .append("\"")
                    .append("],");
        }

        String json = cardData.toString();

        if (json.endsWith(",")) {
            json = json.substring(
                    0, json.length() - 1);
        }

        String html =
                "<!DOCTYPE html>" +

                "<html>" +

                "<head>" +

                "<title>Flashcard Quiz</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#eee;" +
                "margin:0;" +
                "padding:30px;" +
                "}" +

                ".container{" +
                "max-width:700px;" +
                "margin:auto;" +
                "}" +

                "h1{" +
                "text-align:center;" +
                "}" +

                ".card{" +
                "background:white;" +
                "min-height:220px;" +
                "padding:30px;" +
                "border-radius:15px;" +
                "box-shadow:0 4px 15px #bbb;" +
                "text-align:center;" +
                "display:flex;" +
                "flex-direction:column;" +
                "justify-content:center;" +
                "}" +

                "#question{" +
                "font-size:25px;" +
                "font-weight:bold;" +
                "}" +

                "#answer{" +
                "font-size:20px;" +
                "margin-top:20px;" +
                "display:none;" +
                "}" +

                "button{" +
                "padding:12px 18px;" +
                "margin:8px;" +
                "border:0;" +
                "border-radius:6px;" +
                "cursor:pointer;" +
                "font-size:15px;" +
                "}" +

                ".buttons{" +
                "text-align:center;" +
                "margin-top:15px;" +
                "}" +

                ".add{" +
                "background:white;" +
                "padding:20px;" +
                "margin-top:25px;" +
                "border-radius:10px;" +
                "}" +

                "input{" +
                "width:95%;" +
                "padding:10px;" +
                "margin:6px 0;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🎯 Flashcard Quiz</h1>" +

                "<div class='card'>" +

                "<div id='question'></div>" +

                "<div id='answer'></div>" +

                "</div>" +

                "<div class='buttons'>" +

                "<button onclick='showAnswer()'>" +
                "Show Answer" +
                "</button>" +

                "<button onclick='nextCard()'>" +
                "Next Card ➡" +
                "</button>" +

                "</div>" +

                "<p id='counter' " +
                "style='text-align:center;'></p>" +

                "<div class='add'>" +

                "<h2>➕ Add Flashcard</h2>" +

                "<form method='POST'>" +

                "<input name='question' " +
                "placeholder='Enter question' required>" +

                "<input name='answer' " +
                "placeholder='Enter answer' required>" +

                "<button type='submit'>" +
                "Add Card" +
                "</button>" +

                "</form>" +

                "</div>" +

                "</div>" +

                "<script>" +

                "const cards=[" + json + "];" +

                "let current=0;" +

                "function loadCard(){" +

                "document.getElementById('question')" +
                ".innerText=cards[current][0];" +

                "document.getElementById('answer')" +
                ".innerText=cards[current][1];" +

                "document.getElementById('answer')" +
                ".style.display='none';" +

                "document.getElementById('counter')" +
                ".innerText='Card '+(current+1)" +
                "+' of '+cards.length;" +

                "}" +

                "function showAnswer(){" +

                "document.getElementById('answer')" +
                ".style.display='block';" +

                "}" +

                "function nextCard(){" +

                "current++;" +

                "if(current>=cards.length)" +
                "current=0;" +

                "loadCard();" +

                "}" +

                "loadCard();" +

                "</script>" +

                "</body>" +

                "</html>";

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8");

        byte[] response =
                html.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                200,
                response.length);

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    static Map<String, String> parseForm(
            String data) {

        Map<String, String> result =
                new HashMap<>();

        String[] pairs = data.split("&");

        for (String pair : pairs) {

            String[] parts = pair.split(
                    "=", 2);

            if (parts.length == 2) {

                String key =
                        URLDecoder.decode(
                                parts[0],
                                StandardCharsets.UTF_8);

                String value =
                        URLDecoder.decode(
                                parts[1],
                                StandardCharsets.UTF_8);

                result.put(key, value);
            }
        }

        return result;
    }

    static String escape(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }
}