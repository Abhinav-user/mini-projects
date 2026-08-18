import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MemoryGame {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", MemoryGame::home);

        server.start();

        System.out.println("================================");
        System.out.println("        MEMORY PATTERN GAME");
        System.out.println("================================");
        System.out.println("Open: http://localhost:8080");
    }

    static void home(HttpExchange exchange) throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +
                "<title>Memory Pattern Game</title>" +

                "<style>" +

                "body {" +
                "    margin: 0;" +
                "    font-family: Arial;" +
                "    background: #222;" +
                "    color: white;" +
                "    text-align: center;" +
                "}" +

                ".container {" +
                "    padding-top: 50px;" +
                "}" +

                "h1 {" +
                "    font-size: 40px;" +
                "}" +

                "#message {" +
                "    font-size: 22px;" +
                "    margin: 20px;" +
                "}" +

                "#score {" +
                "    font-size: 20px;" +
                "    margin: 15px;" +
                "}" +

                "#sequence {" +
                "    font-size: 45px;" +
                "    height: 70px;" +
                "}" +

                ".buttons {" +
                "    display: grid;" +
                "    grid-template-columns: 120px 120px;" +
                "    gap: 15px;" +
                "    justify-content: center;" +
                "    margin-top: 30px;" +
                "}" +

                ".color {" +
                "    width: 120px;" +
                "    height: 120px;" +
                "    border: none;" +
                "    border-radius: 15px;" +
                "    cursor: pointer;" +
                "    opacity: 0.85;" +
                "}" +

                ".color:hover {" +
                "    opacity: 1;" +
                "}" +

                ".red {" +
                "    background: #e74c3c;" +
                "}" +

                ".green {" +
                "    background: #2ecc71;" +
                "}" +

                ".blue {" +
                "    background: #3498db;" +
                "}" +

                ".yellow {" +
                "    background: #f1c40f;" +
                "}" +

                "#start {" +
                "    margin-top: 30px;" +
                "    padding: 12px 30px;" +
                "    font-size: 18px;" +
                "    border: none;" +
                "    border-radius: 8px;" +
                "    cursor: pointer;" +
                "}" +

                "</style>" +
                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🧠 Memory Game</h1>" +

                "<div id='message'>" +
                "Press START to begin" +
                "</div>" +

                "<div id='score'>" +
                "Score: 0" +
                "</div>" +

                "<div id='sequence'></div>" +

                "<div class='buttons'>" +

                "<button class='color red' " +
                "onclick=\"selectColor('red')\"></button>" +

                "<button class='color green' " +
                "onclick=\"selectColor('green')\"></button>" +

                "<button class='color blue' " +
                "onclick=\"selectColor('blue')\"></button>" +

                "<button class='color yellow' " +
                "onclick=\"selectColor('yellow')\"></button>" +

                "</div>" +

                "<button id='start' onclick='startGame()'>" +
                "START GAME" +
                "</button>" +

                "</div>" +

                "<script>" +

                "let pattern = [];" +
                "let playerIndex = 0;" +
                "let score = 0;" +
                "let playing = false;" +

                "const colors = [" +
                "'red'," +
                "'green'," +
                "'blue'," +
                "'yellow'" +
                "];" +

                "function startGame() {" +

                "pattern = [];" +
                "playerIndex = 0;" +
                "score = 0;" +
                "playing = false;" +

                "document.getElementById('score').innerText =" +
                "'Score: 0';" +

                "document.getElementById('message').innerText =" +
                "'Watch carefully...';" +

                "nextRound();" +

                "}" +

                "function nextRound() {" +

                "playerIndex = 0;" +

                "playing = false;" +

                "let randomColor =" +
                "colors[Math.floor(Math.random() * colors.length)];" +

                "pattern.push(randomColor);" +

                "showPattern();" +

                "}" +

                "function showPattern() {" +

                "let sequence = " +
                "document.getElementById('sequence');" +

                "let index = 0;" +

                "sequence.innerText = '';" +

                "let interval = setInterval(function() {" +

                "sequence.innerText = pattern[index];" +

                "index++;" +

                "if(index >= pattern.length) {" +

                "clearInterval(interval);" +

                "setTimeout(function() {" +

                "sequence.innerText = 'Your turn!';" +

                "playing = true;" +

                "}, 500);" +

                "}" +

                "}, 800);" +

                "}" +

                "function selectColor(color) {" +

                "if(!playing) return;" +

                "if(color === pattern[playerIndex]) {" +

                "playerIndex++;" +

                "if(playerIndex === pattern.length) {" +

                "score++;" +

                "document.getElementById('score').innerText =" +
                "'Score: ' + score;" +

                "playing = false;" +

                "document.getElementById('message').innerText =" +
                "'Correct! Next round...';" +

                "setTimeout(nextRound, 1000);" +

                "}" +

                "} else {" +

                "gameOver();" +

                "}" +

                "}" +

                "function gameOver() {" +

                "playing = false;" +

                "document.getElementById('message').innerText =" +
                "'Game Over! Final score: ' + score;" +

                "document.getElementById('sequence').innerText =" +
                "'💀';" +

                "}" +

                "</script>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    static void sendHTML(
            HttpExchange exchange,
            String html) throws IOException {

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