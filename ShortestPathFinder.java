import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ShortestPathFinder {

    static Map<String, Map<String, Integer>> graph = new HashMap<>();

    public static void main(String[] args) throws Exception {

        addRoad("Chennai", "Tambaram", 25);
        addRoad("Chennai", "Guindy", 10);
        addRoad("Guindy", "Tambaram", 18);
        addRoad("Guindy", "Velachery", 8);
        addRoad("Velachery", "Tambaram", 12);
        addRoad("Tambaram", "Vandalur", 10);
        addRoad("Velachery", "Adyar", 7);
        addRoad("Adyar", "Guindy", 9);

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080), 0);

        server.createContext("/", ShortestPathFinder::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Shortest Path Finder started!");
        System.out.println("Open: http://localhost:8080");
    }

    static void addRoad(String a, String b, int distance) {

        graph.putIfAbsent(a, new HashMap<>());
        graph.putIfAbsent(b, new HashMap<>());

        graph.get(a).put(b, distance);
        graph.get(b).put(a, distance);
    }

    static void handle(HttpExchange exchange)
            throws IOException {

        String start = "";
        String end = "";
        String result = "";

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    exchange.getRequestBody(),
                                    StandardCharsets.UTF_8));

            String data = reader.readLine();

            if (data != null) {

                for (String part : data.split("&")) {

                    String[] pair = part.split("=", 2);

                    if (pair.length == 2) {

                        // Fixed for Java 8/11 compatibility
                        String key =
                                URLDecoder.decode(
                                        pair[0],
                                        "UTF-8");

                        String value =
                                URLDecoder.decode(
                                        pair[1],
                                        "UTF-8");

                        if (key.equals("start"))
                            start = value;

                        if (key.equals("end"))
                            end = value;
                    }
                }
            }

            if (graph.containsKey(start) &&
                    graph.containsKey(end)) {

                Path path = dijkstra(start, end);

                if (path != null) {

                    result =
                            "<div class='result'>" +
                            "<h2>Shortest Route</h2>" +
                            "<p><b>Route:</b> " +
                            String.join(" → ", path.nodes) +
                            "</p>" +
                            "<p><b>Total Distance:</b> " +
                            path.distance + " km</p>" +
                            "</div>";

                } else {

                    result =
                            "<div class='result'>" +
                            "No route found." +
                            "</div>";
                }

            } else {

                result =
                        "<div class='result'>" +
                        "Please select valid locations." +
                        "</div>";
            }
        }

        StringBuilder options =
                new StringBuilder();

        for (String place : graph.keySet()) {

            options.append("<option value='")
                    .append(place)
                    .append("'>")
                    .append(place)
                    .append("</option>");
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>Shortest Path Finder</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#eeeeee;" +
                "padding:40px;" +
                "}" +

                ".box{" +
                "max-width:600px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:30px;" +
                "border-radius:12px;" +
                "box-shadow:0 0 15px #bbb;" +
                "}" +

                "h1{text-align:center;}" +

                "label{" +
                "display:block;" +
                "margin-top:15px;" +
                "font-weight:bold;" +
                "}" +

                "select{" +
                "width:100%;" +
                "padding:12px;" +
                "margin-top:6px;" +
                "font-size:16px;" +
                "}" +

                "button{" +
                "width:100%;" +
                "padding:13px;" +
                "margin-top:20px;" +
                "background:#222;" +
                "color:white;" +
                "border:0;" +
                "border-radius:6px;" +
                "font-size:16px;" +
                "cursor:pointer;" +
                "}" +

                ".result{" +
                "margin-top:25px;" +
                "padding:20px;" +
                "background:#f5f5f5;" +
                "border-radius:8px;" +
                "line-height:1.8;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>🗺️ Shortest Path Finder</h1>" +

                "<p>" +
                "Find the shortest route between two locations." +
                "</p>" +

                "<form method='POST'>" +

                "<label>Starting Location</label>" +

                "<select name='start'>" +
                options +
                "</select>" +

                "<label>Destination</label>" +

                "<select name='end'>" +
                options +
                "</select>" +

                "<button type='submit'>" +
                "Find Shortest Route" +
                "</button>" +

                "</form>" +

                result +

                "</div>" +

                "</body>" +

                "</html>";

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8");

        byte[] response =
                html.getBytes(StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(
                200, response.length);

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    static Path dijkstra(
            String start,
            String end) {

        Map<String, Integer> distance =
                new HashMap<>();

        Map<String, String> previous =
                new HashMap<>();

        for (String node : graph.keySet()) {
            distance.put(node, Integer.MAX_VALUE);
        }

        distance.put(start, 0);

        PriorityQueue<Node> queue =
                new PriorityQueue<>(
                        Comparator.comparingInt(
                                n -> n.distance));

        queue.add(new Node(start, 0));

        while (!queue.isEmpty()) {

            Node current = queue.poll();

            if (current.distance >
                    distance.get(current.name)) {
                continue;
            }

            if (current.name.equals(end)) {
                break;
            }

            for (Map.Entry<String, Integer> edge :
                    graph.get(current.name).entrySet()) {

                String neighbor = edge.getKey();

                int newDistance =
                        current.distance +
                        edge.getValue();

                if (newDistance <
                        distance.get(neighbor)) {

                    distance.put(
                            neighbor,
                            newDistance);

                    previous.put(
                            neighbor,
                            current.name);

                    queue.add(
                            new Node(
                                    neighbor,
                                    newDistance));
                }
            }
        }

        if (distance.get(end) ==
                Integer.MAX_VALUE) {

            return null;
        }

        ArrayList<String> route =
                new ArrayList<>();

        String current = end;

        while (current != null) {

            route.add(current);
            current = previous.get(current);
        }

        Collections.reverse(route);

        return new Path(
                route,
                distance.get(end));
    }

    static class Node {

        String name;
        int distance;

        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }

    static class Path {

        ArrayList<String> nodes;
        int distance;

        Path(ArrayList<String> nodes,
             int distance) {

            this.nodes = nodes;
            this.distance = distance;
        }
    }
}