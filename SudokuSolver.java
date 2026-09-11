import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SudokuSolver {

    public static void main(String[] args) throws Exception {

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080), 0);

        server.createContext("/", SudokuSolver::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Sudoku Solver started!");
        System.out.println("Open: http://localhost:8080");
    }

    static void handle(HttpExchange exchange)
            throws IOException {

        String boardText = "";
        String result = "";

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    exchange.getRequestBody(),
                                    StandardCharsets.UTF_8));

            String data = reader.readLine();

            if (data != null &&
                    data.startsWith("board=")) {

                boardText = URLDecoder.decode(
                        data.substring(6),
                        StandardCharsets.UTF_8);

                int[][] board =
                        parseBoard(boardText);

                if (board == null) {

                    result =
                            "<div class='error'>" +
                            "❌ Enter exactly 81 numbers." +
                            "</div>";

                } else if (!isValid(board)) {

                    result =
                            "<div class='error'>" +
                            "❌ The Sudoku board is invalid." +
                            "</div>";

                } else if (solve(board)) {

                    result =
                            "<div class='success'>" +
                            "<h2>✅ Solved!</h2>" +
                            boardToHTML(board) +
                            "</div>";

                } else {

                    result =
                            "<div class='error'>" +
                            "❌ This Sudoku has no solution." +
                            "</div>";
                }
            }
        }

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>Sudoku Solver</title>" +

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

                "textarea{" +
                "width:96%;" +
                "height:150px;" +
                "padding:10px;" +
                "font-size:18px;" +
                "font-family:monospace;" +
                "}" +

                "button{" +
                "width:100%;" +
                "padding:13px;" +
                "margin-top:15px;" +
                "background:#222;" +
                "color:white;" +
                "border:0;" +
                "border-radius:6px;" +
                "font-size:16px;" +
                "cursor:pointer;" +
                "}" +

                ".success{" +
                "margin-top:25px;" +
                "padding:20px;" +
                "background:#f5f5f5;" +
                "border-radius:8px;" +
                "text-align:center;" +
                "}" +

                ".error{" +
                "margin-top:20px;" +
                "padding:15px;" +
                "background:#f5f5f5;" +
                "border-radius:8px;" +
                "text-align:center;" +
                "}" +

                ".sudoku{" +
                "border-collapse:collapse;" +
                "margin:20px auto;" +
                "}" +

                ".sudoku td{" +
                "width:45px;" +
                "height:45px;" +
                "border:1px solid #777;" +
                "font-size:22px;" +
                "font-weight:bold;" +
                "text-align:center;" +
                "}" +

                ".sudoku tr:nth-child(3n) td{" +
                "border-bottom:3px solid black;" +
                "}" +

                ".sudoku td:nth-child(3n){" +
                "border-right:3px solid black;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='box'>" +

                "<h1>🧩 Sudoku Solver</h1>" +

                "<p>" +
                "Enter 81 numbers. Use 0 for empty cells." +
                "</p>" +

                "<form method='POST'>" +

                "<textarea name='board' " +
                "placeholder='530070000600195000...'>" +
                "</textarea>" +

                "<button type='submit'>" +
                "Solve Sudoku" +
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
                200,
                response.length);

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    static int[][] parseBoard(String text) {

        text = text.replaceAll("[^0-9]", "");

        if (text.length() != 81) {
            return null;
        }

        int[][] board = new int[9][9];

        int index = 0;

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                board[row][col] =
                        text.charAt(index) - '0';

                index++;
            }
        }

        return board;
    }

    static boolean isValid(int[][] board) {

        for (int row = 0; row < 9; row++) {

            boolean[] seen = new boolean[10];

            for (int col = 0; col < 9; col++) {

                int value = board[row][col];

                if (value == 0)
                    continue;

                if (seen[value])
                    return false;

                seen[value] = true;
            }
        }

        for (int col = 0; col < 9; col++) {

            boolean[] seen = new boolean[10];

            for (int row = 0; row < 9; row++) {

                int value = board[row][col];

                if (value == 0)
                    continue;

                if (seen[value])
                    return false;

                seen[value] = true;
            }
        }

        for (int boxRow = 0;
             boxRow < 9;
             boxRow += 3) {

            for (int boxCol = 0;
                 boxCol < 9;
                 boxCol += 3) {

                boolean[] seen =
                        new boolean[10];

                for (int row = boxRow;
                     row < boxRow + 3;
                     row++) {

                    for (int col = boxCol;
                         col < boxCol + 3;
                         col++) {

                        int value =
                                board[row][col];

                        if (value == 0)
                            continue;

                        if (seen[value])
                            return false;

                        seen[value] = true;
                    }
                }
            }
        }

        return true;
    }

    static boolean solve(int[][] board) {

        for (int row = 0; row < 9; row++) {

            for (int col = 0; col < 9; col++) {

                if (board[row][col] == 0) {

                    for (int number = 1;
                         number <= 9;
                         number++) {

                        if (canPlace(
                                board,
                                row,
                                col,
                                number)) {

                            board[row][col] =
                                    number;

                            if (solve(board)) {
                                return true;
                            }

                            board[row][col] = 0;
                        }
                    }

                    return false;
                }
            }
        }

        return true;
    }

    static boolean canPlace(
            int[][] board,
            int row,
            int col,
            int number) {

        for (int i = 0; i < 9; i++) {

            if (board[row][i] == number)
                return false;

            if (board[i][col] == number)
                return false;
        }

        int startRow =
                (row / 3) * 3;

        int startCol =
                (col / 3) * 3;

        for (int r = startRow;
             r < startRow + 3;
             r++) {

            for (int c = startCol;
                 c < startCol + 3;
                 c++) {

                if (board[r][c] == number)
                    return false;
            }
        }

        return true;
    }

    static String boardToHTML(
            int[][] board) {

        StringBuilder html =
                new StringBuilder();

        html.append("<table class='sudoku'>");

        for (int row = 0; row < 9; row++) {

            html.append("<tr>");

            for (int col = 0; col < 9; col++) {

                html.append("<td>")
                        .append(board[row][col])
                        .append("</td>");
            }

            html.append("</tr>");
        }

        html.append("</table>");

        return html.toString();
    }
}