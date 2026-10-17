
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GameOfLife {
    static final int SIZE = 30;
    static boolean[][] cells = new boolean[SIZE][SIZE];

    static synchronized String boardJson() {
        StringBuilder s = new StringBuilder("[");
        for (int r = 0; r < SIZE; r++) {
            if (r > 0) s.append(",");
            s.append("[");
            for (int c = 0; c < SIZE; c++) {
                if (c > 0) s.append(",");
                s.append(cells[r][c] ? "1" : "0");
            }
            s.append("]");
        }
        return s.append("]").toString();
    }

    static synchronized void step() {
        boolean[][] next = new boolean[SIZE][SIZE];

        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int neighbours = 0;

                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;

                        int nr = r + dr;
                        int nc = c + dc;

                        if (nr >= 0 && nr < SIZE &&
                            nc >= 0 && nc < SIZE &&
                            cells[nr][nc]) {
                            neighbours++;
                        }
                    }
                }

                next[r][c] = cells[r][c]
                    ? neighbours == 2 || neighbours == 3
                    : neighbours == 3;
            }
        }

        cells = next;
    }

    static synchronized void randomize() {
        Random random = new Random();
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                cells[r][c] = random.nextDouble() < 0.3;
    }

    static synchronized void clear() {
        cells = new boolean[SIZE][SIZE];
    }

    static synchronized void toggle(int r, int c) {
        if (r >= 0 && r < SIZE && c >= 0 && c < SIZE)
            cells[r][c] = !cells[r][c];
    }

    static void send(HttpExchange ex, String body, String type)
            throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("Cache-Control", "no-store");
        ex.sendResponseHeaders(200, data.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(data);
        }
    }

    static Map<String, String> params(HttpExchange ex) {
        Map<String, String> result = new HashMap<String, String>();
        String query = ex.getRequestURI().getRawQuery();

        if (query != null) {
            for (String part : query.split("&")) {
                String[] pair = part.split("=", 2);
                if (pair.length == 2) {
                    try {
                        result.put(
                            pair[0],
                            java.net.URLDecoder.decode(pair[1], "UTF-8")
                        );
                    } catch (Exception ignored) { }
                }
            }
        }
        return result;
    }

    static void handle(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();

        if (path.equals("/board")) {
            send(ex, boardJson(), "application/json; charset=UTF-8");
            return;
        }

        if (path.equals("/step")) {
            step();
            send(ex, boardJson(), "application/json; charset=UTF-8");
            return;
        }

        if (path.equals("/random")) {
            randomize();
            send(ex, boardJson(), "application/json; charset=UTF-8");
            return;
        }

        if (path.equals("/clear")) {
            clear();
            send(ex, boardJson(), "application/json; charset=UTF-8");
            return;
        }

        if (path.equals("/toggle")) {
            Map<String, String> p = params(ex);
            try {
                toggle(Integer.parseInt(p.get("r")),
                       Integer.parseInt(p.get("c")));
            } catch (Exception ignored) { }
            send(ex, boardJson(), "application/json; charset=UTF-8");
            return;
        }

        String html =
            "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
            "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
            "<title>Game of Life</title><style>" +
            "body{background:#10151d;color:#e8eef5;font-family:Arial;" +
            "text-align:center;padding:20px}" +
            ".panel{max-width:650px;margin:auto;background:#1d2733;" +
            "padding:20px;border-radius:14px}" +
            "#grid{display:grid;grid-template-columns:repeat(30,1fr);" +
            "gap:2px;width:min(90vw,540px);margin:20px auto}" +
            ".cell{aspect-ratio:1;background:#344454;border-radius:2px;" +
            "cursor:pointer}.alive{background:#48e0a4}" +
            "button{margin:4px;padding:10px 13px;border:0;" +
            "border-radius:6px;cursor:pointer;background:#48e0a4}" +
            "input{vertical-align:middle}" +
            "</style></head><body><div class='panel'>" +
            "<h1>Conway's Game of Life</h1>" +
            "<p>Click cells to edit the board, then start the simulation.</p>" +
            "<button onclick='start()'>Start</button>" +
            "<button onclick='stop()'>Pause</button>" +
            "<button onclick='randomBoard()'>Randomize</button>" +
            "<button onclick='clearBoard()'>Clear</button><br><br>" +
            "<label>Speed: <input id='speed' type='range' min='50' " +
            "max='1000' value='250' step='50'></label>" +
            "<p id='status'>Generation: 0</p><div id='grid'></div>" +
            "</div><script>" +
            "const N=30;let running=false,timer=null,generation=0;" +
            "const grid=document.getElementById('grid');" +
            "for(let r=0;r<N;r++)for(let c=0;c<N;c++){" +
            "let d=document.createElement('div');d.className='cell';" +
            "d.onclick=()=>request('/toggle?r='+r+'&c='+c);grid.appendChild(d);}" +
            "async function request(url){" +
            "let response=await fetch(url);let data=await response.json();" +
            "render(data);}" +
            "function render(data){" +
            "let i=0,count=0;" +
            "for(let r=0;r<N;r++)for(let c=0;c<N;c++){" +
            "let alive=data[r][c]===1;" +
            "grid.children[i++].className='cell'+(alive?' alive':'');" +
            "if(alive)count++;}" +
            "document.getElementById('status').textContent=" +
            "'Generation: '+generation+' | Live cells: '+count;}" +
            "async function tick(){if(!running)return;" +
            "await request('/step');generation++;" +
            "if(running)timer=setTimeout(tick," +
            "Number(document.getElementById('speed').value));}" +
            "function start(){if(running)return;running=true;tick();}" +
            "function stop(){running=false;clearTimeout(timer);}" +
            "async function randomBoard(){stop();generation=0;" +
            "await request('/random');}" +
            "async function clearBoard(){stop();generation=0;" +
            "await request('/clear');}" +
            "request('/board');" +
            "</script></body></html>";

        send(ex, html, "text/html; charset=UTF-8");
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0
        );
        server.createContext("/", GameOfLife::handle);
        server.setExecutor(null);
        server.start();

        System.out.println("Game of Life running at http://localhost:8080");
    }
}
