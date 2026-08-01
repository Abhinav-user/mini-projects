import os
from datetime import datetime

MORSE = {
    'A': '.-', 'B': '-...', 'C': '-.-.', 'D': '-..',
    'E': '.', 'F': '..-.', 'G': '--.', 'H': '....',
    'I': '..', 'J': '.---', 'K': '-.-', 'L': '.-..',
    'M': '--', 'N': '-.', 'O': '---', 'P': '.--.',
    'Q': '--.-', 'R': '.-.', 'S': '...', 'T': '-',
    'U': '..-', 'V': '...-', 'W': '.--', 'X': '-..-',
    'Y': '-.--', 'Z': '--..',

    '0': '-----', '1': '.----', '2': '..---',
    '3': '...--', '4': '....-', '5': '.....',
    '6': '-....', '7': '--...', '8': '---..',
    '9': '----.',

    '.': '.-.-.-',
    ',': '--..--',
    '?': '..--..',
    '!': '-.-.--',
    ':': '---...',
    ';': '-.-.-.',
    '-': '-....-',
    '/': '-..-.',
    '@': '.--.-.',
    '(': '-.--.',
    ')': '-.--.-',

    ' ': '/'
}

REVERSE = {v: k for k, v in MORSE.items()}

history = []


def clear():
    os.system("cls" if os.name == "nt" else "clear")


def english_to_morse(text):
    result = []

    for ch in text.upper():
        result.append(MORSE.get(ch, "<UNKNOWN>"))

    return " ".join(result)


def morse_to_english(code):
    words = code.split(" / ")
    sentence = []

    for word in words:
        letters = word.split()
        translated = ""

        for letter in letters:
            translated += REVERSE.get(letter, "□")

        sentence.append(translated)

    return " ".join(sentence)


def save_history():
    filename = f"translations_{datetime.now().strftime('%Y%m%d_%H%M%S')}.txt"

    with open(filename, "w", encoding="utf-8") as file:
        file.write("====== MORSE TRANSLATION HISTORY ======\n\n")

        for i, item in enumerate(history, 1):
            file.write(f"{i}. {item['type']}\n")
            file.write(f"Input : {item['input']}\n")
            file.write(f"Output: {item['output']}\n")
            file.write("-" * 50 + "\n")

    print(f"\nHistory saved as '{filename}'")


def show_history():
    if not history:
        print("\nNo translations yet.")
        return

    print("\n====== HISTORY ======")

    for i, item in enumerate(history, 1):
        print(f"\n{i}. {item['type']}")
        print("Input :", item["input"])
        print("Output:", item["output"])


def statistics(text):
    letters = sum(c.isalpha() for c in text)
    digits = sum(c.isdigit() for c in text)
    spaces = text.count(" ")

    print("\n------ Statistics ------")
    print("Characters :", len(text))
    print("Letters    :", letters)
    print("Digits     :", digits)
    print("Spaces     :", spaces)


while True:

    print("\n========== MORSE CODE TRANSLATOR ==========")
    print("1. English → Morse")
    print("2. Morse → English")
    print("3. View History")
    print("4. Save History")
    print("5. Clear Screen")
    print("6. Exit")

    choice = input("\nEnter choice: ")

    if choice == "1":
        text = input("\nEnter English text:\n> ")

        translated = english_to_morse(text)

        print("\nMorse Code:")
        print(translated)

        statistics(text)

        history.append({
            "type": "English → Morse",
            "input": text,
            "output": translated
        })

    elif choice == "2":
        code = input("\nEnter Morse Code:\n> ")

        translated = morse_to_english(code)

        print("\nEnglish:")
        print(translated)

        statistics(translated)

        history.append({
            "type": "Morse → English",
            "input": code,
            "output": translated
        })

    elif choice == "3":
        show_history()

    elif choice == "4":
        save_history()

    elif choice == "5":
        clear()

    elif choice == "6":
        print("\nThank you for using Morse Translator!")
        break

    else:
        print("\nInvalid choice. Try again.")