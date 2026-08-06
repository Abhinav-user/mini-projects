import os
import random
import string


# ---------- Account Class ----------
class Account:
    def __init__(self, website, username, password):
        self.website = website
        self.username = username
        self.password = password

    def display(self):
        print(f"Website : {self.website}")
        print(f"Username: {self.username}")
        print(f"Password: {self.password}")


# ---------- Encryption Class ----------
class Encryption:
    KEY = 7

    @staticmethod
    def encrypt(text):
        return "".join(chr(ord(c) + Encryption.KEY) for c in text)

    @staticmethod
    def decrypt(text):
        return "".join(chr(ord(c) - Encryption.KEY) for c in text)

    @staticmethod
    def generate_password(length=12):
        chars = string.ascii_letters + string.digits + "!@#$%^&*"
        return "".join(random.choice(chars) for _ in range(length))


# ---------- Vault Class ----------
class Vault:
    FILE = "vault.txt"

    def __init__(self):
        self.accounts = []
        self.load()

    def load(self):
        if not os.path.exists(self.FILE):
            return

        with open(self.FILE, "r") as f:
            for line in f:
                website, username, enc_pass = line.strip().split("|")
                password = Encryption.decrypt(enc_pass)
                self.accounts.append(Account(website, username, password))

    def save(self):
        with open(self.FILE, "w") as f:
            for acc in self.accounts:
                enc_pass = Encryption.encrypt(acc.password)
                f.write(f"{acc.website}|{acc.username}|{enc_pass}\n")

    def add_account(self):
        website = input("Website : ")
        username = input("Username: ")

        choice = input("Generate password? (y/n): ").lower()

        if choice == "y":
            password = Encryption.generate_password()
            print("Generated Password:", password)
        else:
            password = input("Password: ")

        self.accounts.append(Account(website, username, password))
        self.save()
        print("Account Saved Successfully!")

    def search(self):
        website = input("Enter Website: ").lower()

        found = False

        for acc in self.accounts:
            if acc.website.lower() == website:
                print("\nAccount Found")
                print("-" * 25)
                acc.display()
                found = True

        if not found:
            print("No account found.")

    def display_all(self):
        if not self.accounts:
            print("Vault is empty.")
            return

        print("\nSaved Accounts")
        print("-" * 30)

        for i, acc in enumerate(self.accounts, 1):
            print(f"\nAccount {i}")
            acc.display()


# ---------- Main ----------
vault = Vault()

while True:
    print("\n====== PASSWORD VAULT ======")
    print("1. Add Account")
    print("2. Search Account")
    print("3. View All Accounts")
    print("4. Generate Strong Password")
    print("5. Exit")

    choice = input("Enter choice: ")

    if choice == "1":
        vault.add_account()

    elif choice == "2":
        vault.search()

    elif choice == "3":
        vault.display_all()

    elif choice == "4":
        length = int(input("Password Length: "))
        print("Generated Password:", Encryption.generate_password(length))

    elif choice == "5":
        print("Thank you for using Password Vault!")
        break

    else:
        print("Invalid Choice!")