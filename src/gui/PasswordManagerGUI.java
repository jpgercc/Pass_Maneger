package gui;

import db.DatabaseManager;
import util.PasswordManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.security.SecureRandom;

public class PasswordManagerGUI extends JFrame {
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
                PasswordManager.addPassword(service, username, password);
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
                String generatedPassword = PasswordManager.generateRandomPassword(length);
                passwordField.setText(generatedPassword);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid length. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton copyButton = new JButton("Copy Password");
        copyButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if (!password.isEmpty()) {
                StringSelection selection = new StringSelection(password);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(this, "Password copied to clipboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No password generated.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton retrieveButton = new JButton("Retrieve Password");
        retrieveButton.addActionListener(e -> {
            String service = JOptionPane.showInputDialog(this, "Enter the service name:", "Retrieve Password", JOptionPane.PLAIN_MESSAGE);
            if (service != null) {
                PasswordManager.retrievePassword(service);
            }
        });

        JButton deleteButton = new JButton("Delete Password");
        deleteButton.addActionListener(e -> {
            String service = JOptionPane.showInputDialog(this, "Enter the service name:", "Delete Password", JOptionPane.PLAIN_MESSAGE);
            if (service != null) {
                PasswordManager.deletePassword(service);
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
        mainPanel.add(copyButton, constraints);

        constraints.gridy = 6;
        mainPanel.add(retrieveButton, constraints);

        constraints.gridy = 7;
        mainPanel.add(deleteButton, constraints);

        constraints.gridy = 8;
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

}
