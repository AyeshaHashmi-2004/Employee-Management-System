/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hp
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class EmployeeAttendance extends JFrame implements ActionListener {

    private String dbUrl = "jdbc:mysql://localhost:3306/employee management system"; 
    private String dbUser = "root";
    private String dbPassword = "";

    private JLabel titleLabel, nameLabel, basicPayLabel, todayPaymentLabel, attendanceLabel;
    private JTextField nameField, basicPayField, todayPaymentField, advanceField, bonusField;
    private JButton submitButton;
    private JRadioButton presentButton, absentButton, halfDayButton, holidayButton;
    private ButtonGroup attendanceGroup;
    private int currentEmployeeId;

    // Factory class to create components
    private static class ComponentFactory {
        public static JLabel createStyledLabel(String text, int fontSize) {
            JLabel label = new JLabel(text);
            label.setFont(new Font("Arial", Font.BOLD, fontSize));
            return label;
        }

        public static JTextField createStyledTextField() {
            JTextField textField = new JTextField(15);
            textField.setFont(new Font("Arial", Font.PLAIN, 14));
            textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            return textField;
        }

        public static JRadioButton createStyledRadioButton(String text) {
            JRadioButton radioButton = new JRadioButton(text);
            radioButton.setBackground(new Color(135, 206, 235)); // Sky blue background to match frame
            radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
            return radioButton;
        }
    }

    public EmployeeAttendance() {
        super("Employee Attendance");

        // Prompt user for employee ID
        String input = JOptionPane.showInputDialog(this, "Enter Employee ID:");
        try {
            currentEmployeeId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID. Exiting application.");
            System.exit(0);
        }

        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(135, 206, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        titleLabel = ComponentFactory.createStyledLabel("Manage Employee Attendance", 20);
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Name field
        nameLabel = ComponentFactory.createStyledLabel("Employee Name:", 14);
        nameField = ComponentFactory.createStyledTextField();
        nameField.setEditable(false); // Make it read-only
        addComponent(gbc, nameLabel, nameField, 1);

        // Basic pay field
        basicPayLabel = ComponentFactory.createStyledLabel("Basic Pay:", 14);
        basicPayField = ComponentFactory.createStyledTextField();
        basicPayField.setEditable(false); // Make it read-only
        addComponent(gbc, basicPayLabel, basicPayField, 2);

        // Attendance options
        attendanceLabel = ComponentFactory.createStyledLabel("Attendance:", 14);
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(attendanceLabel, gbc);

        attendanceGroup = new ButtonGroup();
        presentButton = ComponentFactory.createStyledRadioButton("Present");
        absentButton = ComponentFactory.createStyledRadioButton("Absent");
        halfDayButton = ComponentFactory.createStyledRadioButton("Half Day");
        holidayButton = ComponentFactory.createStyledRadioButton("Holiday");

        attendanceGroup.add(presentButton);
        attendanceGroup.add(absentButton);
        attendanceGroup.add(halfDayButton);
        attendanceGroup.add(holidayButton);

        gbc.gridx = 1;
        JPanel attendancePanel = new JPanel(new GridLayout(1, 4));
        attendancePanel.setBackground(new Color(135, 206, 235));
        attendancePanel.add(presentButton);
        attendancePanel.add(absentButton);
        attendancePanel.add(halfDayButton);
        attendancePanel.add(holidayButton);
        add(attendancePanel, gbc);

        // Advance field
        JLabel advanceLabel = ComponentFactory.createStyledLabel("Advance:", 14);
        advanceField = ComponentFactory.createStyledTextField();
        addComponent(gbc, advanceLabel, advanceField, 4);

        // Bonus field
        JLabel bonusLabel = ComponentFactory.createStyledLabel("Bonus:", 14);
        bonusField = ComponentFactory.createStyledTextField();
        addComponent(gbc, bonusLabel, bonusField, 5);

        // Today's payment field
        todayPaymentLabel = ComponentFactory.createStyledLabel("Today's Payment:", 14);
        todayPaymentField = ComponentFactory.createStyledTextField();
        todayPaymentField.setEditable(false); // Make it read-only
        addComponent(gbc, todayPaymentLabel, todayPaymentField, 6);

        // Submit button
        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setBackground(new Color(255, 105, 180)); // Dark pink color
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(this);

        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        // Load employee data
        loadEmployeeData();

        setVisible(true);
    }

    private void addComponent(GridBagConstraints gbc, JLabel label, JTextField field, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(field, gbc);
    }

    private void loadEmployeeData() {
        String query = "SELECT * FROM employees WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, currentEmployeeId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    double basicPay = rs.getDouble("basic_pay");
                    basicPayField.setText(String.valueOf(basicPay));
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found with ID: " + currentEmployeeId);
                    System.exit(0);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            // Calculate today's payment based on attendance
            double todayPayment = calculateTodayPayment();

            // Set the calculated payment to the Today's Payment field
            todayPaymentField.setText(String.format("%.2f", todayPayment)); // Format to two decimal places

            // Update the database with the attendance and payment
            updateAttendanceInDatabase(todayPayment);

            JOptionPane.showMessageDialog(this, "Attendance submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private double calculateTodayPayment() {
        // Parse the basic pay as a double instead of an integer
        double basicPay = Double.parseDouble(basicPayField.getText());
        double advance = 0;
        double bonus = 0;

        // Handle advance field input (allow floating-point values)
        try {
            advance = Double.parseDouble(advanceField.getText());
        } catch (NumberFormatException e) {
            // If invalid, leave advance as 0
        }

        // Handle bonus field input (allow floating-point values)
        try {
            bonus = Double.parseDouble(bonusField.getText());
        } catch (NumberFormatException e) {
            // If invalid, leave bonus as 0
        }

        // Initialize the payment with basic pay
        double todayPayment = basicPay;

        // Adjust payment based on attendance type
        if (absentButton.isSelected()) {
            todayPayment = 0;
        } else if (halfDayButton.isSelected()) {
            todayPayment /= 2;
        }

        // Add bonus and subtract advance
        todayPayment += bonus - advance;

        return todayPayment;
    }

    private void updateAttendanceInDatabase(double todayPayment) {
        String attendanceStatus = "Present";
        if (absentButton.isSelected()) {
            attendanceStatus = "Absent";
        } else if (halfDayButton.isSelected()) {
            attendanceStatus = "Half Day";
        } else if (holidayButton.isSelected()) {
            attendanceStatus = "Holiday";
        }

        String sql = "INSERT INTO attendance (employee_id, attendance_status, today_payment) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE attendance_status = ?, today_payment = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, currentEmployeeId);
            stmt.setString(2, attendanceStatus);
            stmt.setDouble(3, todayPayment);
            stmt.setString(4, attendanceStatus);
            stmt.setDouble(5, todayPayment);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating attendance data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Start the application
        new EmployeeAttendance();
    }
}
