import shutil
import hashlib
import time
from pathlib import Path


def file_hash(file_path):
    """Generate SHA-256 hash of a file."""
    hasher = hashlib.sha256()

    try:
        with open(file_path, "rb") as file:
            while chunk := file.read(8192):
                hasher.update(chunk)
        return hasher.hexdigest()
    except Exception:
        return None


def sync(source, destination, mirror=False):
    source = Path(source)
    destination = Path(destination)

    destination.mkdir(parents=True, exist_ok=True)

    copied = 0
    updated = 0
    skipped = 0
    deleted = 0

    source_files = set()

    print("\nSynchronizing...\n")

    for item in source.rglob("*"):

        relative = item.relative_to(source)
        target = destination / relative

        if item.is_dir():
            target.mkdir(parents=True, exist_ok=True)
            continue

        source_files.add(str(relative))

        if not target.exists():
            shutil.copy2(item, target)
            copied += 1
            print(f"[COPIED]  {relative}")

        else:
            # Compare size first (faster)
            if item.stat().st_size != target.stat().st_size:
                shutil.copy2(item, target)
                updated += 1
                print(f"[UPDATED] {relative}")

            else:
                if file_hash(item) != file_hash(target):
                    shutil.copy2(item, target)
                    updated += 1
                    print(f"[UPDATED] {relative}")
                else:
                    skipped += 1
                    print(f"[SKIPPED] {relative}")

    if mirror:
        print("\nChecking extra files...")

        for item in destination.rglob("*"):
            if item.is_file():
                relative = str(item.relative_to(destination))

                if relative not in source_files:
                    item.unlink()
                    deleted += 1
                    print(f"[DELETED] {relative}")

    print("\n" + "=" * 45)
    print("        SYNCHRONIZATION SUMMARY")
    print("=" * 45)
    print(f"Files Copied  : {copied}")
    print(f"Files Updated : {updated}")
    print(f"Files Skipped : {skipped}")
    if mirror:
        print(f"Files Deleted : {deleted}")
    print("=" * 45)


def main():
    print("=" * 50)
    print("         ADVANCED FILE SYNCHRONIZER")
    print("=" * 50)

    source = input("Enter Source Folder      : ").strip()
    destination = input("Enter Destination Folder : ").strip()

    if not Path(source).exists():
        print("\nSource folder does not exist.")
        return

    mirror = input("Enable Mirror Mode (Y/N): ").strip().lower() == "y"

    start = time.time()

    try:
        sync(source, destination, mirror)
    except PermissionError:
        print("\nPermission denied while accessing files.")
    except Exception as error:
        print(f"\nUnexpected Error: {error}")

    end = time.time()

    print(f"\nCompleted in {end - start:.2f} seconds.")


if __name__ == "__main__":
    main()