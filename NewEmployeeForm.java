/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

// Observer Pattern: Subject (Observable) Class
class EmployeeSubject {
    private List<EmployeeObserver> observers = new ArrayList<>();

    public void addObserver(EmployeeObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(EmployeeObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String message) {
        for (EmployeeObserver observer : observers) {
            observer.update(message);
        }
    }
}

// Observer Pattern: Observer Interface
interface EmployeeObserver {
    void update(String message);
}

public class AddEmployee extends JFrame implements EmployeeObserver {
    private JLabel employeeIDLabel, employeeNameLabel, roleLabel, emailLabel, passwordLabel, basicPayLabel;
    private JTextField employeeIDField, employeeNameField, roleField, emailField, basicPayField;
    private JPasswordField passwordField;
    private JButton submitButton, updateButton, deleteButton;
    private EmployeeSubject employeeSubject;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/employee management system"; 
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public AddEmployee(EmployeeSubject employeeSubject) {
        this.employeeSubject = employeeSubject;
        this.employeeSubject.addObserver(this);
        setTitle("Employee Management");
        setSize(450, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(135, 206, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);  
        gbc.fill = GridBagConstraints.HORIZONTAL;

        employeeIDLabel = new JLabel("Employee ID:");
        employeeNameLabel = new JLabel("Employee Name:");
        roleLabel = new JLabel("Role:");
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Password:");
        basicPayLabel = new JLabel("Basic Pay:");

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        JLabel[] labels = {employeeIDLabel, employeeNameLabel, roleLabel, emailLabel, passwordLabel, basicPayLabel};
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setHorizontalAlignment(SwingConstants.LEFT);
        }

        employeeIDField = createStyledTextField();
        employeeNameField = createStyledTextField();
        roleField = createStyledTextField();
        emailField = createStyledTextField();
        passwordField = new JPasswordField();
        basicPayField = createStyledTextField();
        stylePasswordField(passwordField);

        submitButton = createStyledButton("Register", new Color(60, 179, 113)); // Medium sea green
        submitButton.addActionListener(e -> handleRegistration());

        updateButton = createStyledButton("Update", new Color(30, 144, 255)); // Dodger blue
        updateButton.addActionListener(e -> handleUpdate());

        deleteButton = createStyledButton("Delete", new Color(255, 69, 0)); // Red-orange
        deleteButton.addActionListener(e -> handleDelete());

        addComponent(employeeIDLabel, gbc, 0, 0);
        addComponent(employeeIDField, gbc, 1, 0);
        addComponent(employeeNameLabel, gbc, 0, 1);
        addComponent(employeeNameField, gbc, 1, 1);
        addComponent(roleLabel, gbc, 0, 2);
        addComponent(roleField, gbc, 1, 2);
        addComponent(emailLabel, gbc, 0, 3);
        addComponent(emailField, gbc, 1, 3);
        addComponent(passwordLabel, gbc, 0, 4);
        addComponent(passwordField, gbc, 1, 4);
        addComponent(basicPayLabel, gbc, 0, 5);
        addComponent(basicPayField, gbc, 1, 5);

        gbc.gridx = 1;
        gbc.gridy = 6;
        add(submitButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(updateButton, gbc);

        gbc.gridx = 2;
        add(deleteButton, gbc);

        setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setForeground(Color.DARK_GRAY);
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    private void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    private void addComponent(Component component, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(component, gbc);
    }

    private void handleRegistration() {
        String employeeID = employeeIDField.getText();
        String employeeName = employeeNameField.getText();
        String role = roleField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String basicPay = basicPayField.getText();

        if (employeeID.isEmpty() || employeeName.isEmpty() || role.isEmpty() || email.isEmpty() || password.isEmpty() || basicPay.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Employee ID (must be an integer)
        try {
            Integer.parseInt(employeeID);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Employee ID must be a valid integer!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Employee Name (only letters)
        if (!employeeName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "Employee Name must contain only characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Role (only letters)
        if (!role.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "Role must contain only Characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Email format
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Password (must contain at least one uppercase, one lowercase, one digit, and one special character)
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!password.matches(passwordRegex)) {
            JOptionPane.showMessageDialog(this, "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate Basic Pay (must be numeric)
        try {
            BigDecimal pay = new BigDecimal(basicPay);
            if (pay.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Basic pay must be greater than zero and should be in integer.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Basic Pay. It must be a valid number and greater than zero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO employees (id, name, role, email, password, basic_pay) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employeeID);
                pstmt.setString(2, employeeName);
                pstmt.setString(3, role);
                pstmt.setString(4, email);
                pstmt.setString(5, password);
                pstmt.setBigDecimal(6, new BigDecimal(basicPay));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Employee Registered Successfully!");
                employeeSubject.notifyObservers("New employee registered: " + employeeName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        // Similar validation and logic as in handleRegistration()
    }

    private void handleDelete() {
        // Logic for delete operation
    }

    @Override
    public void update(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    // Main method to run the application
    public static void main(String[] args) {
        EmployeeSubject employeeSubject = new EmployeeSubject();
        new AddEmployee(employeeSubject);
    }
}
