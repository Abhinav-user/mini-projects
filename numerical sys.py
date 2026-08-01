history = []


def validate(number, base):
    digits = {
        2: "01",
        8: "01234567",
        10: "0123456789",
        16: "0123456789ABCDEFabcdef"
    }

    return all(ch in digits[base] for ch in number)


def convert(number, from_base, to_base):
    decimal = int(number, from_base)

    if to_base == 2:
        return bin(decimal)[2:]
    elif to_base == 8:
        return oct(decimal)[2:]
    elif to_base == 10:
        return str(decimal)
    elif to_base == 16:
        return hex(decimal)[2:].upper()


def auto_detect(num):
    if num.startswith(("0b", "0B")):
        return num[2:], 2
    elif num.startswith(("0o", "0O")):
        return num[2:], 8
    elif num.startswith(("0x", "0X")):
        return num[2:], 16
    else:
        return num, 10


while True:

    print("\n===== NUMBER BASE CONVERTER =====")
    print("1. Convert")
    print("2. Auto Detect")
    print("3. View History")
    print("4. Save History")
    print("5. Exit")

    choice = input("Enter choice: ")

    if choice == "1":

        number = input("Enter number: ")

        print("\nBases")
        print("2 - Binary")
        print("8 - Octal")
        print("10 - Decimal")
        print("16 - Hexadecimal")

        try:
            from_base = int(input("Source base: "))
            to_base = int(input("Target base: "))

            if from_base not in [2, 8, 10, 16] or to_base not in [2, 8, 10, 16]:
                print("Invalid base.")
                continue

            if not validate(number, from_base):
                print("Invalid number for selected base.")
                continue

            result = convert(number, from_base, to_base)

            print("\nResult:", result)

            if to_base == 2:
                print("Bit Length:", len(result))
                print("Grouped:", " ".join(
                    result[max(i - 4, 0):i]
                    for i in range(len(result), 0, -4)
                )[::-1])

            history.append(
                f"{number} (Base {from_base}) -> {result} (Base {to_base})"
            )

        except ValueError:
            print("Invalid input.")

    elif choice == "2":

        num = input("Enter number (0b,0o,0x supported): ")

        value, base = auto_detect(num)

        if not validate(value, base):
            print("Invalid number.")
            continue

        decimal = int(value, base)

        print("\nDetected Base:", base)
        print("Binary      :", bin(decimal)[2:])
        print("Octal       :", oct(decimal)[2:])
        print("Decimal     :", decimal)
        print("Hexadecimal :", hex(decimal)[2:].upper())

    elif choice == "3":

        if not history:
            print("No history available.")
        else:
            print("\n===== HISTORY =====")
            for i, item in enumerate(history, 1):
                print(f"{i}. {item}")

    elif choice == "4":

        with open("history.txt", "w") as file:
            for item in history:
                file.write(item + "\n")

        print("History saved to history.txt")

    elif choice == "5":
        print("Goodbye!")
        break

    else:
        print("Invalid choice.")