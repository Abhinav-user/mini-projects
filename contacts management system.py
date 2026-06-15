contacts = {}


def add_contact():
    name = input("Enter Name: ").strip()

    if not name:
        print("Name cannot be empty!")
        return

    if name in contacts:
        print("Contact already exists!")
        return

    phone = input("Enter Phone Number: ").strip()

    if not phone.isdigit():
        print("Invalid phone number!")
        return

    contacts[name] = phone
    print("Contact added successfully!")


def search_contact():
    name = input("Enter Name to Search: ").strip()

    if name in contacts:
        print(f"{name}: {contacts[name]}")
    else:
        print("Contact not found!")


def update_contact():
    name = input("Enter Name to Update: ").strip()

    if name not in contacts:
        print("Contact not found!")
        return

    new_phone = input("Enter New Phone Number: ").strip()

    if not new_phone.isdigit():
        print("Invalid phone number!")
        return

    contacts[name] = new_phone
    print("Contact updated successfully!")


def delete_contact():
    name = input("Enter Name to Delete: ").strip()

    if name in contacts:
        del contacts[name]
        print("Contact deleted successfully!")
    else:
        print("Contact not found!")


def view_contacts():
    if not contacts:
        print("No contacts available!")
        return

    print("\n--- Contact List ---")
    for name, phone in contacts.items():
        print(f"{name}: {phone}")


while True:
    print("\n===== Contact Management System =====")
    print("1. Add Contact")
    print("2. Search Contact")
    print("3. Update Contact")
    print("4. Delete Contact")
    print("5. View All Contacts")
    print("6. Exit")

    choice = input("Enter your choice (1-6): ")

    if choice == "1":
        add_contact()
    elif choice == "2":
        search_contact()
    elif choice == "3":
        update_contact()
    elif choice == "4":
        delete_contact()
    elif choice == "5":
        view_contacts()
    elif choice == "6":
        print("Exiting program...")
        break
    else:
        print("Invalid choice! Please enter a number between 1 and 6.")