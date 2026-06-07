import os

folder = input("Enter folder path: ")

files = {}

for root, dirs, filenames in os.walk(folder):
    for filename in filenames:
        filepath = os.path.join(root, filename)

        try:
            with open(filepath, "rb") as file:
                content = file.read()

            if content in files:
                files[content].append(filepath)
            else:
                files[content] = [filepath]

        except Exception as e:
            print(f"Could not read {filepath}: {e}")

print("\nDuplicate Files:")

found = False

for paths in files.values():
    if len(paths) > 1:
        found = True
        print("\nDuplicates:")
        for path in paths:
            print(path)

if not found:
    print("No duplicate files found.")