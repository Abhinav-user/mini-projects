import time

# ==========================================
# PALINDROME NUMBER ANALYZER
# ==========================================

def reverse_number(num):
    reverse = 0
    temp = num

    print("\nReversing Process")
    print("-" * 35)

    while temp > 0:
        digit = temp % 10
        reverse = reverse * 10 + digit
        print(f"Digit: {digit}  --> Reverse: {reverse}")
        temp //= 10

    return reverse


def digit_sum(num):
    total = 0
    while num > 0:
        total += num % 10
        num //= 10
    return total


def is_prime(num):
    if num < 2:
        return False

    for i in range(2, int(num ** 0.5) + 1):
        if num % i == 0:
            return False

    return True


def is_armstrong(num):
    digits = len(str(num))
    total = 0
    temp = num

    while temp > 0:
        digit = temp % 10
        total += digit ** digits
        temp //= 10

    return total == num


def is_perfect(num):
    if num <= 1:
        return False

    total = 1

    for i in range(2, int(num ** 0.5) + 1):
        if num % i == 0:
            total += i

            if i != num // i:
                total += num // i

    return total == num


def number_info(number):

    reversed_number = reverse_number(number)

    print("\n" + "=" * 50)
    print("NUMBER ANALYSIS")
    print("=" * 50)

    print(f"Original Number     : {number}")
    print(f"Reversed Number     : {reversed_number}")

    if number == reversed_number:
        print("Palindrome          : Yes")
    else:
        print("Palindrome          : No")

    print(f"Digits              : {len(str(number))}")
    print(f"Sum of Digits       : {digit_sum(number)}")
    print(f"First Digit         : {str(number)[0]}")
    print(f"Last Digit          : {str(number)[-1]}")

    if number % 2 == 0:
        print("Even/Odd            : Even")
    else:
        print("Even/Odd            : Odd")

    if is_prime(number):
        print("Prime Number        : Yes")
    else:
        print("Prime Number        : No")

    if is_armstrong(number):
        print("Armstrong Number    : Yes")
    else:
        print("Armstrong Number    : No")

    if is_perfect(number):
        print("Perfect Number      : Yes")
    else:
        print("Perfect Number      : No")

    print(f"Binary              : {bin(number)}")
    print(f"Octal               : {oct(number)}")
    print(f"Hexadecimal         : {hex(number)}")

    print("=" * 50)


# ==========================================
# MAIN PROGRAM
# ==========================================

while True:

    print("\n" + "=" * 55)
    print("        ADVANCED PALINDROME NUMBER ANALYZER")
    print("=" * 55)

    print("1. Check a Number")
    print("2. About Palindrome")
    print("3. Exit")

    choice = input("\nEnter your choice (1-3): ")

    if choice == "1":

        try:
            number = int(input("\nEnter a positive integer: "))

            if number < 0:
                print("\nNegative numbers are not considered palindromes.")
                continue

            print("\nAnalyzing number...")
            time.sleep(1)

            number_info(number)

        except ValueError:
            print("\nPlease enter a valid integer.")

    elif choice == "2":

        print("\nA palindrome number reads the same")
        print("forward and backward.")

        print("\nExamples:")
        print("121")
        print("1331")
        print("12321")
        print("4554")

        print("\nNon-Palindrome Examples:")
        print("123")
        print("456")
        print("789")

    elif choice == "3":

        print("\nThank you for using the Advanced Palindrome Number Analyzer!")
        print("Goodbye!")
        break

    else:
        print("\nInvalid choice. Please select between 1 and 3.") 