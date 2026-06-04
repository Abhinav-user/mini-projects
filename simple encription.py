def encrypt(text, shift):
    result = ""

    for char in text:
        if char.isalpha():
            if char.isupper():
                result += chr((ord(char) - ord('A') + shift) % 26 + ord('A'))
            else:
                result += chr((ord(char) - ord('a') + shift) % 26 + ord('a'))
        else:
            result += char

    return result


def decrypt(text, shift):
    return encrypt(text, -shift)


def read_file(filename):
    try:
        with open(filename, "r") as file:
            return file.read()
    except FileNotFoundError:
        print("File not found.")
        return None


def write_file(filename, content):
    with open(filename, "w") as file:
        file.write(content)


while True:
    print("\n=== Caesar Cipher File Tool ===")
    print("1. Encrypt File")
    print("2. Decrypt File")
    print("3. Exit")

    choice = input("Enter your choice: ")

    if choice == "1":
        input_file = input("Enter input file name: ")
        output_file = input("Enter output file name: ")
        shift = int(input("Enter shift value: "))

        text = read_file(input_file)

        if text is not None:
            encrypted_text = encrypt(text, shift)
            write_file(output_file, encrypted_text)
            print("File encrypted successfully!")

    elif choice == "2":
        input_file = input("Enter encrypted file name: ")
        output_file = input("Enter output file name: ")
        shift = int(input("Enter shift value: "))

        text = read_file(input_file)

        if text is not None:
            decrypted_text = decrypt(text, shift)
            write_file(output_file, decrypted_text)
            print("File decrypted successfully!")

    elif choice == "3":
        print("Goodbye!")
        break

    else:
        print("Invalid choice. Try again.")