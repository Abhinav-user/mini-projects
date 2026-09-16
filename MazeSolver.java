import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MazeSolver {

    static final int ROWS = 15;
    static final int COLS = 25;

    static int[][] maze = new int[ROWS][COLS];

    static final int WALL = 0;
    static final int PATH = 1;
    static final int SOLUTION = 2;

    public static void main(String[] args) throws Exception {

        generateMaze();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0);

        server.createContext("/", MazeSolver::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Maze Generator started!");
        System.out.println("Open: http://localhost:8080");
    }

    static void handle(HttpExchange exchange)
            throws IOException {

        if (exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            generateMaze();
        }

        String mazeHTML = drawMaze();

        String html =
                "<!DOCTYPE html>" +
                "<html>" +

                "<head>" +

                "<title>Maze Generator</title>" +

                "<style>" +

                "body{" +
                "font-family:Arial;" +
                "background:#eeeeee;" +
                "padding:30px;" +
                "text-align:center;" +
                "}" +

                ".container{" +
                "max-width:1000px;" +
                "margin:auto;" +
                "background:white;" +
                "padding:25px;" +
                "border-radius:12px;" +
                "box-shadow:0 0 15px #bbb;" +
                "}" +

                ".maze{" +
                "font-family:monospace;" +
                "font-size:18px;" +
                "line-height:18px;" +
                "display:inline-block;" +
                "text-align:left;" +
                "background:#111;" +
                "padding:15px;" +
                "border-radius:8px;" +
                "}" +

                "button{" +
                "padding:13px 25px;" +
                "margin:15px;" +
                "background:#222;" +
                "color:white;" +
                "border:0;" +
                "border-radius:6px;" +
                "font-size:16px;" +
                "cursor:pointer;" +
                "}" +

                ".legend{" +
                "margin-top:15px;" +
                "}" +

                "</style>" +

                "</head>" +

                "<body>" +

                "<div class='container'>" +

                "<h1>🧩 Maze Generator & Solver</h1>" +

                "<p>" +
                "Generate a random maze and find a path " +
                "from START to END." +
                "</p>" +

                "<form method='POST'>" +

                "<button type='submit'>" +
                "Generate & Solve New Maze" +
                "</button>" +

                "</form>" +

                "<div class='maze'>" +

                mazeHTML +

                "</div>" +

                "<div class='legend'>" +
                "⬛ Wall &nbsp;&nbsp; " +
                "⬜ Open &nbsp;&nbsp; " +
                "🟢 Start &nbsp;&nbsp; " +
                "🔴 End &nbsp;&nbsp; " +
                "🟡 Solution" +
                "</div>" +

                "</div>" +

                "</body>" +

                "</html>";

        byte[] response =
                html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
                200,
                response.length);

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }

    static void generateMaze() {

        // Fill everything with walls.
        for (int r = 0; r < ROWS; r++) {

            Arrays.fill(maze[r], WALL);
        }

        // Start generating from the top-left.
        createMaze(1, 1);

        // Make sure start and end are open.
        maze[1][1] = PATH;
        maze[ROWS - 2][COLS - 2] = PATH;

        // Create a guaranteed connection
        // to the bottom-right area.
        maze[ROWS - 2][COLS - 3] = PATH;
        maze[ROWS - 3][COLS - 2] = PATH;

        solveMaze();
    }

    static void createMaze(int row, int col) {

        maze[row][col] = PATH;

        int[][] directions = {
                {-2, 0},
                {2, 0},
                {0, -2},
                {0, 2}
        };

        shuffle(directions);

        for (int[] direction : directions) {

            int newRow =
                    row + direction[0];

            int newCol =
                    col + direction[1];

            if (newRow > 0 &&
                    newRow < ROWS - 1 &&
                    newCol > 0 &&
                    newCol < COLS - 1 &&
                    maze[newRow][newCol] == WALL) {

                // Remove wall between cells.
                maze[
                        row + direction[0] / 2
                ][
                        col + direction[1] / 2
                ] = PATH;

                createMaze(newRow, newCol);
            }
        }
    }

    static void shuffle(int[][] array) {

        Random random = new Random();

        for (int i = array.length - 1;
             i > 0;
             i--) {

            int j = random.nextInt(i + 1);

            int[] temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    static boolean solveMaze() {

        // Remove previous solution markings.
        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                if (maze[r][c] == SOLUTION) {
                    maze[r][c] = PATH;
                }
            }
        }

        boolean[][] visited =
                new boolean[ROWS][COLS];

        boolean solved =
                findPath(
                        1,
                        1,
                        visited);

        if (solved) {

            maze[1][1] = 3;
            maze[ROWS - 2][COLS - 2] = 4;
        }

        return solved;
    }

    static boolean findPath(
            int row,
            int col,
            boolean[][] visited) {

        if (row < 0 ||
                row >= ROWS ||
                col < 0 ||
                col >= COLS) {

            return false;
        }

        if (maze[row][col] == WALL ||
                visited[row][col]) {

            return false;
        }

        visited[row][col] = true;

        // Destination reached.
        if (row == ROWS - 2 &&
                col == COLS - 2) {

            return true;
        }

        int[][] directions = {
                {1, 0},
                {0, 1},
                {-1, 0},
                {0, -1}
        };

        for (int[] direction : directions) {

            int nextRow =
                    row + direction[0];

            int nextCol =
                    col + direction[1];

            if (findPath(
                    nextRow,
                    nextCol,
                    visited)) {

                // Mark solution path.
                if (!(row == 1 && col == 1)) {
                    maze[row][col] = SOLUTION;
                }

                return true;
            }
        }

        return false;
    }

    static String drawMaze() {

        StringBuilder result =
                new StringBuilder();

        for (int r = 0; r < ROWS; r++) {

            for (int c = 0; c < COLS; c++) {

                if (r == 1 && c == 1) {

                    result.append("🟢");

                } else if (
                        r == ROWS - 2 &&
                        c == COLS - 2) {

                    result.append("🔴");

                } else if (maze[r][c] == WALL) {

                    result.append("⬛");

                } else if (
                        maze[r][c] == SOLUTION) {

                    result.append("🟨");

                } else {

                    result.append("⬜");
                }
            }

            result.append("<br>");
        }

        return result.toString();
    }
}