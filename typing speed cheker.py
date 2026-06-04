import random
import time

TEXT_FILE = "texts.txt"
SCORE_FILE = "scores.txt"


def get_random_text():
    with open(TEXT_FILE, "r") as file:
        texts = file.readlines()
    return random.choice(texts).strip()


def calculate_accuracy(original, typed):
    correct = 0

    for o, t in zip(original, typed):
        if o == t:
            correct += 1

    return (correct / len(original)) * 100


def save_score(wpm):
    try:
        with open(SCORE_FILE, "a") as file:
            file.write(f"{wpm}\n")
    except:
        pass


def get_best_score():
    try:
        with open(SCORE_FILE, "r") as file:
            scores = [float(line.strip()) for line in file]

        return max(scores) if scores else 0

    except FileNotFoundError:
        return 0


text = get_random_text()

print("\nType the following:\n")
print(text)

input("\nPress Enter to start...")

start = time.time()

typed = input("\n> ")

end = time.time()

elapsed = end - start

words = len(typed.split())

wpm = (words / elapsed) * 60

accuracy = calculate_accuracy(text, typed)

save_score(round(wpm, 2))

print("\n----- RESULTS -----")
print(f"Time: {elapsed:.2f} sec")
print(f"WPM: {wpm:.2f}")
print(f"Accuracy: {accuracy:.2f}%")
print(f"Best WPM: {get_best_score()}")