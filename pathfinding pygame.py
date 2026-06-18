import pygame
import time
import random
from queue import Queue, PriorityQueue

pygame.init()

WIDTH = 800
ROWS = 40

WIN = pygame.display.set_mode((WIDTH, WIDTH))
pygame.display.set_caption("Pathfinding Visualizer")

WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
RED = (255, 80, 80)
GREEN = (80, 255, 120)
BLUE = (0, 0, 255)
YELLOW = (255, 255, 0)
PURPLE = (128, 0, 128)
ORANGE = (255, 165, 0)
GREY = (128, 128, 128)
TURQUOISE = (64, 224, 208)

FONT = pygame.font.SysFont("arial", 20)

SPEED = 0.01


class Spot:
    def __init__(self, row, col, width, total_rows):
        self.row = row
        self.col = col
        self.x = row * width
        self.y = col * width
        self.color = WHITE
        self.neighbors = []
        self.width = width
        self.total_rows = total_rows

    def get_pos(self):
        return self.row, self.col

    def is_barrier(self):
        return self.color == BLACK

    def reset(self):
        self.color = WHITE

    def make_start(self):
        self.color = ORANGE

    def make_closed(self):
        self.color = RED

    def make_open(self):
        self.color = GREEN

    def make_barrier(self):
        self.color = BLACK

    def make_end(self):
        self.color = TURQUOISE

    def make_path(self):
        self.color = PURPLE

    def draw(self, win):
        pygame.draw.rect(win, self.color, (self.x, self.y, self.width, self.width))

    def update_neighbors(self, grid):
        self.neighbors = []

        dirs = [
            (1, 0), (-1, 0), (0, 1), (0, -1),
            (1, 1), (-1, -1), (1, -1), (-1, 1)
        ]

        for dx, dy in dirs:
            r = self.row + dx
            c = self.col + dy
            if 0 <= r < self.total_rows and 0 <= c < self.total_rows:
                if not grid[r][c].is_barrier():
                    self.neighbors.append(grid[r][c])

    def __lt__(self, other):
        return False


def heuristic(a, b):
    x1, y1 = a
    x2, y2 = b
    return abs(x1 - x2) + abs(y1 - y2)


def reconstruct(came_from, current, draw):
    while current in came_from:
        current = came_from[current]
        current.make_path()
        draw()


def bfs(draw, start, end):
    q = Queue()
    q.put(start)
    came_from = {}
    visited = {start}

    while not q.empty():
        pygame.event.pump()
        time.sleep(SPEED)

        current = q.get()

        if current == end:
            reconstruct(came_from, end, draw)
            end.make_end()
            return True

        for neighbor in current.neighbors:
            if neighbor not in visited:
                visited.add(neighbor)
                came_from[neighbor] = current
                q.put(neighbor)
                neighbor.make_open()

        draw()
        if current != start:
            current.make_closed()

    return False


def dfs(draw, start, end):
    stack = [start]
    came_from = {}
    visited = {start}

    while stack:
        pygame.event.pump()
        time.sleep(SPEED)

        current = stack.pop()

        if current == end:
            reconstruct(came_from, end, draw)
            return True

        for neighbor in current.neighbors:
            if neighbor not in visited:
                visited.add(neighbor)
                came_from[neighbor] = current
                stack.append(neighbor)

        draw()
        if current != start:
            current.make_closed()

    return False


def dijkstra(draw, start, end):
    count = 0
    pq = PriorityQueue()
    pq.put((0, count, start))
    came_from = {}
    dist = {start: 0}

    while not pq.empty():
        pygame.event.pump()
        time.sleep(SPEED)

        current = pq.get()[2]

        if current == end:
            reconstruct(came_from, end, draw)
            return True

        for neighbor in current.neighbors:
            temp = dist[current] + 1

            if neighbor not in dist or temp < dist[neighbor]:
                dist[neighbor] = temp
                count += 1
                pq.put((temp, count, neighbor))
                came_from[neighbor] = current

        draw()
        if current != start:
            current.make_closed()

    return False


