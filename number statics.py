numbers = []

for i in range(5):
    num = int(input("Enter a number: "))
    numbers.append(num)

print("Numbers:", numbers)
print("Total:", sum(numbers))
print("Average:", sum(numbers) / len(numbers))
print("Highest:", max(numbers))
print("Lowest:", min(numbers))