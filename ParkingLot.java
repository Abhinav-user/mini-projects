import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class ParkingLot {

    static final int TOTAL_SLOTS = 12;

    static ParkingSlot[] slots =
            new ParkingSlot[TOTAL_SLOTS];

    static {
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            slots[i] = new ParkingSlot(i + 1);
        }
    }

    static class ParkingSlot {

        int number;
        String vehicleNumber;
        String vehicleType;
        long entryTime;

        ParkingSlot(int number) {
            this.number = number;
        }

        boolean isFree() {
            return vehicleNumber == null;
        }
    }

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", ParkingLot::home);
        server.createContext("/park", ParkingLot::park);
        server.createContext("/remove", ParkingLot::remove);
        server.createContext("/search", ParkingLot::search);

        server.start();

        System.out.println("==============================");
        System.out.println("       PARKING LOT");
        System.out.println("==============================");
        System.out.println("Open: http://localhost:8080");
    }

    // ================= HOME =================

    static void home(HttpExchange exchange)
            throws IOException {

        StringBuilder parking =
                new StringBuilder();

        for (ParkingSlot slot : slots) {

            String status =
                    slot.isFree()
                    ? "FREE"
                    : "OCCUPIED";

            String vehicle =
                    slot.isFree()
                    ? ""
                    : "<br>" + slot.vehicleNumber;

            parking.append(
                    "<div class='slot " +
                    (slot.isFree()
                    ? "free"
                    : "occupied") +
                    "'>" +

                    "<b>Slot " +
                    slot.number +
                    "</b>" +

                    "<br>" +

                    status +

                    vehicle +

                    "</div>"
            );
        }

        int free = 0;

        for (ParkingSlot slot : slots) {
            if (slot.isFree()) {
                free++;
            }
        }

        int occupied =
                TOTAL_SLOTS - free;

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>Parking Lot</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#f1f1f1;" +
                "margin:0;" +
                "padding:30px;" +
                "}" +

                ".container{" +
                "max-width:850px;" +
                "margin:auto;" +
                "}" +

                "h1{" +
                "text-align:center;" +
                "}" +

                ".stats{" +
                "display:flex;" +
                "gap:15px;" +
                "justify-content:center;" +
                "margin:25px 0;" +
                "}" +

                ".stat{" +
                "background:white;" +
                "padding:20px;" +
                "border-radius:8px;" +
                "text-align:center;" +
                "flex:1;" +
                "}" +

                ".number{" +
                "font-size:30px;" +
                "font-weight:bold;" +
                "}" +

                ".parking{" +
                "display:grid;" +
                "grid-template-columns:" +
                "repeat(4,1fr);" +
                "gap:12px;" +
                "}" +

                ".slot{" +
                "padding:20px 5px;" +
                "text-align:center;" +
                "border-radius:8px;" +
                "min-height:55px;" +
                "}" +

                ".free{" +
                "background:#c8f7c5;" +
                "}" +

                ".occupied{" +
                "background:#ffb5b5;" +
                "}" +

                ".forms{" +
                "display:grid;" +
                "grid-template-columns:1fr 1fr;" +
                "gap:20px;" +
                "margin-top:30px;" +
                "}" +

                ".box{" +
                "background:white;" +
                "padding:20px;" +
                "border-radius:10px;" +
                "}" +

                "input,select{" +
                "width:90%;" +
                "padding:10px;" +
                "margin:8px 0;" +
                "font-size:15px;" +
                "}" +

                "button{" +
                "padding:10px 20px;" +
                "background:#222;" +
                "color:white;" +
                "border:none;" +
                "border-radius:5px;" +
                "cursor:pointer;" +
                "}" +

                "@media(max-width:600px){" +
                ".parking{" +
                "grid-template-columns:" +
                "repeat(2,1fr);" +
                "}" +
                ".forms{" +
                "grid-template-columns:1fr;" +
                "}" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🅿️ Parking Lot Manager</h1>" +

                "<div class='stats'>" +

                "<div class='stat'>" +
                "<div class='number'>" +
                TOTAL_SLOTS +
                "</div>Total Slots</div>" +

                "<div class='stat'>" +
                "<div class='number'>" +
                occupied +
                "</div>Occupied</div>" +

                "<div class='stat'>" +
                "<div class='number'>" +
                free +
                "</div>Available</div>" +

                "</div>" +

                "<h2>Parking Slots</h2>" +

                "<div class='parking'>" +
                parking +
                "</div>" +

                "<div class='forms'>" +

                "<div class='box'>" +

                "<h2>Park Vehicle</h2>" +

                "<form method='POST' action='/park'>" +

                "<input name='vehicle' " +
                "placeholder='Vehicle number' " +
                "required>" +

                "<select name='type'>" +

                "<option value='Car'>Car</option>" +
                "<option value='Bike'>Bike</option>" +
                "<option value='Truck'>Truck</option>" +

                "</select>" +

                "<br>" +

                "<button type='submit'>" +
                "Park Vehicle" +
                "</button>" +

                "</form>" +

                "</div>" +

                "<div class='box'>" +

                "<h2>Remove Vehicle</h2>" +

                "<form method='POST' action='/remove'>" +

                "<input name='vehicle' " +
                "placeholder='Vehicle number' " +
                "required>" +

                "<br>" +

                "<button type='submit'>" +
                "Remove Vehicle" +
                "</button>" +

                "</form>" +

                "<h2>Search</h2>" +

                "<form method='POST' action='/search'>" +

                "<input name='vehicle' " +
                "placeholder='Vehicle number' " +
                "required>" +

                "<br>" +

                "<button type='submit'>" +
                "Search Vehicle" +
                "</button>" +

                "</form>" +

                "</div>" +

                "</div>" +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }

    // ================= PARK =================

    static void park(HttpExchange exchange)
            throws IOException {

        String data =
                readRequest(exchange);

        String vehicle =
                getValue(data, "vehicle");

        String type =
                getValue(data, "type");

        if (vehicle == null ||
                vehicle.trim().isEmpty()) {

            result(
                    exchange,
                    "Vehicle number is required."
            );

            return;
        }

        vehicle =
                vehicle.trim().toUpperCase();

        // Check duplicate

        for (ParkingSlot slot : slots) {

            if (!slot.isFree() &&
                    slot.vehicleNumber.equals(vehicle)) {

                result(
                        exchange,
                        "This vehicle is already parked."
                );

                return;
            }
        }

        // Find free slot

        for (ParkingSlot slot : slots) {

            if (slot.isFree()) {

                slot.vehicleNumber = vehicle;
                slot.vehicleType = type;
                slot.entryTime =
                        System.currentTimeMillis();

                result(
                        exchange,
                        "Vehicle parked successfully! " +
                        "Slot number: " +
                        slot.number
                );

                return;
            }
        }

        result(
                exchange,
                "Parking lot is full."
        );
    }

    // ================= REMOVE =================

    static void remove(HttpExchange exchange)
            throws IOException {

        String data =
                readRequest(exchange);

        String vehicle =
                getValue(data, "vehicle");

        if (vehicle == null) {

            result(
                    exchange,
                    "Vehicle number required."
            );

            return;
        }

        vehicle =
                vehicle.trim().toUpperCase();

        for (ParkingSlot slot : slots) {

            if (!slot.isFree() &&
                    slot.vehicleNumber.equals(vehicle)) {

                long duration =
                        System.currentTimeMillis()
                        - slot.entryTime;

                long hours =
                        Math.max(
                                1,
                                (duration + 3599999)
                                / 3600000
                        );

                int fee =
                        calculateFee(
                                slot.vehicleType,
                                hours
                        );

                int slotNumber =
                        slot.number;

                slot.vehicleNumber = null;
                slot.vehicleType = null;
                slot.entryTime = 0;

                result(
                        exchange,
                        "Vehicle removed from Slot " +
                        slotNumber +
                        "<br><br>" +
                        "Parking time: " +
                        hours +
                        " hour(s)" +
                        "<br>" +
                        "Parking fee: ₹" +
                        fee
                );

                return;
            }
        }

        result(
                exchange,
                "Vehicle not found."
        );
    }

    // ================= SEARCH =================

    static void search(HttpExchange exchange)
            throws IOException {

        String data =
                readRequest(exchange);

        String vehicle =
                getValue(data, "vehicle");

        if (vehicle == null) {

            result(
                    exchange,
                    "Vehicle number required."
            );

            return;
        }

        vehicle =
                vehicle.trim().toUpperCase();

        for (ParkingSlot slot : slots) {

            if (!slot.isFree() &&
                    slot.vehicleNumber.equals(vehicle)) {

                long minutes =
                        (System.currentTimeMillis()
                        - slot.entryTime)
                        / 60000;

                result(
                        exchange,
                        "Vehicle Found! 🚗" +
                        "<br><br>" +
                        "Vehicle: " +
                        slot.vehicleNumber +
                        "<br>" +
                        "Type: " +
                        slot.vehicleType +
                        "<br>" +
                        "Slot: " +
                        slot.number +
                        "<br>" +
                        "Parked for: " +
                        minutes +
                        " minute(s)"
                );

                return;
            }
        }

        result(
                exchange,
                "Vehicle not found."
        );
    }

    // ================= FEE =================

    static int calculateFee(
            String type,
            long hours) {

        int rate;

        if ("Bike".equals(type)) {
            rate = 20;
        }
        else if ("Truck".equals(type)) {
            rate = 80;
        }
        else {
            rate = 40;
        }

        return (int) hours * rate;
    }

    // ================= READ REQUEST =================

    static String readRequest(
            HttpExchange exchange)
            throws IOException {

        InputStream input =
                exchange.getRequestBody();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        byte[] buffer =
                new byte[1024];

        int length;

        while ((length =
                input.read(buffer)) != -1) {

            output.write(
                    buffer,
                    0,
                    length
            );
        }

        return new String(
                output.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    // ================= FORM VALUE =================

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

    // ================= RESULT PAGE =================

    static void result(
            HttpExchange exchange,
            String message)
            throws IOException {

        String html =
                "<html>" +

                "<head>" +
                "<title>Parking Result</title>" +
                "</head>" +

                "<body style='" +
                "font-family:Arial;" +
                "text-align:center;" +
                "padding:60px;" +
                "background:#f1f1f1;" +
                "'>" +

                "<div style='" +
                "background:white;" +
                "padding:40px;" +
                "max-width:500px;" +
                "margin:auto;" +
                "border-radius:12px;" +
                "'>" +

                "<h2>" +
                message +
                "</h2>" +

                "<br>" +

                "<a href='/' style='" +
                "background:#222;" +
                "color:white;" +
                "padding:12px 25px;" +
                "text-decoration:none;" +
                "border-radius:5px;" +
                "'>" +

                "Back to Parking Lot" +

                "</a>" +

                "</div>" +

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