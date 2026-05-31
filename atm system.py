balance = 5000
pin = "1234"

entered_pin = input("Enter ATM PIN: ")

if entered_pin != pin:
    print("Wrong PIN")

else:
    while True:

        print("\n1.Check Balance")
        print("2.Deposit")
        print("3.Withdraw")
        print("4.Exit")

        choice = input("Choose option: ")

        if choice == "1":
            print("Balance:", balance)

        elif choice == "2":
            amount = int(input("Enter amount: "))
            balance += amount
            print("Amount deposited")

        elif choice == "3":
            amount = int(input("Enter amount: "))

            if amount > balance:
                print("Insufficient balance")

            else:
                balance -= amount
                print("Withdrawal successful")

        elif choice == "4":
            print("Thank you")
            break

        else:
            print("Invalid option")