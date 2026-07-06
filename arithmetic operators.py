import math

print("=" * 45)
print("        ADVANCED PYTHON CALCULATOR")
print("=" * 45)

while True:

    num1 = float(input("\nEnter first number : "))
    num2 = float(input("Enter second number: "))

    print("\nChoose an option")
    print("1. Show All Arithmetic Operations")
    print("2. Compare Numbers")
    print("3. Square Root")
    print("4. Absolute Difference")
    print("5. Largest & Smallest")
    print("6. Swap Numbers")
    print("7. Enter New Numbers")
    print("8. Exit")

    choice = input("Enter choice: ")

    if choice == "1":

        print("\n----- Arithmetic Operations -----")

        print(f"{num1} + {num2} = {num1 + num2}")
        print(f"{num1} - {num2} = {num1 - num2}")
        print(f"{num1} * {num2} = {num1 * num2}")

        if num2 != 0:
            print(f"{num1} / {num2} = {round(num1 / num2,2)}")
            print(f"{num1} // {num2} = {num1 // num2}")
            print(f"{num1} % {num2} = {num1 % num2}")
        else:
            print("Division, Floor Division and Modulus not possible.")

        print(f"{num1} ** {num2} = {num1 ** num2}")

    elif choice == "2":

        print("\n----- Comparison -----")

        if num1 > num2:
            print(num1, "is greater.")
        elif num2 > num1:
            print(num2, "is greater.")
        else:
            print("Both numbers are equal.")

    elif choice == "3":

        print("\n----- Square Root -----")

        if num1 >= 0:
            print("√", num1, "=", round(math.sqrt(num1), 2))
        else:
            print("Square root of first number not possible.")

        if num2 >= 0:
            print("√", num2, "=", round(math.sqrt(num2), 2))
        else:
            print("Square root of second number not possible.")

    elif choice == "4":

        print("\nAbsolute Difference =", abs(num1 - num2))

    elif choice == "5":

        print("\nLargest Number :", max(num1, num2))
        print("Smallest Number:", min(num1, num2))

    elif choice == "6":

        num1, num2 = num2, num1

        print("\nNumbers Swapped!")
        print("First Number :", num1)
        print("Second Number:", num2)

    elif choice == "7":
        continue

    elif choice == "8":
        print("\nThank you for using the calculator!")
        break

    else:
        print("\nInvalid choice!")

    input("\nPress Enter to continue...")