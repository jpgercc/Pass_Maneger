package util;

import db.DatabaseManager;

import javax.swing.*;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Objects;

public class PasswordManager {
    private static final String MENU_PASSWORD = "92491043";

    public static void addPassword(String service, String username, String password) {
        try (var connection = DatabaseManager.getConnection()) {
            String insertQuery = "INSERT INTO passwords (service, username, password) VALUES (?, ?, ?)";
            var statement = connection.prepareStatement(insertQuery);
            statement.setString(1, service);
            statement.setString(2, username);
            statement.setString(3, password);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Password saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to save the password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String generateRandomPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()_+~`|}{[]:;?><,./-=";
        String allChars = upper + lower + digits + specialChars;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        for (int i = 0; i < password.length(); i++) {
            int j = random.nextInt(password.length());
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }

    public static void retrievePassword(String service) {
        try (var connection = DatabaseManager.getConnection()) {
            String selectQuery = "SELECT * FROM passwords WHERE service = ?";
            var statement = connection.prepareStatement(selectQuery);
            statement.setString(1, service);
            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String message = "Service: " + service + "\nUsername: " + username + "\nPassword: " + password;
                JOptionPane.showMessageDialog(null, message, "Retrieve Password", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No password found for the specified service.", "Retrieve Password", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void deletePassword(String service) {
        try (var connection = DatabaseManager.getConnection()) {
            String deleteQuery = "DELETE FROM passwords WHERE service = ?";
            var statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, service);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Password deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No password found for the specified service.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static boolean isMenuPassword(String password) {
        return Objects.equals(password, MENU_PASSWORD);
    }
}
