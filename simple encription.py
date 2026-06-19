import os
from collections import Counter
from datetime import datetime

# ---------------------------
# Cipher Functions
# ---------------------------

def caesar_cipher(text, shift):
    result = ""

    for char in text:
        if char.isalpha():
            start = ord('A') if char.isupper() else ord('a')
            result += chr((ord(char) - start + shift) % 26 + start)
        else:
            result += char

    return result

# ---------------------------
# File Operations
# ---------------------------

def read_file(filename):
    try:
        with open(filename, "r", encoding="utf-8") as file:
            return file.read()
    except Exception as e:
        print("Error:", e)
        return None

def write_file(filename, content):
    try:
        with open(filename, "w", encoding="utf-8") as file:
            file.write(content)
        print(f"Saved -> {filename}")
    except Exception as e:
        print("Error:", e)

# ---------------------------
# Statistics
# ---------------------------

def show_stats(text):
    print("\nFILE STATISTICS")
    print("-" * 30)

    print("Characters:", len(text))
    print("Letters:", sum(c.isalpha() for c in text))
    print("Digits:", sum(c.isdigit() for c in text))
    print("Words:", len(text.split()))

# ---------------------------
# Logging
# ---------------------------

def log_action(action):
    with open("cipher_log.txt", "a", encoding="utf-8") as log:
        log.write(
            f"{datetime.now()} | {action}\n"
        )

# ---------------------------
# Frequency Analysis
# ---------------------------

def guess_shift(text):

    letters = [
        c.lower()
        for c in text
        if c.isalpha()
    ]

    if not letters:
        return 0

    most_common = Counter(
        letters
    ).most_common(1)[0][0]

    return (ord(most_common) - ord('e')) % 26

# Encrypt

def encrypt_file():

    filename = input("Input file: ")

    text = read_file(filename)

    if text is None:
        return

    show_stats(text)

    shift = int(input("Shift (1-25): "))

    encrypted = caesar_cipher(
        text,
        shift
    )

    output = input(
        "Output file: "
    )

    write_file(output, encrypted)

    log_action(
        f"Encrypted {filename} -> {output}"
    )


# Decrypt

def decrypt_file():

    filename = input(
        "Encrypted file: "
    )

    text = read_file(filename)

    if text is None:
        return

    shift = int(
        input("Shift (1-25): ")
    )

    decrypted = caesar_cipher(
        text,
        -shift
    )

    output = input(
        "Output file: "
    )

    write_file(output, decrypted)

    log_action(
        f"Decrypted {filename}"
    )

# Brute Force

def brute_force():

    filename = input(
        "Encrypted file: "
    )

    text = read_file(filename)

    if text is None:
        return

    for shift in range(26):

        print(
            f"\nSHIFT {shift}"
        )

        print("-" * 50)

        print(
            caesar_cipher(
                text,
                -shift
            )[:300]
        )

# Auto Crack

def auto_crack():

    filename = input(
        "Encrypted file: "
    )

    text = read_file(filename)

    if text is None:
        return

    shift = guess_shift(text)

    decrypted = caesar_cipher(
        text,
        -shift
    )

    print(
        f"\nEstimated Shift: {shift}"
    )

    print("-" * 50)

    print(decrypted[:500])


# Batch Encrypt

def batch_encrypt():

    folder = input(
        "Folder path: "
    )

    shift = int(
        input("Shift: ")
    )

    output_folder = (
        folder + "_encrypted"
    )

    os.makedirs(
        output_folder,
        exist_ok=True
    )

    count = 0

    for file in os.listdir(folder):

        if file.endswith(".txt"):

            path = os.path.join(
                folder,
                file
            )

            text = read_file(path)

            encrypted = (
                caesar_cipher(
                    text,
                    shift
                )
            )

            write_file(
                os.path.join(
                    output_folder,
                    file
                ),
                encrypted
            )

            count += 1

    print(
        f"\nEncrypted {count} files."
    )

# Main Menu
def main():

    while True:

        print("\n" + "=" * 50)
        print("ADVANCED CAESAR TOOLKIT")
        print("=" * 50)

        print("1. Encrypt File")
        print("2. Decrypt File")
        print("3. Brute Force Crack")
        print("4. Auto Crack")
        print("5. Batch Encrypt Folder")
        print("6. Exit")

        choice = input(
            "\nChoice: "
        )

        if choice == "1":
            encrypt_file()

        elif choice == "2":
            decrypt_file()

        elif choice == "3":
            brute_force()

        elif choice == "4":
            auto_crack()

        elif choice == "5":
            batch_encrypt()

        elif choice == "6":
            print("Goodbye!")
            break

        else:
            print(
                "Invalid option."
            )

if __name__ == "__main__":
    main()