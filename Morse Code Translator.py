# Morse Code Translator

MORSE_CODE = {
    'A': '.-', 'B': '-...', 'C': '-.-.', 'D': '-..',
    'E': '.', 'F': '..-.', 'G': '--.', 'H': '....',
    'I': '..', 'J': '.---', 'K': '-.-', 'L': '.-..',
    'M': '--', 'N': '-.', 'O': '---', 'P': '.--.',
    'Q': '--.-', 'R': '.-.', 'S': '...', 'T': '-',
    'U': '..-', 'V': '...-', 'W': '.--', 'X': '-..-',
    'Y': '-.--', 'Z': '--..',
    '1': '.----', '2': '..---', '3': '...--',
    '4': '....-', '5': '.....', '6': '-....',
    '7': '--...', '8': '---..', '9': '----.',
    '0': '-----',
    ' ': '/'
}

REVERSE_MORSE = {value: key for key, value in MORSE_CODE.items()}


def english_to_morse(text):
    morse = []

    for char in text.upper():
        if char in MORSE_CODE:
            morse.append(MORSE_CODE[char])
        else:
            morse.append('?')

    return " ".join(morse)


def morse_to_english(code):
    words = code.split(" / ")
    result = []

    for word in words:
        letters = word.split()
        translated = ""

        for letter in letters:
            translated += REVERSE_MORSE.get(letter, '?')

        result.append(translated)

    return " ".join(result)


def save_translation(original, translated):
    with open("translations.txt", "a") as file:
        file.write(f"Original   : {original}\n")
        file.write(f"Translated : {translated}\n")
        file.write("-" * 40 + "\n")

    print("Translation saved to translations.txt")


while True:
    print("\n====== MORSE CODE TRANSLATOR ======")
    print("1. English → Morse")
    print("2. Morse → English")
    print("3. Exit")

    choice = input("Enter your choice: ")

    if choice == "1":
        text = input("Enter English text: ")
        result = english_to_morse(text)
        print("\nMorse Code:")
        print(result)

        save = input("\nSave translation? (y/n): ").lower()
        if save == "y":
            save_translation(text, result)

    elif choice == "2":
        code = input("Enter Morse Code (use / between words): ")
        result = morse_to_english(code)
        print("\nEnglish Text:")
        print(result)

        save = input("\nSave translation? (y/n): ").lower()
        if save == "y":
            save_translation(code, result)

    elif choice == "3":
        print("Thank you for using the translator!")
        break

    else:
        print("Invalid choice. Please try again.")