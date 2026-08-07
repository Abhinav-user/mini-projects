import itertools
import string
import time
import random


# ============================================================
# PASSWORD CLASS
# ============================================================

class Password:

    def __init__(self, password):
        self.password = password

    def length(self):
        return len(self.password)

    def strength(self):
        score = 0

        if len(self.password) >= 4:
            score += 1
        if len(self.password) >= 8:
            score += 1
        if any(c.isupper() for c in self.password):
            score += 1
        if any(c.isdigit() for c in self.password):
            score += 1
        if any(c in string.punctuation for c in self.password):
            score += 1

        if score <= 1:
            return "Very Weak"
        elif score == 2:
            return "Weak"
        elif score == 3:
            return "Moderate"
        elif score == 4:
            return "Strong"
        return "Very Strong"

    def character_set(self):
        characters = ""

        if any(c.islower() for c in self.password):
            characters += string.ascii_lowercase

        if any(c.isupper() for c in self.password):
            characters += string.ascii_uppercase

        if any(c.isdigit() for c in self.password):
            characters += string.digits

        if any(c in string.punctuation for c in self.password):
            characters += string.punctuation

        return characters

    def search_space(self):
        charset = self.character_set()
        return len(charset) ** len(self.password)


# ============================================================
# PASSWORD GENERATOR
# ============================================================

class PasswordGenerator:

    CHARACTERS = (
        string.ascii_letters +
        string.digits +
        string.punctuation
    )

    @staticmethod
    def generate(length):

        return "".join(
            random.choice(PasswordGenerator.CHARACTERS)
            for _ in range(length)
        )


# ============================================================
# BRUTE FORCE SIMULATOR
# ============================================================

class BruteForceSimulator:

    def __init__(self, password, show_attempts=False):

        self.password = password
        self.show_attempts = show_attempts
        self.attempts = 0
        self.start_time = 0
        self.end_time = 0

    def progress_bar(self, current, total):

        percentage = current / total
        bar_length = 30

        filled = int(bar_length * percentage)

        bar = "#" * filled + "-" * (bar_length - filled)

        print(
            f"\r[{bar}] "
            f"{percentage * 100:6.2f}% "
            f"| {current:,}/{total:,}",
            end=""
        )

    def run(self):

        password_obj = Password(self.password)

        charset = password_obj.character_set()
        total = password_obj.search_space()

        self.start_time = time.perf_counter()

        print("\n")
        print("=" * 55)
        print("             BRUTE FORCE SIMULATION")
        print("=" * 55)

        print(f"Character Set : {len(charset)} characters")
        print(f"Password Size : {len(self.password)}")
        print(f"Search Space  : {total:,}")
        print("=" * 55)

        for length in range(1, len(self.password) + 1):

            for combination in itertools.product(
                charset,
                repeat=length
            ):

                self.attempts += 1

                guess = "".join(combination)

                if self.show_attempts:

                    print(
                        f"Trying: {guess:<15}",
                        end="\r"
                    )

                elif self.attempts % 1000 == 0:

                    self.progress_bar(
                        self.attempts,
                        total
                    )

                if guess == self.password:

                    self.end_time = time.perf_counter()

                    self.show_result(
                        guess,
                        total
                    )

                    return True

        return False

    def show_result(self, guess, total):

        elapsed = self.end_time - self.start_time

        print("\n\n")
        print("=" * 55)
        print("              PASSWORD FOUND")
        print("=" * 55)

        print(f"Password          : {guess}")
        print(f"Attempts          : {self.attempts:,}")
        print(f"Search Space      : {total:,}")
        print(f"Time Taken        : {elapsed:.4f} seconds")

        if elapsed > 0:

            speed = self.attempts / elapsed

            print(f"Attempts / Second : {speed:,.0f}")

        progress = (self.attempts / total) * 100

        print(f"Search Completed  : {progress:.2f}%")

        print("=" * 55)


# ============================================================
# REPORT MANAGER
# ============================================================

class ReportManager:

    FILE_NAME = "bruteforce_report.txt"

    @staticmethod
    def save(password, attempts, elapsed):

        with open(
            ReportManager.FILE_NAME,
            "a",
            encoding="utf-8"
        ) as file:

            file.write("\n")
            file.write("=" * 50 + "\n")
            file.write("BRUTE FORCE SIMULATION REPORT\n")
            file.write("=" * 50 + "\n")
            file.write(f"Password : {password}\n")
            file.write(f"Attempts : {attempts:,}\n")
            file.write(f"Time     : {elapsed:.4f} seconds\n")
            file.write("=" * 50 + "\n")

        print(
            f"\nReport saved to "
            f"'{ReportManager.FILE_NAME}'"
        )


# ============================================================
# MAIN APPLICATION
# ============================================================

class PasswordSecurityApp:

    def display_banner(self):

        print("\n" + "=" * 60)
        print("          PASSWORD SECURITY SIMULATOR")
        print("=" * 60)

    def generate_password(self):

        try:

            length = int(
                input("\nEnter password length: ")
            )

            if length < 1 or length > 6:

                print(
                    "Length must be between 1 and 6."
                )

                return

            password = PasswordGenerator.generate(length)

            print("\nGenerated Password:", password)

        except ValueError:

            print("Please enter a valid number.")

    def start_simulation(self):

        password = input(
            "\nEnter password to simulate: "
        ).strip()

        if not password:

            print("Password cannot be empty.")
            return

        if len(password) > 6:

            print(
                "For this educational simulator, "
                "maximum length is 6."
            )

            return

        password_obj = Password(password)

        print("\n---------- PASSWORD ANALYSIS ----------")
        print(f"Length       : {password_obj.length()}")
        print(f"Strength     : {password_obj.strength()}")
        print(
            f"Character Set: "
            f"{len(password_obj.character_set())}"
        )
        print(
            f"Search Space : "
            f"{password_obj.search_space():,}"
        )
        print("----------------------------------------")

        show = input(
            "\nShow every attempt? (y/n): "
        ).lower()

        simulator = BruteForceSimulator(
            password,
            show == "y"
        )

        found = simulator.run()

        if found:

            elapsed = (
                simulator.end_time -
                simulator.start_time
            )

            save = input(
                "\nSave result to report? (y/n): "
            ).lower()

            if save == "y":

                ReportManager.save(
                    password,
                    simulator.attempts,
                    elapsed
                )

    def run(self):

        while True:

            self.display_banner()

            print("1. Analyze & Simulate Password")
            print("2. Generate Random Password")
            print("3. Exit")

            choice = input(
                "\nEnter choice: "
            ).strip()

            if choice == "1":

                self.start_simulation()

            elif choice == "2":

                self.generate_password()

            elif choice == "3":

                print(
                    "\nThank you for using "
                    "Password Security Simulator."
                )

                break

            else:

                print(
                    "\nInvalid choice."
                )


# ============================================================
# PROGRAM ENTRY POINT
# ============================================================

if __name__ == "__main__":

    app = PasswordSecurityApp()
    app.run()