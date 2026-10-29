
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MorseCodeStudio {
    static final Map<Character, String> TO_MORSE = new HashMap<Character, String>();
    static final Map<String, Character> FROM_MORSE = new HashMap<String, Character>();

    static {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String[] codes = {
            ".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
            "....", "..", ".---", "-.-", ".-..", "--", "-.",
            "---", ".--.", "--.-", ".-.", "...", "-", "..-",
            "...-", ".--", "-..-", "-.--", "--..",
            "-----", ".----", "..---", "...--", "....-",
            ".....", "-....", "--...", "---..", "----."
        };

        for (int i = 0; i < letters.length(); i++) {
            TO_MORSE.put(letters.charAt(i), codes[i]);
            FROM_MORSE.put(codes[i], letters.charAt(i));
        }

        TO_MORSE.put('.', ".-.-.-");
        TO_MORSE.put(',', "--..--");
        TO_MORSE.put('?', "..--..");
        TO_MORSE.put('!', "-.-.--");
        TO_MORSE.put('/', "-..-.");
        TO_MORSE.put('-', "-....-");
        TO_MORSE.put('(', "-.--.");
        TO_MORSE.put(')', "-.--.-");
        TO_MORSE.put(':', "---...");
        TO_MORSE.put(';', "-.-.-.");
        TO_MORSE.put('=', "-...-");
        TO_MORSE.put('+', ".-.-.");
        TO_MORSE.put('@', ".--.-.");

        for (Map.Entry<Character, String> entry : TO_MORSE.entrySet()) {
            FROM_MORSE.put(entry.getValue(), entry.getKey());
        }
    }

    static String encode(String text) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = Character.toUpperCase(text.charAt(i));

            if (ch == ' ') {
                result.append("/ ");
            } else if (TO_MORSE.containsKey(ch)) {
                result.append(TO_MORSE.get(ch)).append(' ');
            } else {
                result.append("? ");
            }
        }

        return result.toString().trim();
    }

    static String decode(String morse) {
        StringBuilder result = new StringBuilder();
        String trimmed = morse.trim();

        if (trimmed.isEmpty()) return "";

        String[] words = trimmed.split("\\s*/\\s*");

        for (int w = 0; w < words.length; w++) {
            if (w > 0) result.append(' ');

            String word = words[w].trim();
            if (word.isEmpty()) continue;

            String[] symbols = word.split("\\s+");

            for (String symbol : symbols) {
                Character ch = FROM_MORSE.get(symbol);

                if (ch == null) {
                    result.append('?');
                } else {
                    result.append(ch.charValue());
                }
            }
        }

        return result.toString();
    }

    static String escape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    static String readBody(HttpExchange ex) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InputStream in = ex.getRequestBody();
        byte[] buffer = new byte[4096];
        int count;

        while ((count = in.read(buffer)) != -1) {
            out.write(buffer, 0, count);
        }

        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }

    static Map<String, String> parseForm(String body)
            throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<String, String>();

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], "UTF-8");
            String value = parts.length > 1
                ? URLDecoder.decode(parts[1], "UTF-8") : "";
            map.put(key, value);
        }

        return map;
    }

    static void send(HttpExchange ex, String content, String type)
            throws IOException {
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(200, data.length);

        try (OutputStream out = ex.getResponseBody()) {
            out.write(data);
        }
    }

    static String page(String input, String output, String mode) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>")
            .append("<meta name='viewport' content='width=device-width, initial-scale=1'>")
            .append("<title>Morse Code Studio</title><style>")
            .append("*{box-sizing:border-box}body{margin:0;padding:25px 15px;")
            .append("font-family:Arial;background:#101522;color:#edf2ff}")
            .append(".wrap{max-width:850px;margin:auto}.panel{background:#1b2435;")
            .append("padding:20px;border-radius:14px;margin:18px 0}")
            .append("textarea{width:100%;min-height:120px;background:#101522;")
            .append("color:#fff;border:1px solid #45516b;border-radius:8px;")
            .append("padding:13px;font:16px monospace}button,select,input{padding:11px;")
            .append("border:0;border-radius:7px;margin:6px 4px 6px 0}")
            .append("button{background:#8ba7ff;font-weight:bold;cursor:pointer}")
            .append(".out{font:18px monospace;overflow-wrap:anywhere;")
            .append("background:#101522;padding:15px;border-radius:8px;min-height:45px}")
            .append(".muted{color:#b3bfd5;line-height:1.6}.timeline{display:flex;")
            .append("gap:4px;flex-wrap:wrap;margin-top:14px}.signal{height:25px;")
            .append("border-radius:4px;background:#8ba7ff}.dot{width:8px}")
            .append(".dash{width:25px}.gap{width:8px;background:transparent}")
            .append("a{color:#9db8ff}</style></head><body><div class='wrap'>")
            .append("<h1>Morse Code Studio</h1>")
            .append("<p class='muted'>Translate messages into Morse code, decode signals, ")
            .append("and play the classic dot-and-dash sound.</p>")
            .append("<div class='panel'><form method='POST' action='/convert'>")
            .append("<label>Choose conversion mode</label><br>")
            .append("<select name='mode'>")
            .append("<option value='encode' ")
            .append(mode.equals("encode") ? "selected" : "")
            .append(">Text to Morse</option>")
            .append("<option value='decode' ")
            .append(mode.equals("decode") ? "selected" : "")
            .append(">Morse to text</option></select><br>")
            .append("<textarea name='input' placeholder='Enter text or Morse code...'>")
            .append(escape(input)).append("</textarea><br>")
            .append("<button type='submit'>Convert</button></form></div>")
            .append("<div class='panel'><h2>Result</h2><div id='output' class='out'>")
            .append(escape(output)).append("</div>")
            .append("<button onclick='copyResult()'>Copy result</button>")
            .append("<button onclick='playMorse()'>Play sound</button>")
            .append("<label>Speed: <span id='speedLabel'>15</span> WPM</label>")
            .append("<input id='speed' type='range' min='5' max='30' value='15' ")
            .append("oninput=\"document.getElementById('speedLabel').textContent=this.value\">")
            .append("<div id='timeline' class='timeline'></div>")
            .append("<p id='status' class='muted'></p></div>")
            .append("<div class='panel'><h2>Morse reference</h2>")
            .append("<p class='muted'>A dot is one time unit, a dash is three. ")
            .append("Letters are separated by three units and words by seven.</p>")
            .append("<p class='muted'>A .-  B -...  C -.-.  D -..  E .  F ..-.<br>")
            .append("G --.  H ....  I ..  J .---  K -.-  L .-..<br>")
            .append("M --  N -.  O ---  P .--.  Q --.-  R .-.<br>")
            .append("S ...  T -  U ..-  V ...-  W .--  X -..-<br>")
            .append("Y -.--  Z --..</p></div>")
            .append("<script>")
            .append("const result=document.getElementById('output').textContent;")
            .append("function copyResult(){navigator.clipboard.writeText(result);")
            .append("document.getElementById('status').textContent='Copy requested.';}")
            .append("function buildTimeline(){let box=document.getElementById('timeline');")
            .append("box.innerHTML='';let symbols=result.split('');")
            .append("for(let ch of symbols){let el=document.createElement('div');")
            .append("if(ch==='.'){el.className='signal dot';}")
            .append("else if(ch==='-'){el.className='signal dash';}")
            .append("else{el.className='signal gap';}")
            .append("box.appendChild(el);}}buildTimeline();")
            .append("function playMorse(){let ctx;try{ctx=new (window.AudioContext||")
            .append("window.webkitAudioContext)();}catch(e){alert('Audio is not supported.');return;}")
            .append("let unit=1200/Number(document.getElementById('speed').value);")
            .append("let t=ctx.currentTime;")
            .append("for(let i=0;i<result.length;i++){let ch=result[i];")
            .append("if(ch==='.'||ch==='-'){let osc=ctx.createOscillator();")
            .append("let gain=ctx.createGain();osc.frequency.value=650;")
            .append("gain.gain.value=0.08;osc.connect(gain);gain.connect(ctx.destination);")
            .append("osc.start(t);let duration=(ch==='.'?1:3)*unit/1000;")
            .append("osc.stop(t+duration);t+=duration+unit/1000;}")
            .append("else if(ch==='/'){t+=4*unit/1000;}else{t+=2*unit/1000;}}")
            .append("document.getElementById('status').textContent='Playing Morse audio.';}")
            .append("</script></div></body></html>");

        return html.toString();
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", ex -> {
            if (!ex.getRequestMethod().equalsIgnoreCase("GET")) {
                send(ex, "Use the conversion form.", "text/plain; charset=UTF-8");
                return;
            }

            send(ex, page("", "", "encode"), "text/html; charset=UTF-8");
        });

        server.createContext("/convert", ex -> {
            try {
                if (!ex.getRequestMethod().equalsIgnoreCase("POST")) {
                    send(ex, page("", "Submit the form to convert.", "encode"),
                        "text/html; charset=UTF-8");
                    return;
                }

                Map<String, String> form = parseForm(readBody(ex));
                String input = form.get("input");
                String mode = form.get("mode");

                if (input == null) input = "";
                if (!"decode".equals(mode)) mode = "encode";

                String output = mode.equals("encode")
                    ? encode(input) : decode(input);

                send(ex, page(input, output, mode), "text/html; charset=UTF-8");
            } catch (Exception e) {
                send(ex, page("", "Conversion failed.", "encode"),
                    "text/html; charset=UTF-8");
            }
        });

        server.setExecutor(null);
        server.start();

        System.out.println("Morse Code Studio running at http://localhost:8080");
    }
}
