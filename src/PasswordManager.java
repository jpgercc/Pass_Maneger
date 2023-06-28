import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Objects;

public class PasswordManagerGUI extends JFrame {
    private static final String DB_URL = "jdbc:postgresql://localhost/password_manager";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "92491043";
    private static final String MENU_PASSWORD = "92491043";

    private JTextField serviceField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public PasswordManagerGUI() {
        initComponents();
        setupWindow();
    }

    private void initComponents() {
        serviceField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton addButton = new JButton("Add Password");
        addButton.addActionListener(e -> {
            String service = serviceField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (service.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                addPassword(service, username, password);
            }

            // Clear input fields
            serviceField.setText("");
            usernameField.setText("");
            passwordField.setText("");
        });

        JButton generateButton = new JButton("Generate Strong Password");
        generateButton.addActionListener(e -> {
            String lengthString = JOptionPane.showInputDialog(this, "Enter the length of the password:", "Generate Password", JOptionPane.PLAIN_MESSAGE);
            try {
                int length = Integer.parseInt(lengthString);
                String generatedPassword = generateRandomPassword(length);
                JOptionPane.showMessageDialog(this, "Generated Password: " + generatedPassword, "Generated Password", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid length. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton retrieveButton = new JButton("Retrieve Password");
        retrieveButton.addActionListener(e -> {
            String service = JOptionPane.showInputDialog(this, "Enter the service name:", "Retrieve Password", JOptionPane.PLAIN_MESSAGE);
            if (service != null) {
                retrievePassword(service);
            }
        });

        JButton deleteButton = new JButton("Delete Password");
        deleteButton.addActionListener(e -> {
            String service = JOptionPane.showInputDialog(this, "Enter the service name:", "Delete Password", JOptionPane.PLAIN_MESSAGE);
            if (service != null) {
                deletePassword(service);
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        mainPanel.add(new JLabel("Service:"), constraints);
        constraints.gridy = 1;
        mainPanel.add(new JLabel("Username:"), constraints);
        constraints.gridy = 2;
        mainPanel.add(new JLabel("Password:"), constraints);

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(serviceField, constraints);
        constraints.gridy = 1;
        mainPanel.add(usernameField, constraints);
        constraints.gridy = 2;
        mainPanel.add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        mainPanel.add(addButton, constraints);

        constraints.gridy = 4;
        mainPanel.add(generateButton, constraints);

        constraints.gridy = 5;
        mainPanel.add(retrieveButton, constraints);

        constraints.gridy = 6;
        mainPanel.add(deleteButton, constraints);

        constraints.gridy = 7;
        mainPanel.add(exitButton, constraints);

        add(mainPanel);
    }

    private void setupWindow() {
        setTitle("Password Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        pack();
    }

    private void addPassword(String service, String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertQuery = "INSERT INTO passwords (service, username, password) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(insertQuery);
            statement.setString(1, service);
            statement.setString(2, username);
            statement.setString(3, password);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save the password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateRandomPassword(int length) {
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

    private void retrievePassword(String service) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM passwords WHERE service = ?";
            PreparedStatement statement = connection.prepareStatement(selectQuery);
            statement.setString(1, service);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String message = "Service: " + service + "\nUsername: " + username + "\nPassword: " + password;
                JOptionPane.showMessageDialog(this, message, "Retrieve Password", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No password found for the specified service.", "Retrieve Password", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePassword(String service) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String deleteQuery = "DELETE FROM passwords WHERE service = ?";
            PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, service);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No password found for the specified service.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            createTable(connection);

            String passwordInput = JOptionPane.showInputDialog(null, "Enter password:", "Password Manager", JOptionPane.PLAIN_MESSAGE);
            if (Objects.equals(passwordInput, MENU_PASSWORD)) {
                SwingUtilities.invokeLater(() -> new PasswordManagerGUI().setVisible(true));
            } else {
                JOptionPane.showMessageDialog(null, "Incorrect password. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
