# Armstrong Number Checker

number = int(input("Enter a number: "))

original = number
digits = len(str(number))
total = 0

while number > 0:
    digit = number % 10
    total += digit ** digits
    number //= 10

# Handle the special case of 0
if original == 0:
    total = 0

print("\nOriginal Number :", original)
print("Armstrong Sum   :", total)

if original == total:
    print("Result          : Armstrong Number")
else:
    print("Result          : Not an Armstrong Number")