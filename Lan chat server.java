import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class LANChat {

    static List<Message> messages =
            Collections.synchronizedList(new ArrayList<Message>());

    static Set<String> users =
            Collections.synchronizedSet(new HashSet<String>());

    static class Message {
        String username;
        String text;
        String time;

        Message(String username, String text) {
            this.username = username;
            this.text = text;
            this.time = new SimpleDateFormat(
                    "HH:mm:ss"
            ).format(new Date());
        }
    }

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", LANChat::home);
        server.createContext("/send", LANChat::sendMessage);
        server.createContext("/messages", LANChat::getMessages);

        server.start();

        System.out.println("==============================");
        System.out.println("       LAN CHAT SERVER");
        System.out.println("==============================");
        System.out.println("Open: http://localhost:8080");
        System.out.println("Press Ctrl+C to stop.");
    }

    // ================= HOME =================

    static void home(HttpExchange exchange)
            throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>LAN Chat</title>" +

                "<style>" +

                "body{" +
                "margin:0;" +
                "font-family:Arial;" +
                "background:#202124;" +
                "color:white;" +
                "}" +

                ".chat{" +
                "width:600px;" +
                "max-width:90%;" +
                "margin:40px auto;" +
                "background:#292a2d;" +
                "border-radius:12px;" +
                "overflow:hidden;" +
                "}" +

                ".header{" +
                "padding:20px;" +
                "background:#111;" +
                "text-align:center;" +
                "}" +

                "#messages{" +
                "height:400px;" +
                "overflow-y:auto;" +
                "padding:20px;" +
                "}" +

                ".message{" +
                "background:#3c4043;" +
                "padding:10px;" +
                "margin:8px 0;" +
                "border-radius:8px;" +
                "}" +

                ".username{" +
                "font-weight:bold;" +
                "}" +

                ".time{" +
                "font-size:11px;" +
                "color:#aaa;" +
                "margin-left:10px;" +
                "}" +

                ".inputArea{" +
                "display:flex;" +
                "padding:15px;" +
                "background:#111;" +
                "}" +

                "input{" +
                "padding:12px;" +
                "border:none;" +
                "border-radius:5px;" +
                "margin-right:8px;" +
                "}" +

                "#username{" +
                "width:100px;" +
                "}" +

                "#text{" +
                "flex:1;" +
                "}" +

                "button{" +
                "padding:12px 20px;" +
                "background:#4caf50;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "cursor:pointer;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='chat'>" +

                "<div class='header'>" +
                "<h2>💬 LAN Chat</h2>" +
                "<div id='online'>" +
                "Users online: 0" +
                "</div>" +
                "</div>" +

                "<div id='messages'>" +
                "</div>" +

                "<div class='inputArea'>" +

                "<input " +
                "id='username' " +
                "placeholder='Name'>" +

                "<input " +
                "id='text' " +
                "placeholder='Type a message...'>" +

                "<button onclick='send()'>" +
                "Send" +
                "</button>" +

                "</div>" +

                "</div>" +

                "<script>" +

                "let lastCount = 0;" +

                "function send() {" +

                "let username =" +
                "document.getElementById('username').value;" +

                "let text =" +
                "document.getElementById('text').value;" +

                "if(username.trim()==='' || text.trim()==='') {" +
                "return;" +
                "}" +

                "fetch('/send', {" +
                "method:'POST'," +
                "headers:{" +
                "'Content-Type':'application/x-www-form-urlencoded'" +
                "}," +
                "body:" +
                "'username=' + encodeURIComponent(username) +" +
                "'&text=' + encodeURIComponent(text)" +
                "});" +

                "document.getElementById('text').value='';" +

                "}" +

                "function loadMessages() {" +

                "fetch('/messages')" +
                ".then(response => response.json())" +
                ".then(data => {" +

                "let box =" +
                "document.getElementById('messages');" +

                "box.innerHTML='';" +

                "data.messages.forEach(message => {" +

                "let div =" +
                "document.createElement('div');" +

                "div.className='message';" +

                "div.innerHTML =" +
                "'<span class=\"username\">' +" +
                "escapeHTML(message.username) +" +
                "'</span>' +" +

                "'<span class=\"time\">' +" +
                "message.time +" +
                "'</span><br>' +" +

                "escapeHTML(message.text);" +

                "box.appendChild(div);" +

                "});" +

                "box.scrollTop = box.scrollHeight;" +

                "document.getElementById('online').innerText =" +
                "'Messages: ' + data.messages.length;" +

                "});" +

                "}" +

                "function escapeHTML(text) {" +

                "let div = document.createElement('div');" +
                "div.textContent = text;" +
                "return div.innerHTML;" +

                "}" +

                "setInterval(loadMessages, 1000);" +

                "loadMessages();" +

                "document.getElementById('text')" +
                ".addEventListener('keydown', function(event) {" +

                "if(event.key === 'Enter') send();" +

                "});" +

                "</script>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= SEND MESSAGE =================

    static void sendMessage(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String data =
                readRequest(exchange);

        String username =
                getValue(data, "username");

        String text =
                getValue(data, "text");

        if (username != null &&
                text != null &&
                !username.trim().isEmpty() &&
                !text.trim().isEmpty()) {

            users.add(username);

            messages.add(
                    new Message(
                            username,
                            text
                    )
            );
        }

        String response = "OK";

        exchange.sendResponseHeaders(
                200,
                response.length()
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(
                response.getBytes()
        );

        output.close();
    }

    // ================= GET MESSAGES =================

    static void getMessages(HttpExchange exchange)
            throws IOException {

        StringBuilder json =
                new StringBuilder();

        json.append("{\"messages\":[");

        synchronized (messages) {

            for (int i = 0;
                 i < messages.size();
                 i++) {

                Message message =
                        messages.get(i);

                if (i > 0) {
                    json.append(",");
                }

                json.append("{");

                json.append(
                        "\"username\":\""
                );

                json.append(
                        jsonEscape(message.username)
                );

                json.append("\",");

                json.append(
                        "\"text\":\""
                );

                json.append(
                        jsonEscape(message.text)
                );

                json.append("\",");

                json.append(
                        "\"time\":\""
                );

                json.append(
                        message.time
                );

                json.append("\"");

                json.append("}");
            }
        }

        json.append("]}");

        byte[] response =
                json.toString()
                        .getBytes(
                                StandardCharsets.UTF_8
                        );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json"
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

    // ================= READ REQUEST =================

    static String readRequest(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        byte[] data = new byte[1024];

        int bytesRead;

        while ((bytesRead =
                input.read(data)) != -1) {

            buffer.write(
                    data,
                    0,
                    bytesRead
            );
        }

        return new String(
                buffer.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    // ================= GET FORM VALUE =================

    static String getValue(
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

    // ================= JSON ESCAPE =================

    static String jsonEscape(String text) {

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
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