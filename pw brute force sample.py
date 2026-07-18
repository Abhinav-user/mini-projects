import itertools
import string
import time

print("====== Brute Force Password Simulator ======")
print("For educational purposes only.\n")

password = input("Enter a password (lowercase letters only, max 4 characters): ")

if not password.isalpha() or not password.islower():
    print("Password must contain only lowercase letters.")
    exit()

if len(password) > 4:
    print("Password must be 4 characters or fewer.")
    exit()

characters = string.ascii_lowercase

attempts = 0
start = time.time()

found = False

for length in range(1, len(password) + 1):
    for guess in itertools.product(characters, repeat=length):
        attempts += 1
        guess = "".join(guess)

        if guess == password:
            end = time.time()

            print("\nPassword Found!")
            print("---------------------------")
            print("Password :", guess)
            print("Attempts :", attempts)
            print("Time Taken: {:.4f} seconds".format(end - start))

            found = True
            break

    if found:
        break