import pygame
import random
from collections import deque

pygame.init()

WIDTH = 800
ROWS = 25
CELL = WIDTH // ROWS

WIN = pygame.display.set_mode((WIDTH, WIDTH))
pygame.display.set_caption("Maze Generator & Solver")

WHITE = (255, 255, 255)
BLACK = (0, 0, 0)
GREEN = (0, 255, 0)
BLUE = (50, 150, 255)
RED = (255, 50, 50)
YELLOW = (255, 255, 0)


class Cell:
    def __init__(self, row, col):
        self.row = row
        self.col = col

        self.walls = {
            "top": True,
            "right": True,
            "bottom": True,
            "left": True
        }

        self.visited = False

    def draw(self, win):
        x = self.col * CELL
        y = self.row * CELL

        if self.visited:
            pygame.draw.rect(win, WHITE, (x, y, CELL, CELL))

        if self.walls["top"]:
            pygame.draw.line(win, BLACK, (x, y), (x + CELL, y), 2)

        if self.walls["right"]:
            pygame.draw.line(win, BLACK,
                             (x + CELL, y),
                             (x + CELL, y + CELL), 2)

        if self.walls["bottom"]:
            pygame.draw.line(win, BLACK,
                             (x, y + CELL),
                             (x + CELL, y + CELL), 2)

        if self.walls["left"]:
            pygame.draw.line(win, BLACK,
                             (x, y),
                             (x, y + CELL), 2)


def remove_walls(a, b):
    dx = a.col - b.col
    dy = a.row - b.row

    if dx == 1:
        a.walls["left"] = False
        b.walls["right"] = False

    elif dx == -1:
        a.walls["right"] = False
        b.walls["left"] = False

    if dy == 1:
        a.walls["top"] = False
        b.walls["bottom"] = False

    elif dy == -1:
        a.walls["bottom"] = False
        b.walls["top"] = False


def get_neighbors(grid, cell):
    neighbors = []

    r = cell.row
    c = cell.col

    directions = [
        (r - 1, c),
        (r + 1, c),
        (r, c - 1),
        (r, c + 1)
    ]

    for nr, nc in directions:
        if 0 <= nr < ROWS and 0 <= nc < ROWS:
            neighbor = grid[nr][nc]
            if not neighbor.visited:
                neighbors.append(neighbor)

    return neighbors


def generate_maze(grid):
    stack = []

    current = grid[0][0]
    current.visited = True

    while True:

        neighbors = get_neighbors(grid, current)

        if neighbors:
            next_cell = random.choice(neighbors)

            stack.append(current)

            remove_walls(current, next_cell)

            next_cell.visited = True
            current = next_cell

        elif stack:
            current = stack.pop()

        else:
            break

        draw(grid)
        pygame.display.update()


def get_open_neighbors(grid, cell):
    result = []

    r = cell.row
    c = cell.col

    if not cell.walls["top"] and r > 0:
        result.append(grid[r - 1][c])

    if not cell.walls["bottom"] and r < ROWS - 1:
        result.append(grid[r + 1][c])

    if not cell.walls["left"] and c > 0:
        result.append(grid[r][c - 1])

    if not cell.walls["right"] and c < ROWS - 1:
        result.append(grid[r][c + 1])

    return result


def bfs(grid):
    start = grid[0][0]
    goal = grid[ROWS - 1][ROWS - 1]

    queue = deque([start])

    visited = {start}
    parent = {}

    while queue:

        current = queue.popleft()

        if current == goal:
            break

        for neighbor in get_open_neighbors(grid, current):

            if neighbor not in visited:
                visited.add(neighbor)
                parent[neighbor] = current
                queue.append(neighbor)

    path = []

    cur = goal

    while cur != start:
        path.append(cur)
        cur = parent[cur]

    path.append(start)

    return path[::-1]





def draw(grid):
    WIN.fill(BLACK)

    for row in grid:
        for cell in row:
            cell.draw(WIN)


def main():
    grid = [[Cell(r, c) for c in range(ROWS)] for r in range(ROWS)]

    generate_maze(grid)

    draw(grid)

    pygame.draw.rect(
        WIN,
        GREEN,
        (5, 5, CELL - 10, CELL - 10)
    )

    pygame.draw.rect(
        WIN,
        RED,
        (
            (ROWS - 1) * CELL + 5,
            (ROWS - 1) * CELL + 5,
            CELL - 10,
            CELL - 10
        )
    )

    pygame.display.update()

    path = bfs(grid)

    pygame.time.delay(1000)

    draw_path(path)

    running = True

    while running:

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False

    pygame.quit()


if __name__ == "__main__":
    main()