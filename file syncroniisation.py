import os
import shutil
import hashlib


def file_hash(path):
    """Return SHA256 hash of a file."""
    h = hashlib.sha256()
    with open(path, "rb") as f:
        while chunk := f.read(8192):
            h.update(chunk)
    return h.hexdigest()


def sync(source, destination, mirror=False):
    # Create destination if it doesn't exist
    os.makedirs(destination, exist_ok=True)

    src_files = {}

    # Traverse source folder
    for root, dirs, files in os.walk(source):
        rel_path = os.path.relpath(root, source)
        dest_root = os.path.join(destination, rel_path)

        os.makedirs(dest_root, exist_ok=True)

        for file in files:
            src_path = os.path.join(root, file)
            dest_path = os.path.join(dest_root, file)

            rel_file = os.path.relpath(src_path, source)
            src_files[rel_file] = True

            # Copy if missing
            if not os.path.exists(dest_path):
                shutil.copy2(src_path, dest_path)
                print(f"[COPIED] {rel_file}")

            # Update if changed
            elif file_hash(src_path) != file_hash(dest_path):
                shutil.copy2(src_path, dest_path)
                print(f"[UPDATED] {rel_file}")

    # Mirror mode: delete files not in source
    if mirror:
        for root, dirs, files in os.walk(destination):
            for file in files:
                dest_path = os.path.join(root, file)
                rel_file = os.path.relpath(dest_path, destination)

                if rel_file not in src_files:
                    os.remove(dest_path)
                    print(f"[DELETED] {rel_file}")

    print("\nSynchronization Complete!")


def main():
    print("=" * 40)
    print("      FILE SYNCHRONIZER")
    print("=" * 40)

    source = input("Source Folder: ").strip()
    destination = input("Destination Folder: ").strip()

    if not os.path.isdir(source):
        print("Source folder does not exist.")
        return

    mirror = input("Mirror Mode? (y/n): ").lower() == "y"

    sync(source, destination, mirror)


if __name__ == "__main__":
    main()