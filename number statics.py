import math
import random
from statistics import median

numbers = []

print("=" * 50)
print("        ADVANCED NUMBER STATISTICS PROGRAM")
print("=" * 50)

# Get valid input
while len(numbers) < 5:
    try:
        num = float(input(f"Enter number {len(numbers) + 1}: "))
        numbers.append(num)
    except ValueError:
        print("Invalid input! Please enter a valid number.")

# Calculations
total = sum(numbers)
average = total / len(numbers)
highest = max(numbers)
lowest = min(numbers)
range_value = highest - lowest
ascending = sorted(numbers)
descending = sorted(numbers, reverse=True)

# Product
product = 1
for num in numbers:
    product *= num

# Positive, Negative, Zero
positive = len([n for n in numbers if n > 0])
negative = len([n for n in numbers if n < 0])
zeros = numbers.count(0)

# Even & Odd (whole numbers only)
even = [int(n) for n in numbers if n.is_integer() and int(n) % 2 == 0]
odd = [int(n) for n in numbers if n.is_integer() and int(n) % 2 != 0]

# Above & Below Average
above_avg = len([n for n in numbers if n > average])
below_avg = len([n for n in numbers if n < average])

# Squares, Cubes & Square Roots
squares = [round(n ** 2, 2) for n in numbers]
cubes = [round(n ** 3, 2) for n in numbers]
roots = [round(math.sqrt(n), 2) if n >= 0 else "N/A" for n in numbers]

# Unique Values
unique_numbers = sorted(set(numbers))

# Frequency
frequency = {}
for num in numbers:
    frequency[num] = frequency.get(num, 0) + 1

# Second Highest & Lowest
if len(unique_numbers) >= 2:
    second_lowest = unique_numbers[1]
    second_highest = unique_numbers[-2]
else:
    second_lowest = "N/A"
    second_highest = "N/A"

# Variance & Standard Deviation
variance = sum((n - average) ** 2 for n in numbers) / len(numbers)
std_dev = math.sqrt(variance)

# Display Results
print("\n" + "=" * 50)
print("                RESULTS")
print("=" * 50)

print(f"Numbers Entered      : {numbers}")
print(f"Total                : {total:.2f}")
print(f"Average              : {average:.2f}")
print(f"Median               : {median(numbers):.2f}")
print(f"Highest              : {highest}")
print(f"Lowest               : {lowest}")
print(f"Second Highest       : {second_highest}")
print(f"Second Lowest        : {second_lowest}")
print(f"Range                : {range_value}")
print(f"Product              : {product}")

print("\nSorting")
print(f"Ascending Order      : {ascending}")
print(f"Descending Order     : {descending}")

print("\nClassification")
print(f"Positive Numbers     : {positive}")
print(f"Negative Numbers     : {negative}")
print(f"Zero(s)              : {zeros}")
print(f"Even Numbers         : {even}")
print(f"Odd Numbers          : {odd}")
print(f"Above Average        : {above_avg}")
print(f"Below Average        : {below_avg}")

print("\nMathematical Operations")
print(f"Squares              : {squares}")
print(f"Cubes                : {cubes}")
print(f"Square Roots         : {roots}")

print("\nStatistics")
print(f"Variance             : {variance:.2f}")
print(f"Standard Deviation   : {std_dev:.2f}")

print("\nUnique Numbers")
print(unique_numbers)

print("\nFrequency Table")
for key, value in sorted(frequency.items()):
    print(f"{key} -> {value}")

print(f"\nRandom Number        : {random.choice(numbers)}")

# Search Feature
search = float(input("\nEnter a number to search: "))
if search in numbers:
    print("Result: Number Found!")
else:
    print("Result: Number Not Found!")

# Save Results
choice = input("\nDo you want to save the results to a file? (y/n): ").lower()

if choice == "y":
    with open("number_statistics.txt", "w") as file:
        file.write("ADVANCED NUMBER STATISTICS REPORT\n")
        file.write("=" * 40 + "\n")
        file.write(f"Numbers: {numbers}\n")
        file.write(f"Total: {total:.2f}\n")
        file.write(f"Average: {average:.2f}\n")
        file.write(f"Median: {median(numbers):.2f}\n")
        file.write(f"Highest: {highest}\n")
        file.write(f"Lowest: {lowest}\n")
        file.write(f"Second Highest: {second_highest}\n")
        file.write(f"Second Lowest: {second_lowest}\n")
        file.write(f"Range: {range_value}\n")
        file.write(f"Product: {product}\n")
        file.write(f"Ascending: {ascending}\n")
        file.write(f"Descending: {descending}\n")
        file.write(f"Positive: {positive}\n")
        file.write(f"Negative: {negative}\n")
        file.write(f"Zeros: {zeros}\n")
        file.write(f"Even Numbers: {even}\n")
        file.write(f"Odd Numbers: {odd}\n")
        file.write(f"Variance: {variance:.2f}\n")
        file.write(f"Standard Deviation: {std_dev:.2f}\n")
        file.write("\nFrequency:\n")
        for key, value in sorted(frequency.items()):
            file.write(f"{key} -> {value}\n")

    print("Results saved successfully as 'number_statistics.txt'.")

print("\nThank you for using the Number Statistics Program!")