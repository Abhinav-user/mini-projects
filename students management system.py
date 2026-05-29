students = []

def add_student():
    name = input("Enter student name: ")
    marks = int(input("Enter marks: "))

    student = {
        "name": name,
        "marks": marks
    }

    students.append(student)
    print("Student added successfully")


def view_students():
    if len(students) == 0:
        print("No students found")

    else:
        for s in students:
            print("Name:", s["name"])
            print("Marks:", s["marks"])
            print("-----------------")


def search_student():
    search = input("Enter student name: ")

    for s in students:
        if s["name"] == search:
            print("Student Found")
            print(s)
            return

    print("Student not found")


while True:
    print("\n1.Add Student")
    print("2.View Students")
    print("3.Search Student")
    print("4.Exit")

    choice = input("Enter choice: ")

    if choice == "1":
        add_student()

    elif choice == "2":
        view_students()

    elif choice == "3":
        search_student()

    elif choice == "4":
        break

    else:
        print("Invalid choice")