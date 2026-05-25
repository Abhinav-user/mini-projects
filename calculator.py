num1 = float(input("First number: "))
operator = input("Choose +, -, *, / : ")
num2 = float(input("Second number: "))

if operator == "+":
    print(num1 + num2)

elif operator == "-":
    print(num1 - num2)

elif operator == "*":
    print(num1 * num2)

elif operator == "/":
    print(num1 / num2)

else:
    print("Invalid operator")