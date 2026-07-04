balance = 5000
pin = "1234"
transactions = []

# PIN Verification
attempts = 3

while attempts > 0:
    entered_pin = input("Enter ATM PIN: ")

    if entered_pin == pin:
        print("\nLogin Successful!")
        break
    else:
        attempts -= 1
        print("Wrong PIN")
        print("Attempts left:", attempts)

if attempts == 0:
    print("Card Blocked!")
    exit()

deposit_count = 0
withdraw_count = 0

while True:

    print("\n========== ATM MENU ==========")
    print("1. Check Balance")
    print("2. Deposit")
    print("3. Withdraw")
    print("4. Transaction History")
    print("5. Account Summary")
    print("6. Exit")

    choice = input("Choose option: ")

    if choice == "1":
        print(f"\nCurrent Balance: ₹{balance}")

    elif choice == "2":
        amount = int(input("Enter deposit amount: ₹"))

        if amount <= 0:
            print("Invalid amount!")
        else:
            balance += amount
            deposit_count += 1
            transactions.append(f"Deposited ₹{amount}")
            print(f"₹{amount} deposited successfully.")
            print("New Balance:", balance)

    elif choice == "3":
        amount = int(input("Enter withdrawal amount: ₹"))

        if amount <= 0:
            print("Invalid amount!")

        elif amount > balance:
            print("Insufficient Balance!")

        else:
            balance -= amount
            withdraw_count += 1
            transactions.append(f"Withdrawn ₹{amount}")
            print(f"₹{amount} withdrawn successfully.")
            print("Remaining Balance:", balance)

    elif choice == "4":
        print("\n------ Transaction History ------")

        if len(transactions) == 0:
            print("No transactions yet.")
        else:
            for i, t in enumerate(transactions, start=1):
                print(f"{i}. {t}")

    elif choice == "5":
        print("\n------ Account Summary ------")
        print("Current Balance :", balance)
        print("Deposits        :", deposit_count)
        print("Withdrawals     :", withdraw_count)
        print("Total Transactions:", len(transactions))

    elif choice == "6":
        print("\nThank you for using our ATM.")
        print("Final Balance: ₹", balance)
        break

    else:
        print("Invalid option! Please choose 1-6.")