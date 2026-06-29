def reverse_number(num):
    reverse = 0

    while num > 0:
        digit = num % 10
        reverse = reverse * 10 + digit
        num //= 10

    return reverse


while True:
    print("\n====== Palindrome Number Checker ======")
    print("1. Check a Number")
    print("2. Exit")

    choice = input("Enter your choice: ")

    if choice == "1":
        try:
            number = int(input("Enter a number: "))

            if number < 0:
                print("Negative numbers are not considered palindromes.")
                continue

            reversed_number = reverse_number(number)

            print("\nOriginal Number :", number)
            print("Reversed Number :", reversed_number)

            if number == reversed_number:
                print("Result : Palindrome")
            else:
                print("Result : Not a Palindrome")

            print("\nExtra Information")
            print("------------------")
            print("Number of Digits :", len(str(number)))
            print("Even" if number % 2 == 0 else "Odd")

        except ValueError:
            print("Invalid input! Please enter an integer.")

    elif choice == "2":
        print("Thank you for using the program.")
        break

    else:
        print("Invalid choice.")