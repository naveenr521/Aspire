package bank;

import java.sql.*;
import java.util.Scanner;

public class BankService {

    private Scanner sc = new Scanner(System.in);

    public void createAccount() throws SQLException {
        String name = "";
        String password = "";

        while (name.isBlank()) {
            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();
            if (name.isBlank()) System.out.println("❌ Name cannot be empty.");
        }

        while (password.isBlank()) {
            System.out.print("Set Password: ");
            password = sc.nextLine().trim();
            if (password.isBlank()) System.out.println("❌ Password cannot be empty.");
        }

        System.out.print("Initial Deposit: ");
        double balance = sc.nextDouble();
        sc.nextLine();

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "INSERT INTO users (name, password, balance) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            pst.setString(1, name);
            pst.setString(2, password);
            pst.setDouble(3, balance);

            if (pst.executeUpdate() > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int accountNo = rs.getInt(1);
                    System.out.println("✅ Account Created Successfully! Your Account Number is: " + accountNo);
                }
            } else {
                System.out.println("❌ Account creation failed.");
            }
        }
    }

    public boolean login(int accountNo, String password) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM users WHERE account_no = ? AND password = ?"
            );
            pst.setInt(1, accountNo);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Login Successful!");
                return true;
            } else {
                System.out.println("❌ Invalid Account Number or Password.");
                return false;
            }
        }
    }

    public void checkBalance(int accountNo) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT balance FROM users WHERE account_no = ?");
            pst.setInt(1, accountNo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                System.out.println("💰 Balance: ₹" + rs.getDouble("balance"));
            } else {
                System.out.println("❌ Account not found.");
            }
        }
    }

    public void transferFunds(int fromAcc) throws SQLException {
        System.out.print("Enter Receiver Account No: ");
        int toAcc = sc.nextInt();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine(); // consume newline

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement checkReceiver = con.prepareStatement("SELECT * FROM users WHERE account_no = ?");
            checkReceiver.setInt(1, toAcc);
            ResultSet rsReceiver = checkReceiver.executeQuery();

            if (!rsReceiver.next()) {
                System.out.println("❌ Receiver not found.");
                return;
            }

            PreparedStatement ps1 = con.prepareStatement(
                    "UPDATE users SET balance = balance - ? WHERE account_no = ? AND balance >= ?"
            );
            ps1.setDouble(1, amount);
            ps1.setInt(2, fromAcc);
            ps1.setDouble(3, amount);
            if (ps1.executeUpdate() == 0) {
                System.out.println("❌ Insufficient balance.");
                return;
            }

            PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE users SET balance = balance + ? WHERE account_no = ?"
            );
            ps2.setDouble(1, amount);
            ps2.setInt(2, toAcc);
            ps2.executeUpdate();

            PreparedStatement ps3 = con.prepareStatement(
                    "INSERT INTO transactions (account_no, type, amount, related_account_no) VALUES (?, 'debit', ?, ?)"
            );
            ps3.setInt(1, fromAcc);
            ps3.setDouble(2, amount);
            ps3.setInt(3, toAcc);
            ps3.executeUpdate();

            PreparedStatement ps4 = con.prepareStatement(
                    "INSERT INTO transactions (account_no, type, amount, related_account_no) VALUES (?, 'credit', ?, ?)"
            );
            ps4.setInt(1, toAcc);
            ps4.setDouble(2, amount);
            ps4.setInt(3, fromAcc);
            ps4.executeUpdate();

            System.out.println("✅ Transfer Successful.");
        }
    }

    public void transactionHistory(int accountNo) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement(
                    "SELECT * FROM transactions WHERE account_no = ? ORDER BY timestamp DESC"
            );
            pst.setInt(1, accountNo);
            ResultSet rs = pst.executeQuery();
            System.out.println("=== Transaction History ===");
            while (rs.next()) {
                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                Timestamp time = rs.getTimestamp("timestamp");
                int relatedAcc = rs.getInt("related_account_no");

                System.out.printf("%s ₹%.2f with A/C No: %d on %s%n",
                        type, amount, relatedAcc, time);
            }
        }
    }
}
