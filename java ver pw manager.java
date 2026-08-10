import java.util.ArrayList;
import java.util.Scanner;

class Account {
    private String website;
    private String username;
    private String password;

    public Account(String website, String username, String password) {
        this.website = website;
        this.username = username;
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void display() {
        System.out.println("Website : " + website);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("----------------------");
    }
}

public class PasswordManager {

    static ArrayList<Account> accounts = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    static void addAccount() {
        System.out.print("Website: ");
        String website = sc.nextLine();

        System.out.print("Username: ");
        String username = sc.nextLine();

        System.out.print("Password: ");
        String password = sc.nextLine();

        accounts.add(new Account(website, username, password));

        System.out.println("Account added successfully!");
    }

    static void viewAccounts() {
        if (accounts.isEmpty()) {
            System.out.println("No accounts saved.");
            return;
        }

        for (Account account : accounts) {
            account.display();
        }
    }

    static void searchAccount() {
        System.out.print("Enter website: ");
        String website = sc.nextLine();

        boolean found = false;

        for (Account account : accounts) {
            if (account.getWebsite().equalsIgnoreCase(website)) {
                account.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("Account not found.");
        }
    }

    static void deleteAccount() {
        System.out.print("Enter website to delete: ");
        String website = sc.nextLine();

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getWebsite().equalsIgnoreCase(website)) {
                accounts.remove(i);
                System.out.println("Account deleted.");
                return;
            }
        }

        System.out.println("Account not found.");
    }

    public static void main(String[] args) {

        while (true) {
            System.out.println("\n===== PASSWORD MANAGER =====");
            System.out.println("1. Add Account");
            System.out.println("2. View Accounts");
            System.out.println("3. Search Account");
            System.out.println("4. Delete Account");
            System.out.println("5. Exit");

            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        addAccount();
                        break;

                    case 2:
                        viewAccounts();
                        break;

                    case 3:
                        searchAccount();
                        break;

                    case 4:
                        deleteAccount();
                        break;

                    case 5:
                        System.out.println("Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}