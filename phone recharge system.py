users = {
    "9876543210": {
        "name": "Abhinav",
        "balance": 250,
        "history": [100, 199]
    },
    "9123456789": {
        "name": "Rahul",
        "balance": 500,
        "history": [299]
    }
}

while True:
    print("\n===== PHONE RECHARGE SYSTEM =====")
    print("1. Add User")
    print("2. Recharge")
    print("3. Check Balance")
    print("4. Recharge History")
    print("5. Search User")
    print("6. Display All Users")
    print("7. Delete User")
    print("8. Update Name")
    print("9. Total Recharge Amount")
    print("10. Highest Balance User")
    print("11. Statistics")
    print("12. Exit")

    choice = input("Enter choice: ")

    if choice == "1":
        phone = input("Phone Number: ")
        if phone in users:
            print("User already exists!")
        elif len(phone) != 10 or not phone.isdigit():
            print("Invalid phone number!")
        else:
            name = input("Name: ")
            users[phone] = {
                "name": name,
                "balance": 0,
                "history": []
            }
            print("User added successfully.")

    elif choice == "2":
        phone = input("Phone Number: ")
        if phone in users:
            amount = int(input("Recharge Amount: "))
            if amount > 0:
                users[phone]["balance"] += amount
                users[phone]["history"].append(amount)
                print("Recharge successful.")
            else:
                print("Invalid amount.")
        else:
            print("User not found.")

    elif choice == "3":
        phone = input("Phone Number: ")
        if phone in users:
            print("Name:", users[phone]["name"])
            print("Balance: ₹", users[phone]["balance"])
        else:
            print("User not found.")

    elif choice == "4":
        phone = input("Phone Number: ")
        if phone in users:
            history = users[phone]["history"]
            if history:
                print("Recharge History:")
                for i, amount in enumerate(history, 1):
                    print(f"{i}. ₹{amount}")
            else:
                print("No recharge history.")
        else:
            print("User not found.")

    elif choice == "5":
        phone = input("Phone Number: ")
        if phone in users:
            print("Name:", users[phone]["name"])
            print("Balance: ₹", users[phone]["balance"])
        else:
            print("User not found.")

    elif choice == "6":
        if users:
            for phone, data in users.items():
                print("\nPhone:", phone)
                print("Name:", data["name"])
                print("Balance: ₹", data["balance"])
        else:
            print("No users found.")

    elif choice == "7":
        phone = input("Phone Number: ")
        if phone in users:
            del users[phone]
            print("User deleted.")
        else:
            print("User not found.")

    elif choice == "8":
        phone = input("Phone Number: ")
        if phone in users:
            new_name = input("New Name: ")
            users[phone]["name"] = new_name
            print("Name updated.")
        else:
            print("User not found.")

    elif choice == "9":
        phone = input("Phone Number: ")
        if phone in users:
            total = sum(users[phone]["history"])
            print("Total Recharged: ₹", total)
        else:
            print("User not found.")

    elif choice == "10":
        if users:
            highest = max(users, key=lambda x: users[x]["balance"])
            print("Highest Balance User:", users[highest]["name"])
            print("Balance: ₹", users[highest]["balance"])
        else:
            print("No users available.")

    elif choice == "11":
        if users:
            total_users = len(users)
            total_recharge = sum(
                sum(data["history"]) for data in users.values()
            )
            balances = [data["balance"] for data in users.values()]

            print("Total Users:", total_users)
            print("Total Recharged: ₹", total_recharge)
            print("Average Balance: ₹", sum(balances) / total_users)
            print("Highest Balance: ₹", max(balances))
            print("Lowest Balance: ₹", min(balances))
        else:
            print("No users available.")

    elif choice == "12":
        print("Thank you for using Phone Recharge System.")
        break

    else:
        print("Invalid choice.")