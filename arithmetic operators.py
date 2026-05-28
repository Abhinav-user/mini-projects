print(" ARITHMETIC OPERATORS IN PYTHON ")

# Taking input from user

num1 = float(input("Enter first number: "))
num2 = float(input("Enter second number: "))

# Addition

addition = num1 + num2

print("\nAddition (+)")
print(num1, "+", num2, "=", addition)

# Subtraction

subtraction = num1 - num2

print("\nSubtraction (-)")
print(num1, "-", num2, "=", subtraction)

# Multiplication

multiplication = num1 * num2

print("\nMultiplication (*)")
print(num1, "*", num2, "=", multiplication)

# Division

if num2 != 0:

    division = num1 / num2

    print("\nDivision (/)")
    print(num1, "/", num2, "=", division)

else:
    print("\nDivision not possible (cannot divide by zero)")

# Floor Division

if num2 != 0:

    floor_division = num1 // num2

    print("\nFloor Division (//)")
    print(num1, "//", num2, "=", floor_division)

else:
    print("\nFloor division not possible")

# Modulus

if num2 != 0:

    modulus = num1 % num2

    print("\nModulus (%)")
    print(num1, "%", num2, "=", modulus)

else:
    print("\nModulus not possible")

# Exponent

power = num1 ** num2

print("\nExponent (**)")
print(num1, "**", num2, "=", power)

print("\n====== PROGRAM COMPLETED ======")