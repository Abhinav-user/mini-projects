def calculator():

    print("\n--- Simple Calculator ---")

    try:
        num1 = float(input("First number: "))
        operator = input("Choose +, -, *, /, %, ** : ")
        num2 = float(input("Second number: "))

        if operator == "+":
            result = num1 + num2

        elif operator == "-":
            result = num1 - num2

        elif operator == "*":
            result = num1 * num2

        elif operator == "/":

            if num2 == 0:
                print("Error: Cannot divide by zero")
                return

            result = num1 / num2

        elif operator == "%":
            result = num1 % num2

        elif operator == "**":
            result = num1 ** num2

        else:
            print("Invalid operator")
            return

        print(f"\nResult: {result}")

    except ValueError:
        print("Invalid number entered")


while True:

    calculator()

    choice = input("\nDo another calculation? (yes/no): ").lower()

    if choice != "yes":
        print("Calculator closed.")
        break