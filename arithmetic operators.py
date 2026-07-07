import math
from datetime import datetime

history = []
memory = 0

def add_history(text):
    timestamp = datetime.now().strftime("%H:%M:%S")
    history.append(f"[{timestamp}] {text}")

while True:

    print("\n" + "=" * 50)
    print("      SMART SCIENTIFIC CALCULATOR")
    print("=" * 50)

    print("1. Arithmetic Operations")
    print("2. Scientific Operations")
    print("3. Number Analysis")
    print("4. Memory Functions")
    print("5. View History")
    print("6. Clear History")
    print("7. Exit")

    choice = input("\nChoose option: ")

    if choice == "1":

        try:
            num1 = float(input("\nEnter first number : "))
            num2 = float(input("Enter second number: "))
        except ValueError:
            print("Invalid number.")
            continue

        print("\n----- RESULTS -----")

        print(f"{num1} + {num2} = {num1 + num2}")
        add_history(f"{num1} + {num2} = {num1 + num2}")

        print(f"{num1} - {num2} = {num1 - num2}")
        add_history(f"{num1} - {num2} = {num1 - num2}")

        print(f"{num1} * {num2} = {num1 * num2}")
        add_history(f"{num1} * {num2} = {num1 * num2}")

        if num2 != 0:
            print(f"{num1} / {num2} = {round(num1 / num2, 4)}")
            print(f"{num1} // {num2} = {num1 // num2}")
            print(f"{num1} % {num2} = {num1 % num2}")
            add_history(f"{num1} / {num2} = {round(num1 / num2, 4)}")
        else:
            print("Division not possible.")

        print(f"{num1} ** {num2} = {num1 ** num2}")
        add_history(f"{num1} ** {num2} = {num1 ** num2}")

        print(f"Average = {(num1 + num2) / 2}")
        print(f"Absolute Difference = {abs(num1 - num2)}")
        print(f"Largest = {max(num1, num2)}")
        print(f"Smallest = {min(num1, num2)}")

    elif choice == "2":

        try:
            num = float(input("\nEnter a number: "))
        except ValueError:
            print("Invalid input.")
            continue

        print("\n1. Square")
        print("2. Cube")
        print("3. Square Root")
        print("4. Factorial")
        print("5. Sin")
        print("6. Cos")
        print("7. Tan")
        print("8. Log10")

        sci = input("Choose: ")

        if sci == "1":
            result = num ** 2
            print("Square =", result)

        elif sci == "2":
            result = num ** 3
            print("Cube =", result)

        elif sci == "3":
            if num >= 0:
                result = math.sqrt(num)
                print("Square Root =", result)
            else:
                print("Cannot calculate.")

        elif sci == "4":
            if num >= 0 and num.is_integer():
                result = math.factorial(int(num))
                print("Factorial =", result)
            else:
                print("Enter a positive integer.")

        elif sci == "5":
            result = math.sin(math.radians(num))
            print("Sin =", result)

        elif sci == "6":
            result = math.cos(math.radians(num))
            print("Cos =", result)

        elif sci == "7":
            result = math.tan(math.radians(num))
            print("Tan =", result)

        elif sci == "8":
            if num > 0:
                result = math.log10(num)
                print("Log10 =", result)
            else:
                print("Must be positive.")

        else:
            print("Invalid choice.")
            continue

        if 'result' in locals():
            add_history(f"Scientific Result = {result}")

    elif choice == "3":

        try:
            n = int(input("\nEnter an integer: "))
        except ValueError:
            print("Invalid integer.")
            continue

        print("\n----- NUMBER ANALYSIS -----")

        print("Even" if n % 2 == 0 else "Odd")

        if n > 0:
            print("Positive")
        elif n < 0:
            print("Negative")
        else:
            print("Zero")

        prime = True

        if n < 2:
            prime = False
        else:
            for i in range(2, int(math.sqrt(n)) + 1):
                if n % i == 0:
                    prime = False
                    break

        print("Prime Number" if prime else "Not Prime")

        if str(abs(n)) == str(abs(n))[::-1]:
            print("Palindrome Number")
        else:
            print("Not Palindrome")

        add_history(f"Analysed Number {n}")

    elif choice == "4":

        print("\nMemory Value =", memory)

        print("1. M+")
        print("2. M-")
        print("3. MR")
        print("4. MC")

        m = input("Choose: ")

        if m == "1":
            value = float(input("Value: "))
            memory += value
            print("Memory =", memory)

        elif m == "2":
            value = float(input("Value: "))
            memory -= value
            print("Memory =", memory)

        elif m == "3":
            print("Memory Recall =", memory)

        elif m == "4":
            memory = 0
            print("Memory Cleared")

    elif choice == "5":

        print("\n----- HISTORY -----")

        if history:
            for item in history:
                print(item)
        else:
            print("No history available.")

    elif choice == "6":

        history.clear()
        print("History Cleared.")

    elif choice == "7":

        print("\nSession Summary")
        print("Calculations Performed:", len(history))
        print("Goodbye!")
        break

    else:
        print("Invalid choice.")

    input("\nPress Enter to continue...")