import java.security.SecureRandom;
import java.util.*;

public class PasswordManager {
    private static final Map<String, String> passwordMap = new HashMap<>();

    public static void main(String[] args) {
        menu();
    }

    private static void menu() {
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
                    addPassword(scanner);
                    break;
                case 2:
                    generateStrongPassword(scanner);
                    break;
                case 3:
                    retrievePassword(scanner);
                    break;
                case 4:
                    deletePassword(scanner);
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

    private static void addPassword(Scanner scanner) {
        System.out.print("Enter the service name: ");
        String service = scanner.nextLine();
        System.out.print("Enter the username: ");
        String username = scanner.nextLine();
        System.out.print("Enter the password: ");
        String password = scanner.nextLine();
        passwordMap.put(service, password);
        System.out.println("Password saved successfully.");
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


    private static void retrievePassword(Scanner scanner) {
        System.out.print("Enter the service name: ");
        String service = scanner.nextLine();
        String password = passwordMap.get(service);
        if (password != null) {
            System.out.println("Service: " + service);
            System.out.println("Password: " + password);
        } else {
            System.out.println("No password found for the specified service.");
        }
    }

    private static void deletePassword(Scanner scanner) {
        System.out.print("Enter the service name: ");
        String service = scanner.nextLine();
        String password = passwordMap.remove(service);
        if (password != null) {
            System.out.println("Password deleted successfully.");
        } else {
            System.out.println("No password found for the specified service.");
        }
    }
}
