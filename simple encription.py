def caesar_cipher(text, shift):
    result = ""

    for char in text:
        if char.isalpha():
            start = ord('A') if char.isupper() else ord('a')
            result += chr((ord(char) - start + shift) % 26 + start)
        else:
            result += char

    return result


def read_file(filename):
    try:
        with open(filename, "r", encoding="utf-8") as file:
            return file.read()
    except FileNotFoundError:
        print("❌ File not found.")
    except Exception as e:
        print(f"❌ Error: {e}")

    return None


def write_file(filename, content):
    try:
        with open(filename, "w", encoding="utf-8") as file:
            file.write(content)
        print(f"✅ Saved to '{filename}'")
    except Exception as e:
        print(f"❌ Error writing file: {e}")


def encrypt_file():
    filename = input("Input file: ")
    text = read_file(filename)

    if text is None:
        return

    shift = get_shift()

    encrypted = caesar_cipher(text, shift)

    print("\nPreview:")
    print("-" * 40)
    print(encrypted[:300])
    print("-" * 40)

    output = input("Output file name: ")
    write_file(output, encrypted)


def decrypt_file():
    filename = input("Encrypted file: ")
    text = read_file(filename)

    if text is None:
        return

    shift = get_shift()

    decrypted = caesar_cipher(text, -shift)

    print("\nPreview:")
    print("-" * 40)
    print(decrypted[:300])
    print("-" * 40)

    output = input("Output file name: ")
    write_file(output, decrypted)


def brute_force():
    filename = input("Encrypted file: ")
    text = read_file(filename)

    if text is None:
        return

    print("\nTrying all possible shifts:\n")

    for shift in range(26):
        print(f"\nShift {shift}")
        print("-" * 40)
        print(caesar_cipher(text, -shift)[:200])
        print("-" * 40)


def get_shift():
    while True:
        try:
            shift = int(input("Shift value (1-25): "))
            if 1 <= shift <= 25:
                return shift
            print("Enter a value between 1 and 25.")
        except ValueError:
            print("Enter a valid number.")


def main():
    while True:
        print("\n" + "=" * 50)
        print("      CAESAR CIPHER FILE ENCRYPTOR")
        print("=" * 50)
        print("1. Encrypt File")
        print("2. Decrypt File")
        print("3. Crack File (Brute Force)")
        print("4. Exit")

        choice = input("\nChoose an option: ")

        if choice == "1":
            encrypt_file()

        elif choice == "2":
            decrypt_file()

        elif choice == "3":
            brute_force()

        elif choice == "4":
            print("Exiting...")
            break

        else:
            print("Invalid choice.")


if __name__ == "__main__":
    main()