students = []


def calculate_grade(marks):
    if marks >= 90:
        return "A+"
    elif marks >= 80:
        return "A"
    elif marks >= 70:
        return "B"
    elif marks >= 60:
        return "C"
    elif marks >= 50:
        return "D"
    else:
        return "F"


def add_student():
    name = input("Enter student name: ").strip().title()

    for student in students:
        if student["name"] == name:
            print("Student already exists!")
            return

    while True:
        try:
            marks = int(input("Enter marks (0-100): "))
            if 0 <= marks <= 100:
                break
            else:
                print("Marks must be between 0 and 100.")
        except ValueError:
            print("Please enter a valid number.")

    students.append({
        "name": name,
        "marks": marks,
        "grade": calculate_grade(marks)
    })

    print("Student added successfully!")


def view_students():
    if not students:
        print("\nNo students available.")
        return

    print("\n" + "=" * 50)
    print(f"{'Name':<20}{'Marks':<10}{'Grade'}")
    print("=" * 50)

    for student in students:
        print(f"{student['name']:<20}{student['marks']:<10}{student['grade']}")

    print("=" * 50)


def search_student():
    name = input("Enter student name: ").strip().title()

    for student in students:
        if student["name"] == name:
            print("\nStudent Found")
            print("-" * 30)
            print("Name :", student["name"])
            print("Marks:", student["marks"])
            print("Grade:", student["grade"])
            return

    print("Student not found.")


def update_marks():
    name = input("Enter student name: ").strip().title()

    for student in students:
        if student["name"] == name:
            while True:
                try:
                    new_marks = int(input("Enter new marks: "))
                    if 0 <= new_marks <= 100:
                        student["marks"] = new_marks
                        student["grade"] = calculate_grade(new_marks)
                        print("Marks updated successfully!")
                        return
                    else:
                        print("Marks must be between 0 and 100.")
                except ValueError:
                    print("Enter a valid number.")

    print("Student not found.")


def delete_student():
    name = input("Enter student name: ").strip().title()

    for student in students:
        if student["name"] == name:
            students.remove(student)
            print("Student deleted successfully!")
            return

    print("Student not found.")


def topper():
    if not students:
        print("No students available.")
        return

    top = students[0]

    for student in students:
        if student["marks"] > top["marks"]:
            top = student

    print("\nTopper Details")
    print("-" * 30)
    print("Name :", top["name"])
    print("Marks:", top["marks"])
    print("Grade:", top["grade"])


def average_marks():
    if not students:
        print("No students available.")
        return

    total = 0

    for student in students:
        total += student["marks"]

    average = total / len(students)

    print(f"\nAverage Marks : {average:.2f}")


def statistics():
    if not students:
        print("No students available.")
        return

    highest = students[0]["marks"]
    lowest = students[0]["marks"]

    for student in students:
        if student["marks"] > highest:
            highest = student["marks"]

        if student["marks"] < lowest:
            lowest = student["marks"]

    print("\nClass Statistics")
    print("-" * 30)
    print("Total Students :", len(students))
    print("Highest Marks  :", highest)
    print("Lowest Marks   :", lowest)
    average_marks()


while True:

    print("\n" + "=" * 40)
    print("      STUDENT MANAGEMENT SYSTEM")
    print("=" * 40)
    print("1. Add Student")
    print("2. View Students")
    print("3. Search Student")
    print("4. Update Marks")
    print("5. Delete Student")
    print("6. Find Topper")
    print("7. Average Marks")
    print("8. Class Statistics")
    print("9. Exit")

    choice = input("Enter your choice: ")

    if choice == "1":
        add_student()

    elif choice == "2":
        view_students()

    elif choice == "3":
        search_student()

    elif choice == "4":
        update_marks()

    elif choice == "5":
        delete_student()

    elif choice == "6":
        topper()

    elif choice == "7":
        average_marks()

    elif choice == "8":
        statistics()

    elif choice == "9":
        print("Thank you for using the Student Management System!")
        break

    else:
        print("Invalid choice! Please try again.")