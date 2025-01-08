import java.sql.*;
import java.util.Scanner;

public class ATM {

    private static Connection connection;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            connectToDatabase();
            System.out.println("Welcome to the ATM Application!");
            
            while (true) {
                System.out.print("Enter your account number: ");
                String accountNumber = scanner.nextLine();

                System.out.print("Enter your PIN: ");
                String pin = scanner.nextLine();

                if (authenticateUser(accountNumber, pin)) {
                    System.out.println("Authentication successful.");
                    performTransactions(accountNumber);
                } else {
                    System.out.println("Invalid account number or PIN. Try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDatabaseConnection();
        }
    }

    private static void connectToDatabase() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/atm_db";
        String user = "root";
        String password = "password";

        connection = DriverManager.getConnection(url, user, password);
    }

    private static void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean authenticateUser(String accountNumber, String pin) throws SQLException {
        String query = "SELECT * FROM users WHERE account_number = ? AND pin = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            statement.setString(2, pin);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next();
        }
    }

    private static void performTransactions(String accountNumber) throws SQLException {
        while (true) {
            System.out.println("\nSelect a transaction:");
            System.out.println("1. Balance Inquiry");
            System.out.println("2. Deposit Cash");
            System.out.println("3. Withdraw Cash");
            System.out.println("4. Fund Transfer");
            System.out.println("5. View Transaction History");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    showBalance(accountNumber);
                    break;
                case 2:
                    depositCash(accountNumber);
                    break;
                case 3:
                    withdrawCash(accountNumber);
                    break;
                case 4:
                    transferFunds(accountNumber);
                    break;
                case 5:
                    viewTransactionHistory(accountNumber);
                    break;
                case 6:
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showBalance(String accountNumber) throws SQLException {
        String query = "SELECT balance FROM users WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Your current balance is: $" + balance);
            }
        }
    }

    private static void depositCash(String accountNumber) throws SQLException {
        System.out.print("Enter amount to deposit: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String updateQuery = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
        try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setDouble(1, amount);
            statement.setString(2, accountNumber);
            statement.executeUpdate();
        }

        logTransaction(accountNumber, "Deposit", amount);
        System.out.println("Amount deposited successfully.");
    }

    private static void withdrawCash(String accountNumber) throws SQLException {
        System.out.print("Enter amount to withdraw: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        String query = "SELECT balance FROM users WHERE account_number = ?";
        try (PreparedStatement checkStatement = connection.prepareStatement(query)) {
            checkStatement.setString(1, accountNumber);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                if (balance >= amount) {
                    String updateQuery = "UPDATE users SET balance = balance - ? WHERE account_number = ?";
                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                        updateStatement.setDouble(1, amount);
                        updateStatement.setString(2, accountNumber);
                        updateStatement.executeUpdate();
                    }

                    logTransaction(accountNumber, "Withdrawal", -amount);
                    System.out.println("Amount withdrawn successfully.");
                } else {
                    System.out.println("Insufficient funds.");
                }
            }
        }
    }

    private static void transferFunds(String accountNumber) throws SQLException {
        System.out.print("Enter recipient's account number: ");
        String recipientAccount = scanner.nextLine();

        System.out.print("Enter amount to transfer: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        connection.setAutoCommit(false);

        try {
            String query = "SELECT balance FROM users WHERE account_number = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(query)) {
                checkStatement.setString(1, accountNumber);
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    double balance = resultSet.getDouble("balance");
                    if (balance >= amount) {
                        String debitQuery = "UPDATE users SET balance = balance - ? WHERE account_number = ?";
                        try (PreparedStatement debitStatement = connection.prepareStatement(debitQuery)) {
                            debitStatement.setDouble(1, amount);
                            debitStatement.setString(2, accountNumber);
                            debitStatement.executeUpdate();
                        }

                        String creditQuery = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
                        try (PreparedStatement creditStatement = connection.prepareStatement(creditQuery)) {
                            creditStatement.setDouble(1, amount);
                            creditStatement.setString(2, recipientAccount);
                            creditStatement.executeUpdate();
                        }

                        logTransaction(accountNumber, "Transfer to " + recipientAccount, -amount);
                        logTransaction(recipientAccount, "Transfer from " + accountNumber, amount);

                        connection.commit();
                        System.out.println("Funds transferred successfully.");
                    } else {
                        System.out.println("Insufficient funds.");
                    }
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            e.printStackTrace();
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private static void viewTransactionHistory(String accountNumber) throws SQLException {
        String query = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("\nTransaction History:");
            while (resultSet.next()) {
                String type = resultSet.getString("transaction_type");
                double amount = resultSet.getDouble("amount");
                Timestamp date = resultSet.getTimestamp("transaction_date");
                System.out.println(date + " - " + type + " - $" + amount);
            }
        }
    }

    private static void logTransaction(String accountNumber, String type, double amount) throws SQLException {
        String query = "INSERT INTO transactions (account_number, transaction_type, amount, transaction_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, accountNumber);
            statement.setString(2, type);
            statement.setDouble(3, amount);
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.executeUpdate();
        }
    }
}