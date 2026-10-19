
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

public class RegexPlayground {

    static String page() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
        "<title>Regex Playground</title><style>" +
        "body{margin:0;padding:25px;background:#10131c;color:#e8eaf0;" +
        "font-family:Arial,sans-serif}" +
        ".panel{max-width:900px;margin:auto;background:#1b2030;" +
        "padding:24px;border-radius:14px}" +
        "textarea,input{box-sizing:border-box;width:100%;padding:12px;" +
        "margin:8px 0 15px;background:#101522;color:#fff;" +
        "border:1px solid #48516a;border-radius:7px;font:15px monospace}" +
        "textarea{min-height:140px;resize:vertical}" +
        "button{padding:11px 18px;background:#8b75ff;color:white;" +
        "border:0;border-radius:7px;cursor:pointer}" +
        "button:hover{background:#7159e8}" +
        ".result{background:#101522;padding:15px;border-radius:8px;" +
        "margin-top:15px;overflow-wrap:anywhere}" +
        ".match{background:#315e49;color:#aaffd0;border-radius:3px}" +
        ".error{color:#ff8585}.hint{color:#a9b2c8;font-size:14px}" +
        "label{font-weight:bold}" +
        "</style></head><body><div class='panel'>" +
        "<h1>Regex Playground</h1>" +
        "<p>Test Java regular expressions against any text.</p>" +
        "<label>Regular expression</label>" +
        "<input id='pattern' value='\\\\b[A-Za-z]+\\\\b' " +
        "placeholder='Enter regex'>" +
        "<label>Flags</label><p>" +
        "<label><input id='icase' type='checkbox' style='width:auto'>" +
        " Ignore case</label> " +
        "<label><input id='multiline' type='checkbox' style='width:auto'>" +
        " Multiline</label> " +
        "<label><input id='dotall' type='checkbox' style='width:auto'>" +
        " Dot matches newline</label></p>" +
        "<label>Test text</label>" +
        "<textarea id='source'>Hello world! Hello Java. " +
        "Contact: demo@example.com</textarea>" +
        "<button onclick='testRegex()'>Test Pattern</button>" +
        "<div id='summary' class='result'>Results will appear here.</div>" +
        "<div id='matches'></div>" +
        "<p class='hint'>Uses Java's Pattern and Matcher classes. " +
        "Match positions are zero-based UTF-16 offsets.</p>" +
        "</div><script>" +

