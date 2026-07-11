import os
import hashlib
from datetime import datetime

# ==========================================
#        ADVANCED DUPLICATE FILE FINDER
# ==========================================

duplicate_files = {}

# ---------- Functions ----------

def banner():
    print("=" * 65)
    print("          ADVANCED DUPLICATE FILE FINDER")
    print("=" * 65)


def get_file_hash(filepath):
    """Return SHA-256 hash of a file."""
    sha = hashlib.sha256()

    try:
        with open(filepath, "rb") as file:
            while True:
                chunk = file.read(4096)
                if not chunk:
                    break
                sha.update(chunk)

        return sha.hexdigest()

    except Exception as e:
        print(f"Cannot read {filepath}")
        return None


def format_size(size):
    """Convert bytes into readable format."""
    for unit in ["Bytes", "KB", "MB", "GB", "TB"]:
        if size < 1024:
            return f"{size:.2f} {unit}"
        size /= 1024
    return f"{size:.2f} PB"


def scan_folder(folder):

    duplicate_files.clear()

    total_files = 0

    print("\nScanning folder...")
    print("-" * 65)

    for root, dirs, files in os.walk(folder):

        for filename in files:

            filepath = os.path.join(root, filename)
            total_files += 1

            file_hash = get_file_hash(filepath)

            if file_hash:

                duplicate_files.setdefault(file_hash, []).append(filepath)

                print(f"Scanned: {filename}")

    print("-" * 65)
    print(f"Total Files Scanned : {total_files}")


def show_duplicates():

    found = False
    duplicate_groups = 0
    duplicate_count = 0
    wasted_space = 0

    print("\n" + "=" * 65)
    print("Duplicate File Report")
    print("=" * 65)

    for paths in duplicate_files.values():

        if len(paths) > 1:

            duplicate_groups += 1
            duplicate_count += len(paths) - 1

            size = os.path.getsize(paths[0])
            wasted_space += size * (len(paths) - 1)

            print(f"\nDuplicate Group #{duplicate_groups}")
            print("-" * 65)

            for i, path in enumerate(paths, start=1):
                print(f"{i}. {path}")

            print(f"Size : {format_size(size)}")

            found = True

    if not found:
        print("No duplicate files found.")

    else:
        print("\n" + "=" * 65)
        print("SUMMARY")
        print("=" * 65)
        print(f"Duplicate Groups : {duplicate_groups}")
        print(f"Duplicate Files  : {duplicate_count}")
        print(f"Space Wasted     : {format_size(wasted_space)}")


def delete_duplicates():

    deleted = 0

    print("\nDelete duplicate files")
    print("(First file in each group will be kept.)")

    confirm = input("Are you sure? (yes/no): ").lower()

    if confirm != "yes":
        print("Deletion cancelled.")
        return

    for paths in duplicate_files.values():

        if len(paths) > 1:

            for file in paths[1:]:

                try:
                    os.remove(file)
                    print("Deleted:", file)
                    deleted += 1

                except Exception as e:
                    print("Cannot delete:", file)

    print(f"\nDeleted {deleted} duplicate files.")


def save_report():

    report_name = "duplicate_report.txt"

    with open(report_name, "w", encoding="utf-8") as report:

        report.write("ADVANCED DUPLICATE FILE REPORT\n")
        report.write("=" * 60 + "\n")
        report.write(f"Generated : {datetime.now()}\n\n")

        group = 1

        for paths in duplicate_files.values():

            if len(paths) > 1:

                report.write(f"Duplicate Group {group}\n")

                for path in paths:
                    report.write(path + "\n")

                report.write("\n")
                group += 1

    print(f"\nReport saved as '{report_name}'")


# ---------- Main Program ----------

banner()

folder = input("Enter folder path: ").strip()

if not os.path.exists(folder):
    print("Folder does not exist.")
    exit()

scan_folder(folder)

while True:

    print("\n" + "=" * 65)
    print("MENU")
    print("=" * 65)
    print("1. Show Duplicate Files")
    print("2. Save Report")
    print("3. Delete Duplicate Files")
    print("4. Scan Again")
    print("5. Exit")

    choice = input("Enter your choice: ")

    if choice == "1":
        show_duplicates()

    elif choice == "2":
        save_report()

    elif choice == "3":
        delete_duplicates()

    elif choice == "4":
        scan_folder(folder)

    elif choice == "5":
        print("\nThank you for using Advanced Duplicate File Finder.")
        break

    else:
        print("Invalid choice. Try again.")