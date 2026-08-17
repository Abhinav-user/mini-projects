import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ReactionTimeTester {

    static Random random = new Random();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", ReactionTimeTester::home);

        server.start();

        System.out.println("================================");
        System.out.println("     REACTION TIME TESTER");
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

                "<title>Reaction Time Tester</title>" +

                "<style>" +

                "body{" +
                "margin:0;" +
                "font-family:Arial;" +
                "background:#222;" +
                "color:white;" +
                "text-align:center;" +
                "}" +

                "#game{" +
                "height:100vh;" +
                "display:flex;" +
                "justify-content:center;" +
                "align-items:center;" +
                "flex-direction:column;" +
                "cursor:pointer;" +
                "}" +

                "h1{" +
                "font-size:40px;" +
                "}" +

                "#message{" +
                "font-size:25px;" +
                "margin:20px;" +
                "}" +

                "#time{" +
                "font-size:50px;" +
                "font-weight:bold;" +
                "}" +

                "button{" +
                "padding:15px 30px;" +
                "font-size:18px;" +
                "border:none;" +
                "border-radius:8px;" +
                "cursor:pointer;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div id='game' onclick='clicked()'>" +

                "<h1>Reaction Time Tester</h1>" +

                "<div id='message'>" +
                "Click START when you're ready" +
                "</div>" +

                "<div id='time'>---</div>" +

                "<br>" +

                "<button onclick='startGame(event)'>" +
                "START" +
                "</button>" +

                "</div>" +

                "<script>" +

                "let startTime = 0;" +

                "let timer = null;" +

                "let waiting = false;" +

                "let ready = false;" +

                "function startGame(event) {" +

                "event.stopPropagation();" +

                "if(waiting || ready) return;" +

                "document.getElementById('message').innerText =" +
                "'Wait for GREEN...';" +

                "document.getElementById('time').innerText =" +
                "'---';" +

                "document.getElementById('game').style.background =" +
                "'#cc3333';" +

                "waiting = true;" +

                "let delay = 2000 + Math.random() * 4000;" +

                "timer = setTimeout(function() {" +

                "document.getElementById('game').style.background =" +
                "'#22aa55';" +

                "document.getElementById('message').innerText =" +
                "'CLICK NOW!';" +

                "startTime = performance.now();" +

                "waiting = false;" +

                "ready = true;" +

                "}, delay);" +

                "}" +

                "function clicked() {" +

                "if(waiting) {" +

                "clearTimeout(timer);" +

                "waiting = false;" +

                "document.getElementById('game').style.background =" +
                "'#222';" +

                "document.getElementById('message').innerText =" +
                "'Too early! Click START and try again.';" +

                "return;" +

                "}" +

                "if(!ready) return;" +

                "let reaction =" +
                "Math.round(performance.now() - startTime);" +

                "document.getElementById('time').innerText =" +
                reaction + ' ms';" +

                "document.getElementById('message').innerText =" +
                getRating(reaction);" +

                "document.getElementById('game').style.background =" +
                "'#222';" +

                "ready = false;" +

                "}" +

                "function getRating(time) {" +

                "if(time < 200) return 'INSANE! ⚡';" +

                "if(time < 300) return 'Excellent! 🔥';" +

                "if(time < 400) return 'Good reaction! 👍';" +

                "if(time < 500) return 'Not bad!';" +

                "return 'You can do better 😭';" +

                "}" +

                "</script>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
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