        "async function testRegex(){" +
        "let pattern=document.getElementById('pattern').value;" +
        "let source=document.getElementById('source').value;" +
        "let flags=" +
        "(document.getElementById('icase').checked?'i':'')+" +
        "(document.getElementById('multiline').checked?'m':'')+" +
        "(document.getElementById('dotall').checked?'s':'');" +
        "let data=new URLSearchParams({pattern:pattern,text:source,flags:flags});" +
        "let summary=document.getElementById('summary');" +
        "let list=document.getElementById('matches');" +
        "summary.textContent='Testing...';list.innerHTML='';" +
        "try{" +
        "let response=await fetch('/test',{method:'POST'," +
        "headers:{'Content-Type':'application/x-www-form-urlencoded'}," +
        "body:data.toString()});" +
        "let result=await response.json();" +
        "if(!response.ok)throw new Error(result.error||'Request failed');" +
        "summary.textContent=result.count+' match(es) found.';" +
        "result.matches.forEach(function(m){" +
        "let box=document.createElement('div');box.className='result';" +
        "let title=document.createElement('strong');" +
        "title.textContent='Match '+m.number+': ';" +
        "box.appendChild(title);" +
        "let value=document.createElement('code');" +
        "value.textContent=JSON.stringify(m.value);" +
        "box.appendChild(value);" +
        "let pos=document.createElement('p');" +
        "pos.textContent='Start: '+m.start+' | End: '+m.end;" +
        "box.appendChild(pos);" +
        "m.groups.forEach(function(g,i){" +
        "let p=document.createElement('p');" +
        "p.textContent='Group '+(i+1)+': '+JSON.stringify(g);" +
        "box.appendChild(p);});" +
        "list.appendChild(box);});" +
        "if(result.count===0)list.textContent='No matches found.';" +
        "}catch(e){summary.className='result error';" +
        "summary.textContent='Error: '+e.message;}" +
        "}" +
        "</script></body></html>";
    }

    static void send(HttpExchange ex, int status, String body,
                     String type) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", type);
        ex.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    static String getParam(String body, String key) throws IOException {
        if (body == null) return "";
        for (String part : body.split("&")) {
            String[] pair = part.split("=", 2);
            if (pair.length == 2 && pair[0].equals(key)) {
                return URLDecoder.decode(pair[1], "UTF-8");
            }
        }
        return "";
    }

    static String jsonEscape(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 32) {
                        out.append(String.format("\\u%04x", (int)c));
                    } else {
                        out.append(c);
                    }
            }
        }
        return out.toString();
    }

    static void test(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "{\"error\":\"POST required\"}",
                 "application/json; charset=UTF-8");
            return;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int n, total = 0;

        try (InputStream in = ex.getRequestBody()) {
            while ((n = in.read(chunk)) != -1) {
                total += n;
                if (total > 100000) {
                    send(ex, 413, "{\"error\":\"Input too large (100 KB max)\"}",
                         "application/json; charset=UTF-8");
                    return;
                }
                buffer.write(chunk, 0, n);
            }
        }

        String body = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        String pattern = getParam(body, "pattern");
        String text = getParam(body, "text");
        String flags = getParam(body, "flags");

        int options = 0;
        if (flags.contains("i")) options |= Pattern.CASE_INSENSITIVE;
        if (flags.contains("m")) options |= Pattern.MULTILINE;
        if (flags.contains("s")) options |= Pattern.DOTALL;

        try {
            Pattern compiled = Pattern.compile(pattern, options);
            Matcher matcher = compiled.matcher(text);
            StringBuilder json = new StringBuilder("{\"count\":0,\"matches\":[");
            int count = 0;

            while (matcher.find()) {
                if (count >= 500) break;
                if (count > 0) json.append(",");

                json.append("{\"number\":").append(count + 1)
                    .append(",\"value\":\"").append(jsonEscape(matcher.group()))
                    .append("\",\"start\":").append(matcher.start())
                    .append(",\"end\":").append(matcher.end())
                    .append(",\"groups\":[");

                for (int g = 1; g <= matcher.groupCount(); g++) {
                    if (g > 1) json.append(",");
                    String group = matcher.group(g);
                    if (group == null) {
                        json.append("null");
                    } else {
                        json.append("\"").append(jsonEscape(group)).append("\"");
                    }
                }

                json.append("]}");
                count++;

                // Avoid an infinite loop for zero-length matches.
                if (matcher.start() == matcher.end() &&
                    matcher.end() == text.length()) break;
            }

            json.append("]}");
            String output = json.toString().replace(
                "\"count\":0", "\"count\":" + count
            );
            send(ex, 200, output, "application/json; charset=UTF-8");

        } catch (PatternSyntaxException e) {
            String error = "{\"error\":\"Invalid regex: " +
                jsonEscape(e.getDescription()) + "\"}";
            send(ex, 400, error, "application/json; charset=UTF-8");
        } catch (Exception e) {
            send(ex, 400, "{\"error\":\"Could not process pattern\"}",
                 "application/json; charset=UTF-8");
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8080), 0);

        server.createContext("/", ex -> {
            if (!"/".equals(ex.getRequestURI().getPath())) {
                send(ex, 404, "Not found", "text/plain; charset=UTF-8");
            } else {
                send(ex, 200, page(), "text/html; charset=UTF-8");
            }
        });

        server.createContext("/test", RegexPlayground::test);
        server.setExecutor(null);
        server.start();

        System.out.println("Regex Playground running!");
        System.out.println("Open http://127.0.0.1:8080");
    }
}
