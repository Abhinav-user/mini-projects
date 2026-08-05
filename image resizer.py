from PIL import Image
import os

SUPPORTED = (".jpg", ".jpeg", ".png", ".bmp", ".webp")


def line():
    print("=" * 55)


def image_info(path):
    img = Image.open(path)
    size = os.path.getsize(path) / 1024

    print("\nIMAGE INFORMATION")
    line()
    print(f"Name       : {os.path.basename(path)}")
    print(f"Format     : {img.format}")
    print(f"Resolution : {img.width} x {img.height}")
    print(f"Mode       : {img.mode}")
    print(f"Size       : {size:.2f} KB")
    line()


def get_positive_number(msg):
    while True:
        try:
            n = int(input(msg))
            if n > 0:
                return n
            print("Enter a value greater than 0.")
        except ValueError:
            print("Invalid input.")


def get_quality():
    while True:
        try:
            q = int(input("Compression Quality (1-100): "))
            if 1 <= q <= 100:
                return q
            print("Enter between 1 and 100.")
        except ValueError:
            print("Invalid input.")


def resize_image(path):
    img = Image.open(path)

    print("\n1. Resize by Width")
    print("2. Resize by Percentage")

    choice = input("Choose option: ")

    if choice == "1":
        width = get_positive_number("New Width: ")
        ratio = width / img.width
        height = int(img.height * ratio)

    elif choice == "2":
        percent = get_positive_number("Resize Percentage: ")
        width = int(img.width * percent / 100)
        height = int(img.height * percent / 100)

    else:
        print("Invalid choice.")
        return

    resized = img.resize((width, height), Image.LANCZOS)

    print("\nSave Format")
    print("1. Keep Original")
    print("2. Convert to JPEG")

    fmt = input("Choice: ")

    quality = get_quality()

    os.makedirs("output", exist_ok=True)

    filename = os.path.splitext(os.path.basename(path))[0]

    if fmt == "2":
        output = f"output/{filename}_compressed.jpg"
        resized.convert("RGB").save(
            output,
            "JPEG",
            quality=quality,
            optimize=True
        )
    else:
        ext = os.path.splitext(path)[1]
        output = f"output/{filename}_compressed{ext}"

        if ext.lower() in [".jpg", ".jpeg"]:
            resized.save(
                output,
                quality=quality,
                optimize=True
            )
        else:
            resized.save(output)

    old_size = os.path.getsize(path) / 1024
    new_size = os.path.getsize(output) / 1024

    reduction = ((old_size - new_size) / old_size) * 100

    print("\nSUCCESS")
    line()
    print(f"Saved To        : {output}")
    print(f"New Resolution  : {width} x {height}")
    print(f"Original Size   : {old_size:.2f} KB")
    print(f"New Size        : {new_size:.2f} KB")

    if reduction >= 0:
        print(f"Reduction       : {reduction:.2f}%")
    else:
        print(f"Size Increased  : {abs(reduction):.2f}%")

    line()


def main():
    line()
    print(" SMART IMAGE RESIZER & COMPRESSOR ")
    line()

    path = input("Enter image path: ").strip('"')

    if not os.path.exists(path):
        print("File not found.")
        return

    if not path.lower().endswith(SUPPORTED):
        print("Unsupported image format.")
        return

    try:
        image_info(path)

        while True:
            print("\nMENU")
            print("1. Resize & Compress")
            print("2. View Image Info")
            print("3. Exit")

            choice = input("Enter choice: ")

            if choice == "1":
                resize_image(path)

            elif choice == "2":
                image_info(path)

            elif choice == "3":
                print("Thank you for using the program.")
                break

            else:
                print("Invalid choice.")

    except Exception as e:
        print("Error:", e)


if __name__ == "__main__":
    main()