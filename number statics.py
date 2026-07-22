numbers = []

print("=== Number Statistics Program ===")

# Get 5 valid numbers from the user
while len(numbers) < 5:
    try:
        num = float(input(f"Enter number {len(numbers) + 1}: "))
        numbers.append(num)
    except ValueError:
        print(" Invalid input! Please enter a valid number.")

# Display results
total = sum(numbers)
average = total / len(numbers)
highest = max(numbers)
lowest = min(numbers)

print("\n===== Results =====")
print("Numbers Entered :", numbers)
print(f"Total           : {total}")
print(f"Average         : {average:.2f}")
print(f"Highest         : {highest}")
print(f"Lowest          : {lowest}")
print(f"Range           : {highest - lowest}")
print(f"Ascending Order : {sorted(numbers)}")
print(f"Descending Order: {sorted(numbers, reverse=True)}")