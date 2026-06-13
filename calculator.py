import math

history = []

def calculator():
    print("\n--- Advanced Calculator ---")
    print("Operators: +, -, *, /, %, **, //, sqrt, max, min, avg")

    operation = input("Choose operation: ").lower()

    try:
        if operation == "sqrt":
            num = float(input("Enter number: "))

            if num < 0:
                print("Cannot find square root of negative number.")
                return

            result = math.sqrt(num)

        else:
            num1 = float(input("First number: "))
            num2 = float(input("Second number: "))

            if operation == "+":
                result = num1 + num2

            elif operation == "-":
                result = num1 - num2

            elif operation == "*":
                result = num1 * num2

            elif operation == "/":
                if num2 == 0:
                    print("Cannot divide by zero.")
                    return
                result = num1 / num2

            elif operation == "%":
                result = num1 % num2

            elif operation == "**":
                result = num1 ** num2

            elif operation == "//":
                result = num1 // num2

            elif operation == "max":
                result = max(num1, num2)

            elif operation == "min":
                result = min(num1, num2)

            elif operation == "avg":
                result = (num1 + num2) / 2

            else:
                print("Invalid operation.")
                return

        print("Result:", result)

        history.append(f"{operation} = {result}")

    except ValueError:
        print("Invalid input.")

while True:
    print("\n1. Calculator")
    print("2. View History")
    print("3. Clear History")
    print("4. Exit")

    choice = input("Enter choice: ")

    if choice == "1":
        calculator()

    elif choice == "2":
        print("\n--- History ---")
        if len(history) == 0:
            print("No calculations yet.")
        else:
            for item in history:
                print(item)

    elif choice == "3":
        history.clear()
        print("History cleared.")

    elif choice == "4":
        print("Calculator closed.")
        break

    else:
        print("Invalid choice.")