def astar(draw, start, end):
    count = 0
    open_set = PriorityQueue()
    open_set.put((0, count, start))

    came_from = {}
    g_score = {start: 0}

    while not open_set.empty():
        pygame.event.pump()
        time.sleep(SPEED)

        current = open_set.get()[2]

        if current == end:
            reconstruct(came_from, end, draw)
            return True

        for neighbor in current.neighbors:
            temp_g = g_score[current] + 1

            if neighbor not in g_score or temp_g < g_score[neighbor]:
                g_score[neighbor] = temp_g
                f = temp_g + heuristic(neighbor.get_pos(), end.get_pos())

                count += 1
                open_set.put((f, count, neighbor))
                came_from[neighbor] = current

        draw()
        if current != start:
            current.make_closed()

    return False


def make_grid(rows, width):
    grid = []
    gap = width // rows

    for i in range(rows):
        grid.append([])
        for j in range(rows):
            grid[i].append(Spot(i, j, gap, rows))

    return grid


def draw_text(win, text):
    render = FONT.render(text, True, BLACK)
    win.blit(render, (10, 10))


def draw_grid(win, rows, width):
    gap = width // rows

    for i in range(rows):
        pygame.draw.line(win, GREY, (0, i * gap), (width, i * gap))

    for j in range(rows):
        pygame.draw.line(win, GREY, (j * gap, 0), (j * gap, width))


def draw(win, grid, rows, width):
    win.fill(WHITE)

    for row in grid:
        for spot in row:
            spot.draw(win)

    draw_grid(win, rows, width)
    draw_text(win, "Pathfinding Visualizer (1 BFS, 2 DFS, 3 Dijkstra, 4 A*, M Maze)")
    pygame.display.update()


def get_clicked(pos, rows, width):
    gap = width // rows
    y, x = pos
    return y // gap, x // gap


def generate_maze(grid):
    for row in grid:
        for spot in row:
            if random.random() < 0.3:
                spot.make_barrier()


def main():
    global SPEED

    grid = make_grid(ROWS, WIDTH)
    start = None
    end = None
    history = []

    run = True

    while run:
        draw(WIN, grid, ROWS, WIDTH)

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                run = False

            if pygame.mouse.get_pressed()[0]:
                pos = pygame.mouse.get_pos()
                row, col = get_clicked(pos, ROWS, WIDTH)
                spot = grid[row][col]

                if not start and spot != end:
                    start = spot
                    start.make_start()
                elif not end and spot != start:
                    end = spot
                    end.make_end()
                elif spot != start and spot != end:
                    spot.make_barrier()

                history.append(spot)

            if event.type == pygame.KEYDOWN:

                if event.key == pygame.K_UP:
                    SPEED = max(0.001, SPEED - 0.005)

                if event.key == pygame.K_DOWN:
                    SPEED += 0.005

                if event.key == pygame.K_u and history:
                    last = history.pop()
                    last.reset()
                    if last == start:
                        start = None
                    if last == end:
                        end = None

                if event.key == pygame.K_c:
                    start = None
                    end = None
                    for row in grid:
                        for spot in row:
                            spot.reset()

                if event.key == pygame.K_r:
                    start = None
                    end = None
                    grid = make_grid(ROWS, WIDTH)

                if event.key == pygame.K_m:
                    generate_maze(grid)

                if start and end:
                    for row in grid:
                        for spot in row:
                            spot.update_neighbors(grid)

                    if event.key == pygame.K_1:
                        bfs(lambda: draw(WIN, grid, ROWS, WIDTH), start, end)

                    if event.key == pygame.K_2:
                        dfs(lambda: draw(WIN, grid, ROWS, WIDTH), start, end)

                    if event.key == pygame.K_3:
                        dijkstra(lambda: draw(WIN, grid, ROWS, WIDTH), start, end)

                    if event.key == pygame.K_4:
                        astar(lambda: draw(WIN, grid, ROWS, WIDTH), start, end)

    pygame.quit()


if __name__ == "__main__":
    main()