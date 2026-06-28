number = int(input("Enter a number: "))

original = number
reverse = 0

while number > 0:
    reverse = reverse * 10 + number % 10
    number //= 10

if original == reverse:
    print("Palindrome")
else:
    print("Not Palindrome")