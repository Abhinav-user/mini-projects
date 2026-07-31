def is_valid_group(group):
    nums = [n for n in group if n != 0]
    return len(nums) == len(set(nums))


def check_sudoku(board):
    valid = True

    # Check rows
    for i in range(9):
        if not is_valid_group(board[i]):
            print(f"❌ Duplicate found in Row {i + 1}")
            valid = False

    # Check columns
    for j in range(9):
        column = [board[i][j] for i in range(9)]
        if not is_valid_group(column):
            print(f"❌ Duplicate found in Column {j + 1}")
            valid = False

    # Check 3x3 boxes
    box_number = 1
    for row in range(0, 9, 3):
        for col in range(0, 9, 3):
            box = []

            for i in range(row, row + 3):
                for j in range(col, col + 3):
                    box.append(board[i][j])

            if not is_valid_group(box):
                print(f"❌ Duplicate found in 3x3 Box {box_number}")
                valid = False

            box_number += 1

    if valid:
        print("\n✅ Sudoku grid is VALID!")
    else:
        print("\n❌ Sudoku grid is INVALID!")


print("Enter the Sudoku grid (9 rows, 9 numbers each).")
print("Use spaces between numbers. Use 0 for empty cells.\n")

board = []

for i in range(9):
    while True:
        row = list(map(int, input(f"Row {i + 1}: ").split()))

        if len(row) == 9 and all(0 <= x <= 9 for x in row):
            board.append(row)
            break
        else:
            print("Please enter exactly 9 numbers between 0 and 9.")

print("\nChecking Sudoku...\n")

check_sudoku(board)