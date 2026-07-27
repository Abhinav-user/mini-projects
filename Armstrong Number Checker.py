# ===============================================
#        ADVANCED ARMSTRONG NUMBER CHECKER
# ===============================================

history = []


def is_armstrong(num):
    """Returns True if the given number is an Armstrong number."""
    digits = len(str(num))
    armstrong_sum = sum(int(digit) ** digits for digit in str(num))
    return armstrong_sum == num


while True:
    print("\n" + "=" * 50)
    print("        ARMSTRONG NUMBER PROGRAM")
    print("=" * 50)
    print("1. Check Armstrong Number")
    print("2. Find Armstrong Numbers in a Range")
    print("3. View History")
    print("4. Exit")

    choice = input("\nEnter your choice (1-4): ")

    # -------------------------------
    # Option 1
    # -------------------------------
    if choice == "1":
        try:
            number = int(input("\nEnter a positive integer: "))

            if number < 0:
                print("Please enter a positive number.")
                continue

            if is_armstrong(number):
                result = "Armstrong Number"
            else:
                result = "Not an Armstrong Number"

            history.append((number, result))

            print("\n" + "-" * 40)
            print(f"Number : {number}")
            print(f"Result : {result}")
            print("-" * 40)

        except ValueError:
            print("Invalid input! Enter integers only.")

    # -------------------------------
    # Option 2
    # -------------------------------
    elif choice == "2":
        try:
            start = int(input("Enter starting number: "))
            end = int(input("Enter ending number: "))

            print(f"\nArmstrong Numbers between {start} and {end}:\n")

            found = False

            for num in range(start, end + 1):
                if num >= 0 and is_armstrong(num):
                    print(num, end="  ")
                    found = True

            if not found:
                print("No Armstrong numbers found.")

            print()

        except ValueError:
            print("Invalid input!")

    # -------------------------------
    # Option 3
    # -------------------------------
    elif choice == "3":

        if not history:
            print("\nNo history available.")
        else:
            print("\nHistory")
            print("-" * 35)
            print("{:<15}{}".format("Number", "Result"))
            print("-" * 35)

            for number, result in history:
                print("{:<15}{}".format(number, result))

    # -------------------------------
    # Option 4
    # -------------------------------
    elif choice == "4":
        print("\nThank you for using the program!")
        break

    # -------------------------------
    # Invalid Choice
    # -------------------------------
    else:
        print("Invalid choice! Please select between 1 and 4.")