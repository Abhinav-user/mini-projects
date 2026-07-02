balance = 0
transactions = []

while True:
    print("\n====== MINI BANK ACCOUNT ======")
    print("1. Deposit")
    print("2. Withdraw")
    print("3. Check Balance")
    print("4. Transaction History")
    print("5. Exit")

    choice = input("Enter your choice (1-5): ")

    if choice == "1":
        amount = float(input("Enter amount to deposit: "))

        if amount > 0:
            balance += amount
            transactions.append(f"Deposited ₹{amount:.2f}")
            print("Deposit Successful!")
        else:
            print("Invalid amount.")

    elif choice == "2":
        amount = float(input("Enter amount to withdraw: "))

        if amount <= 0:
            print("Invalid amount.")
        elif amount > balance:
            print("Insufficient Balance!")
        else:
            balance -= amount
            transactions.append(f"Withdrew ₹{amount:.2f}")
            print("Withdrawal Successful!")

    elif choice == "3":
        print(f"Current Balance: ₹{balance:.2f}")

    elif choice == "4":
        print("\n----- Transaction History -----")

        if len(transactions) == 0:
            print("No transactions yet.")
        else:
            for i in range(len(transactions)):
                print(f"{i + 1}. {transactions[i]}")

    elif choice == "5":
        print("\nThank you for using Mini Bank Account!")
        break

    else:
        print("Invalid choice. Please try again.")