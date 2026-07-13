from datetime import datetime

# ==============================
#        MINI BANK SYSTEM
# ==============================

print("=" * 55)
print("        WELCOME TO PYTHON MINI BANK")
print("=" * 55)

account_name = input("Enter Account Holder Name: ").title()

while True:
    try:
        pin = input("Create a 4-digit PIN: ")
        if len(pin) == 4 and pin.isdigit():
            break
        else:
            print("PIN must be exactly 4 digits.")
    except:
        print("Invalid input.")

balance = 0.0
transactions = []
total_deposit = 0
total_withdraw = 0

# ------------------------------
# Login
# ------------------------------
print("\nLogin Required")

attempts = 3

while attempts > 0:
    entered = input("Enter PIN: ")

    if entered == pin:
        print(f"\nWelcome, {account_name}!")
        break
    else:
        attempts -= 1
        print(f"Incorrect PIN. Attempts left: {attempts}")

if attempts == 0:
    print("Too many incorrect attempts.")
    exit()

# ------------------------------
# Functions
# ------------------------------

def add_transaction(text):
    time = datetime.now().strftime("%d-%m-%Y %I:%M:%S %p")
    transactions.append(f"{time}  |  {text}")


def get_amount(message):
    while True:
        try:
            amount = float(input(message))
            if amount <= 0:
                print("Amount must be greater than zero.")
            else:
                return amount
        except:
            print("Please enter a valid number.")


# ------------------------------
# Main Menu
# ------------------------------

while True:

    print("\n" + "=" * 55)
    print("               MINI BANK MENU")
    print("=" * 55)

    print("1. Deposit Money")
    print("2. Withdraw Money")
    print("3. Check Balance")
    print("4. Transaction History")
    print("5. Mini Statement")
    print("6. Transfer Money")
    print("7. Interest Calculator")
    print("8. Account Summary")
    print("9. Change PIN")
    print("10. Exit")

    choice = input("\nEnter your choice: ")

    # Deposit
    if choice == "1":

        amount = get_amount("Enter amount to deposit: ₹")

        balance += amount
        total_deposit += amount

        add_transaction(f"Deposited ₹{amount:.2f}")

        print("Deposit Successful.")

    # Withdraw
    elif choice == "2":

        amount = get_amount("Enter amount to withdraw: ₹")

        if amount > balance:
            print("Insufficient Balance.")

        else:
            balance -= amount
            total_withdraw += amount

            add_transaction(f"Withdrawn ₹{amount:.2f}")

            print("Withdrawal Successful.")

    # Balance
    elif choice == "3":

        print("\nCurrent Balance")
        print("-" * 30)
        print(f"₹{balance:.2f}")

    # History
    elif choice == "4":

        print("\nTransaction History")
        print("-" * 55)

        if len(transactions) == 0:
            print("No transactions available.")

        else:
            for i, t in enumerate(transactions, 1):
                print(f"{i}. {t}")

    # Mini Statement
    elif choice == "5":

        print("\nLast 5 Transactions")
        print("-" * 55)

        if len(transactions) == 0:
            print("No transactions available.")

        else:
            recent = transactions[-5:]

            for i, t in enumerate(recent, 1):
                print(f"{i}. {t}")

    # Transfer
    elif choice == "6":

        receiver = input("Enter Receiver Name: ").title()

        amount = get_amount("Enter transfer amount: ₹")

        if amount > balance:
            print("Insufficient Balance.")

        else:
            balance -= amount
            total_withdraw += amount

            add_transaction(f"Transferred ₹{amount:.2f} to {receiver}")

            print("Transfer Successful.")

    # Interest
    elif choice == "7":

        try:
            rate = float(input("Annual Interest Rate (%): "))
            years = float(input("Number of Years: "))

            interest = (balance * rate * years) / 100

            print(f"\nEstimated Interest : ₹{interest:.2f}")
            print(f"Future Balance     : ₹{balance + interest:.2f}")

        except:
            print("Invalid input.")

    # Summary
    elif choice == "8":

        print("\nACCOUNT SUMMARY")
        print("-" * 40)

        print(f"Account Holder      : {account_name}")
        print(f"Current Balance     : ₹{balance:.2f}")
        print(f"Total Deposited     : ₹{total_deposit:.2f}")
        print(f"Total Withdrawn     : ₹{total_withdraw:.2f}")
        print(f"Transactions Made   : {len(transactions)}")

    # Change PIN
    elif choice == "9":

        old = input("Enter Current PIN: ")

        if old == pin:

            while True:

                new = input("Enter New 4-digit PIN: ")

                if len(new) == 4 and new.isdigit():
                    pin = new
                    print("PIN Changed Successfully.")
                    break
                else:
                    print("PIN must contain exactly 4 digits.")

        else:
            print("Incorrect Current PIN.")

    # Exit
    elif choice == "10":

        print("\nGenerating Final Summary...")
        print("-" * 40)

        print(f"Account Holder : {account_name}")
        print(f"Final Balance  : ₹{balance:.2f}")
        print(f"Deposited      : ₹{total_deposit:.2f}")
        print(f"Withdrawn      : ₹{total_withdraw:.2f}")
        print(f"Transactions   : {len(transactions)}")

        print("\nThank you for using Python Mini Bank.")
        break

    else:
        print("Invalid Choice. Please try again.")