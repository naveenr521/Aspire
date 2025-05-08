package bank;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService service = new BankService();
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Online Banking Menu ===");
            System.out.println("1. Create Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    try {
                        service.createAccount();
                    } catch (SQLException e) {
                        System.out.println("❌ Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.print("Enter Account No: ");
                    int accNo = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Password: ");
                    String pass = sc.nextLine();
                    try {
                        if (service.login(accNo, pass)) {
                            boolean loggedIn = true;
                            while (loggedIn) {
                                System.out.println("\n--- Banking Options ---");
                                System.out.println("1. Check Balance");
                                System.out.println("2. Transfer Funds");
                                System.out.println("3. Transaction History");
                                System.out.println("4. Logout");
                                System.out.print("Choose: ");
                                int opt = sc.nextInt();
                                sc.nextLine();

                                switch (opt) {
                                    case 1:
                                        service.checkBalance(accNo);
                                        break;
                                    case 2:
                                        service.transferFunds(accNo);
                                        break;
                                    case 3:
                                        service.transactionHistory(accNo);
                                        break;
                                    case 4:
                                        loggedIn = false;
                                        System.out.println("🔒 Logged out.");
                                        break;
                                    default:
                                        System.out.println("❌ Invalid option.");
                                }
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("❌ Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    exit = true;
                    System.out.println("👋 Exiting...");
                    break;

                default:
                    System.out.println("❌ Invalid choice.");
            }
        }

        sc.close();
    }
}
