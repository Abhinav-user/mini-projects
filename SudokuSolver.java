import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
 * Sudoku Solver & Generator
 *
 * Java 8 compatible
 *
 * Save as:
 * SudokuSolver.java
 *
 * Compile:
 * javac SudokuSolver.java
 *
 * Run:
 * java SudokuSolver
 *
 * Open:
 * http://localhost:8080
 */

public class SudokuSolver {

    static final int SIZE = 9;
    static final Random random = new Random();

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080),
                0
        );

        server.createContext("/", SudokuSolver::home);
        server.createContext("/solve", SudokuSolver::solveRequest);
        server.createContext("/new", SudokuSolver::newPuzzle);

        server.setExecutor(null);
        server.start();

        System.out.println("=================================");
        System.out.println("      SUDOKU SOLVER & GENERATOR");
        System.out.println("=================================");
        System.out.println("Server running at:");
        System.out.println("http://localhost:8080");
        System.out.println();
        System.out.println("Press Ctrl+C to stop.");
    }

    // =========================================================
    // HOME PAGE
    // =========================================================

    static void home(HttpExchange exchange) throws IOException {

        int[][] puzzle = generatePuzzle();

        String html = createSudokuPage(puzzle);

        sendHTML(exchange, html);
    }

    // =========================================================
    // NEW PUZZLE
    // =========================================================

    static void newPuzzle(HttpExchange exchange) throws IOException {

        int[][] puzzle = generatePuzzle();

        String html = createSudokuPage(puzzle);

        sendHTML(exchange, html);
    }

    // =========================================================
    // SOLVE REQUEST
    // =========================================================

    static void solveRequest(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {

            sendError(
                    exchange,
                    "Only POST requests are allowed."
            );

            return;
        }

        // Java 8 compatible way of reading InputStream
        String body = readRequestBody(exchange.getRequestBody());

        int[][] board = new int[SIZE][SIZE];

        try {

            Map<String, String> values = parseForm(body);

            for (int row = 0; row < SIZE; row++) {

                for (int col = 0; col < SIZE; col++) {

                    String key = "cell_" + row + "_" + col;

                    String value = values.get(key);

                    if (value == null || value.trim().isEmpty()) {

                        board[row][col] = 0;

                    } else {

                        int number = Integer.parseInt(
                                value.trim()
                        );

                        if (number < 1 || number > 9) {

                            sendError(
                                    exchange,
                                    "Each Sudoku cell must contain a number from 1 to 9."
                            );

                            return;
                        }

                        board[row][col] = number;
                    }
                }
            }

        } catch (NumberFormatException e) {

            sendError(
                    exchange,
                    "Please enter only numbers from 1 to 9."
            );

            return;
        }

        // Check whether the entered board is valid.
        if (!isValidBoard(board)) {

            String html = createResultPage(
                    board,
                    "Invalid Sudoku",
                    "The Sudoku contains duplicate numbers in a row, column, or 3x3 box."
            );

            sendHTML(exchange, html);

            return;
        }

        // Make a copy before solving.
        int[][] solved = copyBoard(board);

        // Try solving the Sudoku.
        if (!solveSudoku(solved)) {

            String html = createResultPage(
                    board,
                    "No Solution",
                    "This Sudoku puzzle does not have a valid solution."
            );

            sendHTML(exchange, html);

            return;
        }

        String html = createResultPage(
                solved,
                "Sudoku Solved!",
                "The puzzle was successfully solved using backtracking."
        );

        sendHTML(exchange, html);
    }

    // =========================================================
    // SUDOKU SOLVER - BACKTRACKING
    // =========================================================

    static boolean solveSudoku(int[][] board) {

        int[] empty = findEmptyCell(board);

        // No empty cells = solved.
        if (empty == null) {
            return true;
        }

        int row = empty[0];
        int col = empty[1];

        List<Integer> numbers = new ArrayList<Integer>();

        for (int number = 1; number <= 9; number++) {

            numbers.add(number);
        }

        // Randomize numbers so generated puzzles differ.
        Collections.shuffle(numbers, random);

        for (int number : numbers) {

            if (isSafe(board, row, col, number)) {

                board[row][col] = number;

                if (solveSudoku(board)) {
                    return true;
                }

                // Backtrack.
                board[row][col] = 0;
            }
        }

        return false;
    }

    // =========================================================
    // FIND EMPTY CELL
    // =========================================================

    static int[] findEmptyCell(int[][] board) {

        for (int row = 0; row < SIZE; row++) {

            for (int col = 0; col < SIZE; col++) {

                if (board[row][col] == 0) {

                    return new int[] {
                            row,
                            col
                    };
                }
            }
        }

        return null;
    }

    // =========================================================
    // CHECK IF NUMBER IS SAFE
    // =========================================================

    static boolean isSafe(
            int[][] board,
            int row,
            int col,
            int number
    ) {

        // Check row.
        for (int c = 0; c < SIZE; c++) {

            if (board[row][c] == number) {
                return false;
            }
        }

        // Check column.
        for (int r = 0; r < SIZE; r++) {

            if (board[r][col] == number) {
                return false;
            }
        }

        // Find 3x3 box.
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;

        for (int r = boxRow; r < boxRow + 3; r++) {

            for (int c = boxCol; c < boxCol + 3; c++) {

                if (board[r][c] == number) {
                    return false;
                }
            }
        }

        return true;
    }

    // =========================================================
    // VALIDATE ENTIRE BOARD
    // =========================================================

    static boolean isValidBoard(int[][] board) {

        // Check rows.
        for (int row = 0; row < SIZE; row++) {

            boolean[] seen = new boolean[10];

            for (int col = 0; col < SIZE; col++) {

                int number = board[row][col];

                if (number == 0) {
                    continue;
                }

                if (seen[number]) {
                    return false;
                }

                seen[number] = true;
            }
        }

        // Check columns.
        for (int col = 0; col < SIZE; col++) {

            boolean[] seen = new boolean[10];

            for (int row = 0; row < SIZE; row++) {

                int number = board[row][col];

                if (number == 0) {
                    continue;
                }

                if (seen[number]) {
                    return false;
                }

                seen[number] = true;
            }
        }

        // Check 3x3 boxes.
        for (int boxRow = 0; boxRow < SIZE; boxRow += 3) {

            for (int boxCol = 0; boxCol < SIZE; boxCol += 3) {

                boolean[] seen = new boolean[10];

                for (
                        int row = boxRow;
                        row < boxRow + 3;
                        row++
                ) {

                    for (
                            int col = boxCol;
                            col < boxCol + 3;
                            col++
                    ) {

                        int number = board[row][col];

                        if (number == 0) {
                            continue;
                        }

                        if (seen[number]) {
                            return false;
                        }

                        seen[number] = true;
                    }
                }
            }
        }

        return true;
    }

    // =========================================================
    // GENERATE SUDOKU PUZZLE
    // =========================================================

    static int[][] generatePuzzle() {

        int[][] solved = new int[SIZE][SIZE];

        // Generate a complete valid Sudoku.
        solveSudoku(solved);

        // Copy it.
        int[][] puzzle = copyBoard(solved);

        // Create random cell positions.
        List<Integer> positions = new ArrayList<Integer>();

        for (int i = 0; i < 81; i++) {

            positions.add(i);
        }

        Collections.shuffle(positions, random);

        /*
         * Remove cells while keeping a unique solution.
         */
        for (int position : positions) {

            int row = position / 9;
            int col = position % 9;

            int backup = puzzle[row][col];

            puzzle[row][col] = 0;

            int[][] test = copyBoard(puzzle);

            if (!hasUniqueSolution(test)) {

                puzzle[row][col] = backup;
            }
        }

        return puzzle;
    }

    // =========================================================
    // CHECK UNIQUE SOLUTION
    // =========================================================

    static boolean hasUniqueSolution(int[][] board) {

        int[][] copy = copyBoard(board);

        int[] count = new int[] {
                0
        };

        countSolutions(copy, count);

        return count[0] == 1;
    }

    static void countSolutions(
            int[][] board,
            int[] count
    ) {

        // We only need to know if there is more than one.
        if (count[0] > 1) {
            return;
        }

        int[] empty = findEmptyCell(board);

        // Found a complete solution.
        if (empty == null) {

            count[0]++;

            return;
        }

        int row = empty[0];
        int col = empty[1];

        for (int number = 1; number <= 9; number++) {

            if (isSafe(board, row, col, number)) {

                board[row][col] = number;

                countSolutions(board, count);

                board[row][col] = 0;

                if (count[0] > 1) {
                    return;
                }
            }
        }
    }

    // =========================================================
    // COPY BOARD
    // =========================================================

    static int[][] copyBoard(int[][] original) {

        int[][] copy = new int[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {

            copy[row] = Arrays.copyOf(
                    original[row],
                    SIZE
            );
        }

        return copy;
    }

    // =========================================================
    // CREATE SUDOKU HTML PAGE
    // =========================================================

    static String createSudokuPage(int[][] puzzle) {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");

        html.append("<meta charset=\"UTF-8\">");

        html.append("<title>Sudoku Solver</title>");

        html.append("<style>");

        html.append("body {");
        html.append("font-family: Arial, sans-serif;");
        html.append("background: #f2f2f2;");
        html.append("text-align: center;");
        html.append("margin: 0;");
        html.append("padding: 30px;");
        html.append("}");

        html.append("h1 {");
        html.append("margin-bottom: 5px;");
        html.append("}");

        html.append(".subtitle {");
        html.append("color: #666;");
        html.append("margin-bottom: 25px;");
        html.append("}");

        html.append(".sudoku {");
        html.append("border-collapse: collapse;");
        html.append("margin: auto;");
        html.append("background: white;");
        html.append("box-shadow: 0 5px 20px rgba(0,0,0,0.15);");
        html.append("}");

        html.append(".sudoku td {");
        html.append("width: 55px;");
        html.append("height: 55px;");
        html.append("padding: 0;");
        html.append("border: 1px solid #999;");
        html.append("}");

        html.append(".sudoku input {");
        html.append("width: 100%;");
        html.append("height: 100%;");
        html.append("box-sizing: border-box;");
        html.append("border: none;");
        html.append("text-align: center;");
        html.append("font-size: 24px;");
        html.append("outline: none;");
        html.append("}");

        html.append(".sudoku input:focus {");
        html.append("background: #e8f0ff;");
        html.append("}");

        html.append(".fixed {");
        html.append("background: #eeeeee;");
        html.append("font-weight: bold;");
        html.append("font-size: 24px;");
        html.append("height: 55px;");
        html.append("line-height: 55px;");
        html.append("}");

        html.append(".right-border {");
        html.append("border-right: 3px solid black !important;");
        html.append("}");

        html.append(".bottom-border {");
        html.append("border-bottom: 3px solid black !important;");
        html.append("}");

        html.append("button {");
        html.append("margin: 20px 8px;");
        html.append("padding: 12px 25px;");
        html.append("font-size: 16px;");
        html.append("border: none;");
        html.append("border-radius: 6px;");
        html.append("cursor: pointer;");
        html.append("}");

        html.append(".solve {");
        html.append("background: #333;");
        html.append("color: white;");
        html.append("}");

        html.append(".new {");
        html.append("background: #ddd;");
        html.append("color: black;");
        html.append("}");

        html.append("button:hover {");
        html.append("opacity: 0.8;");
        html.append("}");

        html.append(".instructions {");
        html.append("margin-top: 20px;");
        html.append("color: #555;");
        html.append("}");

        html.append("</style>");

        html.append("</head>");

        html.append("<body>");

        html.append("<h1>Sudoku Solver</h1>");

        html.append(
                "<div class=\"subtitle\">" +
                "Enter numbers into the empty cells and press Solve." +
                "</div>"
        );

        html.append(
                "<form method=\"POST\" action=\"/solve\">"
        );

        html.append("<table class=\"sudoku\">");

        for (int row = 0; row < SIZE; row++) {

            html.append("<tr>");

            for (int col = 0; col < SIZE; col++) {

                String classes = "";

                if (col == 2 || col == 5) {
                    classes += " right-border";
                }

                if (row == 2 || row == 5) {
                    classes += " bottom-border";
                }

                html.append("<td class=\"");
                html.append(classes);
                html.append("\">");

                int value = puzzle[row][col];

                if (value != 0) {

                    html.append(
                            "<div class=\"fixed\">"
                    );

                    html.append(value);

                    html.append("</div>");

                } else {

                    html.append(
                            "<input " +
                            "type=\"text\" " +
                            "name=\"cell_" +
                            row +
                            "_" +
                            col +
                            "\" " +
                            "maxlength=\"1\" " +
                            "inputmode=\"numeric\">"
                    );
                }

                html.append("</td>");
            }

            html.append("</tr>");
        }

        html.append("</table>");

        html.append(
                "<button class=\"solve\" type=\"submit\">" +
                "Solve Sudoku" +
                "</button>"
        );

        html.append("</form>");

        html.append("<a href=\"/new\">");

        html.append(
                "<button class=\"new\" type=\"button\">" +
                "New Puzzle" +
                "</button>"
        );

        html.append("</a>");

        html.append(
                "<div class=\"instructions\">" +
                "Sudoku rules: every row, column and 3x3 box " +
                "must contain numbers 1-9 exactly once." +
                "</div>"
        );

        html.append("</body>");

        html.append("</html>");

        return html.toString();
    }

    // =========================================================
    // CREATE RESULT PAGE
    // =========================================================

    static String createResultPage(
            int[][] board,
            String title,
            String message
    ) {

        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");

        html.append("<meta charset=\"UTF-8\">");

        html.append("<title>Sudoku Result</title>");

        html.append("<style>");

        html.append("body {");
        html.append("font-family: Arial, sans-serif;");
        html.append("background: #f2f2f2;");
        html.append("text-align: center;");
        html.append("padding: 30px;");
        html.append("}");

        html.append("h1 {");
        html.append("margin-bottom: 5px;");
        html.append("}");

        html.append(".message {");
        html.append("color: #555;");
        html.append("margin-bottom: 25px;");
        html.append("}");

        html.append("table {");
        html.append("border-collapse: collapse;");
        html.append("margin: auto;");
        html.append("background: white;");
        html.append("box-shadow: 0 5px 20px rgba(0,0,0,0.15);");
        html.append("}");

        html.append("td {");
        html.append("width: 55px;");
        html.append("height: 55px;");
        html.append("border: 1px solid #999;");
        html.append("font-size: 24px;");
        html.append("font-weight: bold;");
        html.append("}");

        html.append(".right-border {");
        html.append("border-right: 3px solid black;");
        html.append("}");

        html.append(".bottom-border {");
        html.append("border-bottom: 3px solid black;");
        html.append("}");

        html.append("button {");
        html.append("margin-top: 25px;");
        html.append("padding: 12px 25px;");
        html.append("font-size: 16px;");
        html.append("border: none;");
        html.append("border-radius: 6px;");
        html.append("cursor: pointer;");
        html.append("background: #333;");
        html.append("color: white;");
        html.append("}");

        html.append("</style>");

        html.append("</head>");

        html.append("<body>");

        html.append("<h1>");

        html.append(
                escapeHTML(title)
        );

        html.append("</h1>");

        html.append("<div class=\"message\">");

        html.append(
                escapeHTML(message)
        );

        html.append("</div>");

        html.append("<table>");

        for (int row = 0; row < SIZE; row++) {

            html.append("<tr>");

            for (int col = 0; col < SIZE; col++) {

                String classes = "";

                if (col == 2 || col == 5) {
                    classes += "right-border ";
                }

                if (row == 2 || row == 5) {
                    classes += "bottom-border ";
                }

                html.append("<td class=\"");
                html.append(classes);
                html.append("\">");

                html.append(board[row][col]);

                html.append("</td>");
            }

            html.append("</tr>");
        }

        html.append("</table>");

        html.append("<br>");

        html.append("<a href=\"/\">");

        html.append(
                "<button>New Sudoku</button>"
        );

        html.append("</a>");

        html.append("</body>");

        html.append("</html>");

        return html.toString();
    }

    // =========================================================
    // READ REQUEST BODY - JAVA 8 COMPATIBLE
    // =========================================================

    static String readRequestBody(InputStream input)
            throws IOException {

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];

        int bytesRead;

        while ((bytesRead = input.read(buffer)) != -1) {

            output.write(
                    buffer,
                    0,
                    bytesRead
            );
        }

        return new String(
                output.toByteArray(),
                StandardCharsets.UTF_8
        );
    }

    // =========================================================
    // FORM PARSER
    // =========================================================

    static Map<String, String> parseForm(String body)
            throws UnsupportedEncodingException {

        Map<String, String> result =
                new HashMap<String, String>();

        if (body == null || body.isEmpty()) {
            return result;
        }

        String[] pairs = body.split("&");

        for (String pair : pairs) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0],
                    "UTF-8"
            );

            String value = "";

            if (parts.length > 1) {

                value = URLDecoder.decode(
                        parts[1],
                        "UTF-8"
                );
            }

            result.put(key, value);
        }

        return result;
    }

    // =========================================================
    // HTML ESCAPING
    // =========================================================

    static String escapeHTML(String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // =========================================================
    // SEND HTML
    // =========================================================

    static void sendHTML(
            HttpExchange exchange,
            String html
    ) throws IOException {

        byte[] data = html.getBytes(
                StandardCharsets.UTF_8
        );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                200,
                data.length
        );

        OutputStream output =
                exchange.getResponseBody();

        try {

            output.write(data);

        } finally {

            output.close();
        }
    }

    // =========================================================
    // SEND ERROR
    // =========================================================

    static void sendError(
            HttpExchange exchange,
            String message
    ) throws IOException {

        String html =
                "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<title>Sudoku Error</title>" +

                "<style>" +

                "body {" +
                "font-family: Arial;" +
                "text-align: center;" +
                "padding: 60px;" +
                "background: #f2f2f2;" +
                "}" +

                ".error {" +
                "background: white;" +
                "display: inline-block;" +
                "padding: 30px;" +
                "border-radius: 10px;" +
                "box-shadow: 0 5px 20px rgba(0,0,0,0.15);" +
                "}" +

                "button {" +
                "padding: 10px 20px;" +
                "margin-top: 20px;" +
                "cursor: pointer;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class=\"error\">" +

                "<h2>Sudoku Error</h2>" +

                "<p>" +
                escapeHTML(message) +
                "</p>" +

                "<a href=\"/\">" +
                "<button>Back to Sudoku</button>" +
                "</a>" +

                "</div>" +

                "</body>" +

                "</html>";

        sendHTML(exchange, html);
    }
}