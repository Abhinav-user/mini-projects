import itertools
import string
import time

CHARACTERS = string.ascii_lowercase


# -----------------------------
# Password Strength Checker
# -----------------------------
def password_strength(password):
    if len(password) <= 2:
        return "Very Weak"
    elif len(password) == 3:
        return "Weak"
    elif len(password) == 4:
        return "Moderate"
    else:
        return "Strong"


# -----------------------------
# Calculate Search Space
# -----------------------------
def total_combinations(length):
    total = 0
    for i in range(1, length + 1):
        total += len(CHARACTERS) ** i
    return total


# -----------------------------
# Brute Force Simulation
# -----------------------------
def brute_force(password, show_attempts):

    total = total_combinations(len(password))

    attempts = 0
    start = time.time()

    print("\nStarting simulation...\n")

    for length in range(1, len(password) + 1):

        for guess in itertools.product(CHARACTERS, repeat=length):

            attempts += 1
            guess = "".join(guess)

            if show_attempts:
                print(f"Trying: {guess}", end="\r")

            if attempts % 50000 == 0:
                progress = (attempts / total) * 100
                print(
                    f"\nProgress: {progress:.2f}% ({attempts:,}/{total:,}) attempts"
                )

            if guess == password:

                end = time.time()
                elapsed = end - start

                print("\n\n========== PASSWORD FOUND ==========")
                print(f"Password           : {guess}")
                print(f"Attempts           : {attempts:,}")
                print(f"Total Search Space : {total:,}")
                print(f"Time Taken         : {elapsed:.4f} seconds")

                if elapsed > 0:
                    print(f"Attempts/Second    : {attempts/elapsed:,.0f}")

                print("====================================\n")
                return


# -----------------------------
# Main Program
# -----------------------------
def main():

    while True:

        print("====================================")
        print(" BRUTE FORCE PASSWORD SIMULATOR")
        print("====================================")
        print("1. Start Simulation")
        print("2. Exit")

        choice = input("\nEnter choice: ")

        if choice == "2":
            print("\nThank you for using the simulator.")
            break

        elif choice == "1":

            password = input(
                "\nEnter password (lowercase letters only, max 4 characters): "
            ).strip()

            if not password.isalpha() or not password.islower():
                print("\nError: Only lowercase letters are allowed.\n")
                continue

            if len(password) > 4:
                print("\nError: Maximum length is 4.\n")
                continue

            print("\nPassword Strength :", password_strength(password))
            print("Search Space      :", format(total_combinations(len(password)), ","))

            option = input("\nShow every attempt? (y/n): ").lower()

            brute_force(password, option == "y")

        else:
            print("\nInvalid choice.\n")


if __name__ == "__main__":
    main()