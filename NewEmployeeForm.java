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
import java.sql.*;

public class NewEmployeeForm extends JFrame {

    private JLabel employeeIDLabel, employeeNameLabel, roleLabel, emailLabel, passwordLabel;
    private JTextField employeeIDField, employeeNameField, roleField, emailField;
    private JPasswordField passwordField;
    private JButton submitButton, updateButton, deleteButton;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/employee management system"; // Updated DB name format
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = ""; // Replace with your actual MySQL password

    public NewEmployeeForm() {
        setTitle("Employee Management");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);

        // Sky blue background color
        getContentPane().setBackground(new Color(135, 206, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        employeeIDLabel = new JLabel("Employee ID:");
        employeeNameLabel = new JLabel("Employee Name:");
        roleLabel = new JLabel("Role:");
        emailLabel = new JLabel("Email:");
        passwordLabel = new JLabel("Password:");

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        JLabel[] labels = {employeeIDLabel, employeeNameLabel, roleLabel, emailLabel, passwordLabel};
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setHorizontalAlignment(SwingConstants.LEFT);
        }

        employeeIDField = createStyledTextField();
        employeeNameField = createStyledTextField();
        roleField = createStyledTextField();
        emailField = createStyledTextField();
        passwordField = new JPasswordField();
        stylePasswordField(passwordField);

        submitButton = createStyledButton("Register", new Color(60, 179, 113)); // Medium sea green
        submitButton.addActionListener(e -> handleRegistration());

        updateButton = createStyledButton("Update", new Color(30, 144, 255)); // Dodger blue
        updateButton.addActionListener(e -> handleUpdate());

        deleteButton = createStyledButton("Delete", new Color(255, 69, 0)); // Red-orange
        deleteButton.addActionListener(e -> handleDelete());

        // Adding components to the form
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

        // Add Register button
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(submitButton, gbc);

        // Add Update and Delete buttons below Register
        gbc.gridx = 0;
        gbc.gridy = 6;
        add(updateButton, gbc);

        gbc.gridx = 2;
        add(deleteButton, gbc);

        setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
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
        button.setPreferredSize(new Dimension(120, 30)); // Same size for all buttons
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

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO employees (id, name, role, email, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employeeID);
                pstmt.setString(2, employeeName);
                pstmt.setString(3, role);
                pstmt.setString(4, email);
                pstmt.setString(5, password);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Employee Registered Successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error registering employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        String employeeID = employeeIDField.getText();
        String employeeName = employeeNameField.getText();
        String role = roleField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "UPDATE employees SET name = ?, role = ?, email = ?, password = ? WHERE employee_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employeeName);
                pstmt.setString(2, role);
                pstmt.setString(3, email);
                pstmt.setString(4, password);
                pstmt.setString(5, employeeID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Employee details updated successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Employee ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String employeeID = employeeIDField.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "DELETE FROM employees WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, employeeID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Employee ID not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new NewEmployeeForm().setVisible(true));
    }
}
