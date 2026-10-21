
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class SortingRace {
    static String page() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
        "<meta name='viewport' content='width=device-width,initial-scale=1'>" +
        "<title>Sorting Algorithm Race</title><style>" +
        "body{background:#111827;color:#eee;font-family:Arial;padding:20px}" +
        ".box{max-width:950px;margin:auto;background:#1f2937;padding:20px;" +
        "border-radius:14px}button,select,input{padding:9px;margin:5px;" +
        "border-radius:6px;border:0}button{background:#38bdf8;cursor:pointer}" +
        "#bars{height:320px;display:flex;align-items:flex-end;gap:2px;" +
        "padding-top:20px} .bar{flex:1;min-width:1px;background:#38bdf8;" +
        "border-radius:3px 3px 0 0} .active{background:#fb7185}" +
        ".done{background:#4ade80}.stats{display:flex;gap:20px;flex-wrap:wrap}" +
        "</style></head><body><div class='box'>" +
        "<h1>Sorting Algorithm Race</h1>" +
        "<p>Watch sorting algorithms organize random numbers.</p>" +
        "<label>Algorithm:</label><select id='algo'>" +
        "<option value='bubble'>Bubble Sort</option>" +
        "<option value='selection'>Selection Sort</option>" +
        "<option value='insertion'>Insertion Sort</option>" +
        "<option value='quick'>Quick Sort</option></select>" +
        "<label>Array size:</label><input id='size' type='number' " +
        "min='10' max='120' value='45' style='width:65px'>" +
        "<button onclick='resetArray()'>New Array</button>" +
        "<button onclick='startSort()'>Start</button>" +
        "<button onclick='pauseSort()'>Pause</button>" +
        "<button onclick='resetArray()'>Reset</button>" +
        "<br><label>Speed:</label><input id='speed' type='range' " +
        "min='1' max='100' value='50'>" +
        "<div class='stats'><p id='comparisons'>Comparisons: 0</p>" +
        "<p id='swaps'>Swaps: 0</p><p id='status'>Ready</p></div>" +
        "<div id='bars'></div></div><script>" +

        "let values=[],steps=[],idx=0,timer=null,comparisons=0,swaps=0;" +
        "let running=false;" +
        "function resetArray(){" +
        "pauseSort();let n=Math.max(10,Math.min(120," +
        "parseInt(document.getElementById('size').value)||45));" +
        "values=Array.from({length:n},()=>Math.floor(Math.random()*100)+5);" +
        "steps=[];idx=0;comparisons=0;swaps=0;render(-1,-1);" +
        "stats();document.getElementById('status').textContent='Ready';}" +

        "function render(a,b,finished=false){" +
        "let box=document.getElementById('bars');box.innerHTML='';" +
        "let max=Math.max(...values);" +
        "values.forEach((v,i)=>{" +
        "let bar=document.createElement('div');bar.className='bar';" +
        "bar.style.height=(v/max*100)+'%';" +
        "if(finished)bar.classList.add('done');" +
        "else if(i===a||i===b)bar.classList.add('active');" +
        "bar.title=v;box.appendChild(bar);});}" +

        "function stats(){" +
        "document.getElementById('comparisons').textContent=" +
        "'Comparisons: '+comparisons;" +
        "document.getElementById('swaps').textContent='Swaps: '+swaps;}" +

        "function snapshot(a,b,cmp=1,sw=0){" +
        "steps.push({v:values.slice(),a:a,b:b,cmp:cmp,sw:sw});}" +

        "function makeSteps(a){" +
        "steps=[];let arr=a.slice();" +
        "function record(i,j,cmp,sw){" +
        "steps.push({v:arr.slice(),a:i,b:j,cmp:cmp,sw:sw});}" +
        "let type=document.getElementById('algo').value;" +

        "if(type==='bubble'){" +
        "for(let i=0;i<arr.length-1;i++){" +
        "for(let j=0;j<arr.length-i-1;j++){" +
        "if(arr[j]>arr[j+1]){" +
        "[arr[j],arr[j+1]]=[arr[j+1],arr[j]];record(j,j+1,1,1);" +
        "}else record(j,j+1,1,0);}}}" +

        "else if(type==='selection'){" +
        "for(let i=0;i<arr.length-1;i++){" +
        "let min=i;" +
        "for(let j=i+1;j<arr.length;j++){" +
        "record(min,j,1,0);if(arr[j]<arr[min])min=j;}" +
        "if(min!==i){[arr[i],arr[min]]=[arr[min],arr[i]];" +
        "record(i,min,0,1);}}}" +

        "else if(type==='insertion'){" +
        "for(let i=1;i<arr.length;i++){" +
        "let key=arr[i],j=i-1;" +
        "while(j>=0){record(j,j+1,1,0);" +
        "if(arr[j]<=key)break;arr[j+1]=arr[j];" +
        "record(j,j+1,0,1);j--;}" +
        "arr[j+1]=key;record(j+1,i,0,0);}}" +

        "else{" +
        "function quick(lo,hi){" +
        "if(lo>=hi)return;let pivot=arr[hi],i=lo;" +
        "for(let j=lo;j<hi;j++){" +
        "record(j,hi,1,0);" +
        "if(arr[j]<pivot){" +
        "if(i!==j){[arr[i],arr[j]]=[arr[j],arr[i]];" +
        "record(i,j,0,1);}i++;}}" +
        "if(i!==hi){[arr[i],arr[hi]]=[arr[hi],arr[i]];" +
        "record(i,hi,0,1);}quick(lo,i-1);quick(i+1,hi);}" +
        "quick(0,arr.length-1);}" +

        "values=arr;steps.push({v:arr.slice(),a:-1,b:-1," +
        "cmp:0,sw:0,finished:true});" +
        "values=a.slice();}" +

        "function startSort(){" +
        "pauseSort();makeSteps(values);idx=0;comparisons=0;swaps=0;" +
        "running=true;document.getElementById('status').textContent='Sorting...';" +
        "play();}" +

        "function play(){" +
        "if(!running)return;" +
        "if(idx>=steps.length){" +
        "running=false;document.getElementById('status').textContent='Sorted!';" +
        "render(-1,-1,true);return;}" +
        "let s=steps[idx++];values=s.v;" +
        "comparisons+=s.cmp;swaps+=s.sw;stats();" +
        "render(s.a,s.b,!!s.finished);" +
        "if(s.finished){" +
        "running=false;document.getElementById('status').textContent='Sorted!';" +
        "return;}" +
        "timer=setTimeout(play,Math.max(1,101-" +
        "Number(document.getElementById('speed').value)));}" +

        "function pauseSort(){running=false;clearTimeout(timer);}" +
        "resetArray();" +
        "</script></body></html>";
    }

    static void send(HttpExchange ex, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = ex.getResponseBody()) {
            out.write(bytes);
        }
    }

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8080), 0);
        server.createContext("/", SortingRace::handle);
        server.setExecutor(null);
        server.start();
        System.out.println("Open http://127.0.0.1:8080");
    }

    static void handle(HttpExchange ex) throws IOException {
        send(ex, page());
    }
}
