import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class NQueensVisualizer {

    static String page() {
        return "<!DOCTYPE html>" +
        "<html><head><meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
        "<title>N-Queens Visualizer</title>" +
        "<style>" +
        "body{font-family:Arial;background:#151821;color:#f1f2f6;" +
        "text-align:center;padding:20px}" +
        ".panel{max-width:650px;margin:auto;padding:22px;" +
        "background:#222735;border-radius:14px}" +
        "select,button{padding:10px 14px;margin:6px;border:0;" +
        "border-radius:7px;font-size:15px}" +
        "button{cursor:pointer;background:#7c5cff;color:white}" +
        "button:hover{background:#6244df}" +
        "#board{display:grid;margin:22px auto;width:min(90vw,480px);" +
        "aspect-ratio:1;border:4px solid #aeb5c5}" +
        ".cell{display:flex;align-items:center;justify-content:center;" +
        "font-size:clamp(18px,6vw,42px)}" +
        ".light{background:#e8dfc9}.dark{background:#718096}" +
        ".queen{color:#a31335;text-shadow:0 1px white}" +
        "#status{color:#b9c2d5;min-height:24px}" +
        "</style></head><body>" +

        "<div class='panel'>" +
        "<h1>&#9813; N-Queens Visualizer</h1>" +
        "<p>Place queens so none share a row, column, or diagonal.</p>" +

        "<label for='size'>Board size: </label>" +
        "<select id='size'>" +
        "<option>4</option><option>5</option><option>6</option>" +
        "<option>7</option><option selected>8</option>" +
        "<option>9</option><option>10</option><option>11</option>" +
        "<option>12</option></select><br>" +

        "<button onclick='solve()'>Find Solutions</button>" +
        "<button onclick='previous()'>Previous</button>" +
        "<button onclick='next()'>Next Solution</button>" +

        "<div id='board'></div>" +
        "<div id='status'>Choose a board size and solve.</div>" +
        "</div>" +

        "<script>" +
        "var solutions=[];var index=0;" +

        "async function solve(){" +
        "var n=document.getElementById('size').value;" +
        "document.getElementById('status').textContent='Solving...';" +
        "try{" +
        "var response=await fetch('/solve?n='+n);" +
        "if(!response.ok)throw new Error('Solver failed');" +
        "solutions=await response.json();index=0;draw();" +
        "}catch(e){" +
        "document.getElementById('status').textContent=" +
        "'Could not solve the puzzle. Try again.';" +
        "}}" +

        "function draw(){" +
        "var board=document.getElementById('board');" +
        "var n=Number(document.getElementById('size').value);" +
        "board.innerHTML='';" +
        "board.style.gridTemplateColumns='repeat('+n+',1fr)';" +
        "if(!solutions.length){" +
        "document.getElementById('status').textContent='No solutions found.';" +
        "return;}" +

        "var solution=solutions[index];" +
        "for(var r=0;r<n;r++){" +
        "for(var c=0;c<n;c++){" +
        "var cell=document.createElement('div');" +
        "cell.className='cell '+((r+c)%2===0?'light':'dark');" +
        "if(solution[r]===c){" +
        "var queen=document.createElement('span');" +
        "queen.className='queen';queen.textContent='\\u265B';" +
        "cell.appendChild(queen);}" +
        "board.appendChild(cell);" +
        "}}" +

        "document.getElementById('status').textContent=" +
        "'Solution '+(index+1)+' of '+solutions.length;" +
        "}" +

        "function next(){" +
        "if(!solutions.length)return;" +
        "index=(index+1)%solutions.length;draw();}" +

        "function previous(){" +
        "if(!solutions.length)return;" +
        "index=(index-1+solutions.length)%solutions.length;draw();}" +
        "</script></body></html>";
    }

    static void handleHome(HttpExchange ex) throws IOException {
        send(ex, page(), "text/html; charset=UTF-8");
    }

    static void handleSolve(HttpExchange ex) throws IOException {
        int n = 8;

        try {
            String query = ex.getRequestURI().getRawQuery();

            if (query != null) {
                for (String part : query.split("&")) {
                    String[] pair = part.split("=", 2);

                    if (pair.length == 2 && pair[0].equals("n")) {
                        n = Integer.parseInt(
                            URLDecoder.decode(pair[1], "UTF-8")
                        );
                    }
                }
            }
        } catch (Exception e) {
            n = 8;
        }

        if (n < 4 || n > 12) {
            byte[] error = "Invalid board size".getBytes(
                StandardCharsets.UTF_8
            );
            ex.sendResponseHeaders(400, error.length);
            try (OutputStream out = ex.getResponseBody()) {
                out.write(error);
            }
            return;
        }

        List<int[]> solutions = new ArrayList<int[]>();
        int[] queens = new int[n];
        Arrays.fill(queens, -1);

        backtrack(0, n, queens, solutions);

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < solutions.size(); i++) {
            if (i > 0) json.append(",");

            json.append("[");
            for (int j = 0; j < n; j++) {
                if (j > 0) json.append(",");
                json.append(solutions.get(i)[j]);
            }
            json.append("]");
        }
        json.append("]");

        send(ex, json.toString(), "application/json; charset=UTF-8");
    }

    static void backtrack(
            int row, int n, int[] queens, List<int[]> solutions) {

        if (row == n) {
            solutions.add(queens.clone());
            return;
        }

        for (int col = 0; col < n; col++) {
            if (safe(row, col, queens)) {
                queens[row] = col;
                backtrack(row + 1, n, queens, solutions);
                queens[row] = -1;
            }
        }
    }

    static boolean safe(int row, int col, int[] queens) {
        for (int previousRow = 0; previousRow < row; previousRow++) {
            int previousCol = queens[previousRow];

            if (previousCol == col ||
                Math.abs(previousCol - col) ==
                Math.abs(previousRow - row)) {
                return false;
            }
        }
        return true;
    }

    static void send(
            HttpExchange ex, String body, String contentType)
            throws IOException {

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.sendResponseHeaders(200, bytes.length);

        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );

        server.createContext("/", NQueensVisualizer::handleHome);
        server.createContext("/solve", NQueensVisualizer::handleSolve);
        server.setExecutor(null);
        server.start();

        System.out.println("N-Queens Visualizer running!");
        System.out.println("Open http://localhost:8080");
    }
}
