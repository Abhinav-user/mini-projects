import tkinter as tk
from math import *

root = tk.Tk()
root.title("Scientific Calculator")
root.geometry("420x600")
root.resizable(False, False)

expression = ""


def press(value):
    global expression
    expression += str(value)
    equation.set(expression)


def clear():
    global expression
    expression = ""
    equation.set("")


def calculate():
    global expression
    try:
        result = str(eval(expression))
        equation.set(result)
        expression = result
    except:
        equation.set("Error")
        expression = ""


equation = tk.StringVar()

display = tk.Entry(
    root,
    textvariable=equation,
    font=("Arial", 20),
    justify="right",
    bd=10
)

display.grid(row=0, column=0, columnspan=5, ipadx=8, ipady=20)

buttons = [

    ('7', 1, 0), ('8', 1, 1), ('9', 1, 2), ('/', 1, 3), ('sqrt(', 1, 4),

    ('4', 2, 0), ('5', 2, 1), ('6', 2, 2), ('*', 2, 3), ('**', 2, 4),

    ('1', 3, 0), ('2', 3, 1), ('3', 3, 2), ('-', 3, 3), ('(', 3, 4),

    ('0', 4, 0), ('.', 4, 1), ('=', 4, 2), ('+', 4, 3), (')', 4, 4),

    ('sin(', 5, 0), ('cos(', 5, 1), ('tan(', 5, 2), ('log10(', 5, 3), ('log(', 5, 4),

    ('factorial(', 6, 0), ('pi', 6, 1), ('e', 6, 2), ('%', 6, 3), ('C', 6, 4)

]

for (text, row, col) in buttons:

    if text == "=":
        btn = tk.Button(
            root,
            text=text,
            width=8,
            height=3,
            command=calculate
        )

    elif text == "C":
        btn = tk.Button(
            root,
            text=text,
            width=8,
            height=3,
            command=clear
        )

    else:
        btn = tk.Button(
            root,
            text=text,
            width=8,
            height=3,
            command=lambda t=text: press(t)
        )

    btn.grid(row=row, column=col, padx=2, pady=2)

root.mainloop()