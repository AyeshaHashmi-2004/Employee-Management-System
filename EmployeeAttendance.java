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

    private String dbUrl = "jdbc:mysql://localhost:3306/employee management system";  // Corrected database name format
    private String dbUser = "root";
    private String dbPassword = "";

    private JLabel titleLabel, nameLabel, basicPayLabel, todayPaymentLabel, attendanceLabel;
    private JTextField nameField, basicPayField, todayPaymentField, advanceField, bonusField;
    private JButton submitButton;
    private JRadioButton presentButton, absentButton, halfDayButton, holidayButton;
    private ButtonGroup attendanceGroup;
    private int currentEmployeeId; // Store the current employee ID for updates

    public EmployeeAttendance() {
        super("Employee Attendance");

        // Prompt user for Employee ID
        String inputId = JOptionPane.showInputDialog(this, "Enter Employee ID:", "Input", JOptionPane.QUESTION_MESSAGE);
        if (inputId == null || inputId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee ID is required. Exiting program.");
            System.exit(0);
        }
        try {
            currentEmployeeId = Integer.parseInt(inputId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Employee ID format. Exiting program.");
            System.exit(0);
        }

        // Set up frame
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(135, 206, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        titleLabel = new JLabel("Manage Employee Attendance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Name field
        nameLabel = new JLabel("Employee Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameField = createStyledTextField();
        nameField.setEditable(false); // Make it read-only
        addComponent(gbc, nameLabel, nameField, 1);

        // Basic pay field
        basicPayLabel = new JLabel("Basic Pay:");
        basicPayLabel.setFont(new Font("Arial", Font.BOLD, 14));
        basicPayField = createStyledTextField();
        basicPayField.setEditable(false); // Make it read-only
        addComponent(gbc, basicPayLabel, basicPayField, 2);

        // Attendance options
        attendanceLabel = new JLabel("Attendance:");
        attendanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(attendanceLabel, gbc);

        attendanceGroup = new ButtonGroup();
        presentButton = createStyledRadioButton("Present");
        absentButton = createStyledRadioButton("Absent");
        halfDayButton = createStyledRadioButton("Half Day");
        holidayButton = createStyledRadioButton("Holiday");

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
        JLabel advanceLabel = new JLabel("Advance:");
        advanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        advanceField = createStyledTextField();
        addComponent(gbc, advanceLabel, advanceField, 4);

        // Bonus field
        JLabel bonusLabel = new JLabel("Bonus:");
        bonusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bonusField = createStyledTextField();
        addComponent(gbc, bonusLabel, bonusField, 5);

        // Today's payment field
        todayPaymentLabel = new JLabel("Today's Payment:");
        todayPaymentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        todayPaymentField = createStyledTextField();
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

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15); // Set smaller size
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    private JRadioButton createStyledRadioButton(String text) {
        JRadioButton radioButton = new JRadioButton(text);
        radioButton.setBackground(new Color(135, 206, 235)); // Sky blue background to match frame
        radioButton.setFont(new Font("Arial", Font.PLAIN, 14));
        return radioButton;
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
                    basicPayField.setText(String.valueOf(rs.getDouble("basic_pay")));
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
            try {
                double todayPayment = calculateTodayPayment();
                todayPaymentField.setText(String.format("%.2f", todayPayment));
                updateAttendanceInDatabase(todayPayment);
                JOptionPane.showMessageDialog(this, "Attendance submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Advance and Bonus.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private double calculateTodayPayment() {
        double basicPay = Double.parseDouble(basicPayField.getText());
        double advance = Double.parseDouble(advanceField.getText());
        double bonus = Double.parseDouble(bonusField.getText());

        double todayPayment = basicPay;
        if (absentButton.isSelected()) {
            todayPayment = 0;
        } else if (halfDayButton.isSelected()) {
            todayPayment /= 2;
        }

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
            JOptionPane.showMessageDialog(this, "Error updating attendance: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeAttendance::new);
    }
}

