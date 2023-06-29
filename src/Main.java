import util.PasswordManager;
import gui.PasswordManagerGUI;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        String passwordInput = JOptionPane.showInputDialog(null, "Enter password:", "Password Manager", JOptionPane.PLAIN_MESSAGE);
        if (PasswordManager.isMenuPassword(passwordInput)) {
            SwingUtilities.invokeLater(() -> {
                new PasswordManagerGUI().setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect password. Access denied.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
