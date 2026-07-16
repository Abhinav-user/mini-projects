import tkinter as tk
from tkinter import messagebox, filedialog
import qrcode
import barcode
from barcode.writer import ImageWriter
from PIL import Image, ImageTk
import os


class QRBarcodeGenerator:

    def __init__(self, root):
        self.root = root
        self.root.title("QR & Barcode Generator")
        self.root.geometry("500x600")
        self.root.resizable(False, False)

        title = tk.Label(root,
                         text="QR & Barcode Generator",
                         font=("Arial", 18, "bold"))
        title.pack(pady=10)

        tk.Label(root, text="Enter Text").pack()

        self.text_entry = tk.Entry(root, width=50)
        self.text_entry.pack(pady=5)

        tk.Label(root, text="Choose Type").pack()

        self.option = tk.StringVar(value="QR")

        tk.Radiobutton(root,
                       text="QR Code",
                       variable=self.option,
                       value="QR").pack()

        tk.Radiobutton(root,
                       text="Barcode",
                       variable=self.option,
                       value="BARCODE").pack()

        tk.Button(root,
                  text="Generate",
                  command=self.generate,
                  width=20,
                  bg="green",
                  fg="white").pack(pady=10)

        self.image_label = tk.Label(root)
        self.image_label.pack(pady=10)

        tk.Button(root,
                  text="Save Image",
                  command=self.save_image,
                  width=20).pack()

        self.generated_image = None

    def generate(self):

        text = self.text_entry.get().strip()

        if not text:
            messagebox.showerror("Error", "Please enter some text.")
            return

        if self.option.get() == "QR":
            self.generate_qr(text)
        else:
            self.generate_barcode(text)

    def generate_qr(self, text):

        qr = qrcode.QRCode(
            version=1,
            box_size=10,
            border=4
        )

        qr.add_data(text)
        qr.make(fit=True)

        img = qr.make_image(fill_color="black",
                            back_color="white")

        self.generated_image = img

        img = img.resize((250, 250))

        photo = ImageTk.PhotoImage(img)

        self.image_label.configure(image=photo)
        self.image_label.image = photo

    def generate_barcode(self, text):

        try:
            code128 = barcode.get('code128', text,
                                  writer=ImageWriter())

            filename = "temp_barcode"

            saved = code128.save(filename)

            img = Image.open(saved)

            self.generated_image = img

            img = img.resize((350, 120))

            photo = ImageTk.PhotoImage(img)

            self.image_label.configure(image=photo)
            self.image_label.image = photo

            os.remove(saved)

        except Exception as e:
            messagebox.showerror("Error", str(e))

    def save_image(self):

        if self.generated_image is None:
            messagebox.showerror("Error", "Generate an image first.")
            return

        filepath = filedialog.asksaveasfilename(
            defaultextension=".png",
            filetypes=[("PNG Image", "*.png")]
        )

        if filepath:
            self.generated_image.save(filepath)
            messagebox.showinfo("Success",
                                "Image saved successfully!")


root = tk.Tk()

app = QRBarcodeGenerator(root)

root.mainloop()