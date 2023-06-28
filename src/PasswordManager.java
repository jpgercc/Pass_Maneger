import java.security.SecureRandom;
import java.sql.*;
import java.util.Scanner;

public class PasswordManager {
    private static final String DB_URL = "jdbc:postgresql://localhost/password_manager";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "92491043";

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTable(connection);
            menu(connection);
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection connection) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS passwords (" +
                "id SERIAL PRIMARY KEY, " +
                "service VARCHAR(255) NOT NULL, " +
                "username VARCHAR(255) NOT NULL, " +
                "password VARCHAR(255) NOT NULL)";
        Statement statement = connection.createStatement();
        statement.executeUpdate(createTableQuery);
        statement.close();
    }

    private static void menu(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("1. Add Password");
            System.out.println("2. Generate Strong Password");
            System.out.println("3. Retrieve Password");
            System.out.println("4. Delete Password");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    addPassword(connection, scanner);
                    break;
                case 2:
                    generateStrongPassword(scanner);
                    break;
                case 3:
                    retrievePassword(connection, scanner);
                    break;
                case 4:
                    deletePassword(connection, scanner);
                    break;
                case 5:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 5);
        scanner.close();
    }

    private static void addPassword(Connection connection, Scanner scanner) {
        System.out.print("Enter the service name: ");
        String service = scanner.nextLine();
        System.out.print("Enter the username: ");
        String username = scanner.nextLine();
        System.out.print("Enter the password: ");
        String password = scanner.nextLine();
        try {
            String insertQuery = "INSERT INTO passwords (service, username, password) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, service);
            statement.setString(2, username);
            statement.setString(3, password);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password saved successfully.");
            } else {
                System.out.println("Failed to save the password.");
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void generateStrongPassword(Scanner scanner) {
        System.out.print("Enter the length of the password: ");
        int length = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        String password = generateRandomPassword(length);
        System.out.println("Generated Password: " + password);
    }

    private static String generateRandomPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+~`|}{[]:;?><,./-=";
        String allChars = upper + lower + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Include at least one uppercase, lowercase, digit, and special character
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Generate the remaining password characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the generated password
        for (int i = 0; i < password.length(); i++) {
            int j = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }

    private static void retrievePassword(Connection connection, Scanner scanner) {
        System.out.print("Enter the service name: ");
        String service = scanner.nextLine();
        try {
            String selectQuery = "SELECT * FROM passwords WHERE service = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, service);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Service: " + resultSet.getString("service"));
                System.out.println("Username: " + resultSet.getString("username"));
                System.out.println("Password: " + resultSet.getString("password"));
            } else {
                System.out.println("No password found for the specified service.");
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deletePassword(Connection connection, Scanner scanner) {
        System.out.print("Enter the service name: ");
        String service = scanner.nextLine();
        try {
            String deleteQuery = "DELETE FROM passwords WHERE service = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, service);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password deleted successfully.");
            } else {
                System.out.println("No password found for the specified service.");
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
