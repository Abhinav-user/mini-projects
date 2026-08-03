from PIL import Image
import os


def image_info(path):
    """Display image information."""
    img = Image.open(path)
    size_kb = os.path.getsize(path) / 1024

    print("\n----- IMAGE INFORMATION -----")
    print(f"File Name   : {os.path.basename(path)}")
    print(f"Format      : {img.format}")
    print(f"Resolution  : {img.width} x {img.height}")
    print(f"Color Mode  : {img.mode}")
    print(f"File Size   : {size_kb:.2f} KB")


def resize_compress(path):
    img = Image.open(path)

    while True:
        try:
            width = int(input("\nEnter new width (pixels): "))
            if width > 0:
                break
            print("Width must be greater than 0.")
        except ValueError:
            print("Enter a valid number.")

    # Maintain aspect ratio
    ratio = width / img.width
    height = int(img.height * ratio)

    resized = img.resize((width, height), Image.LANCZOS)

    while True:
        try:
            quality = int(input("Compression Quality (1-100): "))
            if 1 <= quality <= 100:
                break
            print("Quality must be between 1 and 100.")
        except ValueError:
            print("Enter a valid number.")

    os.makedirs("output", exist_ok=True)

    name = os.path.splitext(os.path.basename(path))[0]
    output = os.path.join("output", f"{name}_compressed.jpg")

    resized = resized.convert("RGB")
    resized.save(output, "JPEG", quality=quality, optimize=True)

    old_size = os.path.getsize(path) / 1024
    new_size = os.path.getsize(output) / 1024
    reduction = ((old_size - new_size) / old_size) * 100

    print("\n========== SUCCESS ==========")
    print(f"Saved As       : {output}")
    print(f"New Resolution : {width} x {height}")
    print(f"Original Size  : {old_size:.2f} KB")
    print(f"Compressed     : {new_size:.2f} KB")
    print(f"Reduction      : {reduction:.2f}%")
    print("=============================")


def main():
    print("=" * 40)
    print(" SMART IMAGE RESIZER & COMPRESSOR ")
    print("=" * 40)

    path = input("Enter image path: ").strip()

    if not os.path.isfile(path):
        print("Error: File not found!")
        return

    try:
        image_info(path)
        resize_compress(path)
    except Exception as e:
        print("Error:", e)


if __name__ == "__main__":
    main()