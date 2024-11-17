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

    public EmployeeAttendance(int employeeId) {
        super("Employee Attendance");
        currentEmployeeId = employeeId; // Initialize the employee ID
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        setResizable(false);
        getContentPane().setBackground(new Color(135, 206, 235));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel("Manage Employee Attendance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        gbc.gridwidth = 1;

        nameLabel = new JLabel("Employee Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameField = createStyledTextField();
        addComponent(gbc, nameLabel, nameField, 1);

        basicPayLabel = new JLabel("Basic Pay:");
        basicPayLabel.setFont(new Font("Arial", Font.BOLD, 14));
        basicPayField = createStyledTextField();
        basicPayField.setBackground(Color.WHITE); // Make writable
        addComponent(gbc, basicPayLabel, basicPayField, 2);

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

        JLabel advanceLabel = new JLabel("Advance:");
        advanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        advanceField = createStyledTextField();
        addComponent(gbc, advanceLabel, advanceField, 4);

        JLabel bonusLabel = new JLabel("Bonus:");
        bonusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bonusField = createStyledTextField();
        addComponent(gbc, bonusLabel, bonusField, 5);

        todayPaymentLabel = new JLabel("Today's Payment:");
        todayPaymentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        todayPaymentField = createStyledTextField();
        todayPaymentField.setBackground(Color.WHITE); // Make writable
        addComponent(gbc, todayPaymentLabel, todayPaymentField, 6);

        submitButton = new JButton("Submit");
        submitButton.setFont(new Font("Arial", Font.BOLD, 12));
        submitButton.setBackground(new Color(255, 105, 180)); // Dark pink color
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.addActionListener(this);

        // Centering the submit button in the grid
        gbc.gridy = 7;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitButton, gbc);

        // Load employee data from database
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
        // Remove LIMIT 1 and query for specific employee data
        String query = "SELECT * FROM employees WHERE id = ?";  // Assuming you have an employee_id to query with
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Use the current employee ID to fetch data
            stmt.setInt(1, currentEmployeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    basicPayField.setText(String.valueOf(rs.getDouble("basic_pay")));
                } else {
                    JOptionPane.showMessageDialog(this, "No employee found with the provided ID.");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            double todayPayment = calculateTodayPayment();
            todayPaymentField.setText(String.format("%.2f", todayPayment));
            updateAttendanceInDatabase(todayPayment);
            JOptionPane.showMessageDialog(this, "Attendance submitted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
        if (currentEmployeeId == 0) {
            JOptionPane.showMessageDialog(this, "No employee ID set. Please load the employee data.");
            return;
        }

        String attendanceStatus = "Present";
        if (absentButton.isSelected()) {
            attendanceStatus = "Absent";
        } else if (halfDayButton.isSelected()) {
            attendanceStatus = "Half Day";
        } else if (holidayButton.isSelected()) {
            attendanceStatus = "Holiday";
        }

        String sql = "UPDATE attendance SET attendance_status = ?, today_payment = ? WHERE employee_id = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, attendanceStatus);
            pstmt.setDouble(2, todayPayment);
            pstmt.setInt(3, currentEmployeeId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating attendance: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Pass employee ID when creating the EmployeeAttendance instance
        SwingUtilities.invokeLater(() -> new EmployeeAttendance(1)); // Example with employee ID 1
    }
}
