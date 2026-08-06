import os
import random
import string
import hashlib


# ================= ACCOUNT CLASS =================

class Account:
    def __init__(self, website, username, password):
        self.website = website
        self.username = username
        self.password = password

    def display(self, show_password=False):
        print(f"\nWebsite : {self.website}")
        print(f"Username: {self.username}")

        if show_password:
            print(f"Password: {self.password}")
        else:
            print(f"Password: {'*' * len(self.password)}")


# ================= ENCRYPTION CLASS =================

class Encryption:

    @staticmethod
    def encrypt(text):
        return "".join(chr(ord(c) + 5) for c in text)

    @staticmethod
    def decrypt(text):
        return "".join(chr(ord(c) - 5) for c in text)

    @staticmethod
    def hash_password(password):
        return hashlib.sha256(password.encode()).hexdigest()

    @staticmethod
    def generate_password(length=12):
        chars = (
            string.ascii_letters +
            string.digits +
            "!@#$%^&*"
        )

        return "".join(
            random.choice(chars)
            for _ in range(length)
        )

    @staticmethod
    def strength(password):

        score = 0

        if len(password) >= 8:
            score += 1

        if any(c.isupper() for c in password):
            score += 1

        if any(c.islower() for c in password):
            score += 1

        if any(c.isdigit() for c in password):
            score += 1

        if any(c in "!@#$%^&*" for c in password):
            score += 1

        levels = {
            1: "Very Weak",
            2: "Weak",
            3: "Medium",
            4: "Strong",
            5: "Very Strong"
        }

        return levels.get(score, "Very Weak")


# ================= VAULT CLASS =================

class Vault:

    FILE = "vault.txt"

    def __init__(self):
        self.accounts = []
        self.master_password = None
        self.load()

    def setup_master(self):

        if self.master_password is None:

            print("\nCreate Master Password")
            password = input("Master Password: ")

            self.master_password = Encryption.hash_password(password)

    def authenticate(self):

        password = input("Enter Master Password: ")

        return (
            Encryption.hash_password(password)
            == self.master_password
        )


    def load(self):

        if not os.path.exists(self.FILE):
            return

        with open(self.FILE,"r") as file:

            data = file.readlines()

            if data:

                self.master_password = data[0].strip()

                for line in data[1:]:

                    website, username, password = line.strip().split("|")

                    self.accounts.append(
                        Account(
                            website,
                            username,
                            Encryption.decrypt(password)
                        )
                    )


    def save(self):

        with open(self.FILE,"w") as file:

            file.write(
                str(self.master_password) + "\n"
            )

            for acc in self.accounts:

                file.write(
                    f"{acc.website}|{acc.username}|"
                    f"{Encryption.encrypt(acc.password)}\n"
                )


    def add_account(self):

        website = input("Website: ")
        username = input("Username: ")

        choice = input(
            "Generate password? (y/n): "
        ).lower()

        if choice == "y":

            password = Encryption.generate_password()

            print(
                "Generated:",
                password
            )

        else:
            password = input("Password: ")


        print(
            "Strength:",
            Encryption.strength(password)
        )

        self.accounts.append(
            Account(
                website,
                username,
                password
            )
        )

        self.save()

        print("Account Added!")


    def search(self):

        name = input(
            "Search website: "
        ).lower()

        for acc in self.accounts:

            if acc.website.lower() == name:

                acc.display(True)
                return

        print("Account not found")


    def show_all(self):

        if not self.accounts:

            print("Vault Empty")
            return


        for i,acc in enumerate(self.accounts,1):

            print(
                f"\nAccount {i}"
            )

            acc.display()


    def update_password(self):

        website = input(
            "Website: "
        )

        for acc in self.accounts:

            if acc.website == website:

                acc.password = input(
                    "New Password: "
                )

                self.save()

                print(
                    "Password Updated"
                )

                return

        print("Not found")


    def delete_account(self):

        website = input(
            "Website: "
        )

        for acc in self.accounts:

            if acc.website == website:

                self.accounts.remove(acc)

                self.save()

                print(
                    "Deleted Successfully"
                )

                return

        print("Not found")


# ================= MAIN PROGRAM =================


vault = Vault()

vault.setup_master()


if not vault.authenticate():

    print(
        "Access Denied!"
    )

    exit()


while True:

    print("""
========== PASSWORD VAULT ==========

1. Add Account
2. Search Account
3. View All Accounts
4. Generate Password
5. Update Password
6. Delete Account
7. Exit

""")

    choice = input(
        "Choice: "
    )


    if choice == "1":
        vault.add_account()

    elif choice == "2":
        vault.search()

    elif choice == "3":
        vault.show_all()

    elif choice == "4":

        length = int(
            input("Length: ")
        )

        password = Encryption.generate_password(length)

        print(
            "Password:",
            password
        )

        print(
            "Strength:",
            Encryption.strength(password)
        )


    elif choice == "5":
        vault.update_password()


    elif choice == "6":
        vault.delete_account()


    elif choice == "7":

        print(
            "Goodbye!"
        )

        break


    else:

        print(
            "Invalid Option"
        )