
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class KnightsTour {

    static final int N = 8;
    static int[][] board = new int[N][N];

    static final int[] dr = {-2, -1, 1, 2, 2, 1, -1, -2};
    static final int[] dc = {1, 2, 2, 1, -1, -2, -2, -1};

    static boolean valid(int r, int c) {
        return r >= 0 && r < N && c >= 0 && c < N
            && board[r][c] == -1;
    }

    static boolean solve(int r, int c, int move) {
        board[r][c] = move;

        if (move == N * N - 1) {
            return true;
        }

        for (int i = 0; i < 8; i++) {
            int nr = r + dr[i];
            int nc = c + dc[i];

            if (valid(nr, nc) && solve(nr, nc, move + 1)) {
                return true;
            }
        }

        board[r][c] = -1;
        return false;
    }

    static int[][] findTour(int startRow, int startCol) {
        board = new int[N][N];

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                board[r][c] = -1;
            }
        }

        if (!solve(startRow, startCol, 0)) {
            return null;
        }

        int[][] result = new int[N * N][2];

        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                int move = board[r][c];
                result[move][0] = r;
                result[move][1] = c;
            }
        }

        return result;
    }

    static String readBody(HttpExchange ex) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count;

        while ((count = ex.getRequestBody().read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    static String page(String result) {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
            + "<title>Knight's Tour</title><style>"
            + "body{background:#101522;color:#eef2ff;font-family:Arial;"
            + "text-align:center;padding:20px}"
            + "#board{display:grid;grid-template-columns:repeat(8,1fr);"
            + "width:min(90vw,480px);margin:20px auto;border:3px solid #65718a}"
            + ".sq{aspect-ratio:1;display:flex;align-items:center;"
            + "justify-content:center;font-size:clamp(14px,4vw,26px);"
            + "position:relative;cursor:pointer}"
            + ".light{background:#e5e7eb;color:#182033}"
            + ".dark{background:#596b83;color:white}"
            + ".visited{background:#b7e4c7;color:#183a2a}"
            + ".current{background:#ffcf70;color:#352400}"
            + ".num{position:absolute;bottom:2px;right:3px;font-size:9px}"
            + "button,select{padding:10px;margin:5px;border:0;border-radius:7px;"
            + "background:#86a7ff;color:#101522;font-weight:bold;cursor:pointer}"
            + "p{color:#bdc8dc}.panel{max-width:600px;margin:20px auto;"
            + "background:#1b2435;padding:16px;border-radius:12px}"
            + "</style></head><body><h1>Knight's Tour</h1>"
            + "<p>Visit all 64 squares exactly once using knight moves.</p>"
            + "<div class='panel'><label>Starting square: </label>"
            + "<select id='start'><option value='0,0'>A8</option>"
            + "<option value='0,7'>H8</option><option value='7,0'>A1</option>"
            + "<option value='7,7'>H1</option><option value='3,3'>D5</option>"
            + "</select><button onclick='generate()'>Find Tour</button>"
            + "<button onclick='play()'>Play</button>"
            + "<button onclick='pause()'>Pause</button>"
            + "<button onclick='step()'>Next Move</button>"
            + "<button onclick='resetBoard()'>Reset</button></div>"
            + "<div id='board'></div><h3 id='status'>Choose a starting square</h3>"
            + "<p id='counter'>Move: 0 / 64</p>"
            + "<script>"
            + "let tour=[],index=0,timer=null;"
            + "const board=document.getElementById('board');"
            + "function draw(){board.innerHTML='';"
            + "for(let r=0;r<8;r++)for(let c=0;c<8;c++){"
            + "let sq=document.createElement('div');"
            + "sq.className='sq '+((r+c)%2?'dark':'light');"
            + "let move=tour.findIndex(p=>p[0]===r&&p[1]===c);"
            + "if(move>=0&&move<index)sq.className='sq visited';"
            + "if(move===index-1)sq.className='sq current';"
            + "sq.textContent=(r===Math.floor((tour[0]||[0,0])[0])&&"
            + "c===Math.floor((tour[0]||[0,0])[1])?'♞':'');"
            + "if(move>=0&&move<index){let n=document.createElement('span');"
            + "n.className='num';n.textContent=move+1;sq.appendChild(n);}"
            + "board.appendChild(sq);}"
            + "document.getElementById('counter').textContent="
            + "'Move: '+index+' / '+tour.length;}"
            + "async function generate(){pause();"
            + "document.getElementById('status').textContent='Finding tour...';"
            + "let s=document.getElementById('start').value.split(',');"
            + "try{let response=await fetch('/tour',{method:'POST',"
            + "headers:{'Content-Type':'application/x-www-form-urlencoded'},"
            + "body:'r='+s[0]+'&c='+s[1]});"
            + "let data=await response.json();tour=data.tour||[];index=0;"
            + "draw();document.getElementById('status').textContent="
            + "tour.length?'Tour found! Press Play.':'No tour found.';"
            + "}catch(e){document.getElementById('status').textContent="
            + "'Could not generate tour.';}}"
            + "function step(){if(index<tour.length){index++;draw();}"
            + "if(index===tour.length)pause();}"
            + "function play(){if(!tour.length)return;pause();"
            + "timer=setInterval(step,350);}"
            + "function pause(){if(timer!==null){clearInterval(timer);timer=null;}}"
            + "function resetBoard(){pause();index=0;draw();}"
            + "draw();generate();"
            + "</script></body></html>";
    }

    static void send(HttpExchange ex, String content, String type)
            throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress(8080), 0);

        server.createContext("/", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                send(ex, "Use GET", "text/plain; charset=UTF-8");
                return;
            }
            send(ex, page(""), "text/html; charset=UTF-8");
        });

        server.createContext("/tour", ex -> {
            try {
                String body = readBody(ex);
                int r = 0, c = 0;

                for (String part : body.split("&")) {
                    String[] pair = part.split("=", 2);
                    if (pair.length != 2) continue;
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = URLDecoder.decode(pair[1], "UTF-8");

                    if (key.equals("r")) r = Integer.parseInt(value);
                    if (key.equals("c")) c = Integer.parseInt(value);
                }

                if (r < 0 || r >= 8 || c < 0 || c >= 8) {
                    send(ex, "{\"tour\":[]}", "application/json");
                    return;
                }

                int[][] tour = findTour(r, c);
                StringBuilder json = new StringBuilder("{\"tour\":[");

                if (tour != null) {
                    for (int i = 0; i < tour.length; i++) {
                        if (i > 0) json.append(",");
                        json.append("[").append(tour[i][0]).append(",")
                            .append(tour[i][1]).append("]");
                    }
                }

                json.append("]}");
                send(ex, json.toString(), "application/json; charset=UTF-8");

            } catch (Exception e) {
                send(ex, "{\"tour\":[]}", "application/json; charset=UTF-8");
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Knight's Tour running at http://localhost:8080");
    }
}
