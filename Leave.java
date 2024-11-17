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
import java.awt.event.*;
import java.sql.*;

public class Leave extends JFrame implements ActionListener {
    // Use underscore instead of spaces in the database name
    private String dbURL = "jdbc:mysql://localhost:3306/employee management system"; 
    private String dbUser = "root";
    private String dbPassword = ""; 
    private JTextField employeeIdField, leaveTypeField, startDateField, endDateField;
    private JButton submitLeaveButton, viewBalanceButton;

    public Leave() {
        setTitle("Leave Management System");
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        getContentPane().setBackground(new Color(135, 206, 235));
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels and fields
        JLabel employeeIdLabel = new JLabel("Employee ID:");
        employeeIdField = createStyledTextField();
        JLabel leaveTypeLabel = new JLabel("Leave Type:");
        leaveTypeField = createStyledTextField();
        JLabel startDateLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startDateField = createStyledTextField();
        JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
        endDateField = createStyledTextField();

        // Buttons
        submitLeaveButton = new JButton("Submit Leave Request");
        submitLeaveButton.setBackground(new Color(255, 105, 180)); 
        submitLeaveButton.setForeground(Color.WHITE);
        submitLeaveButton.addActionListener(this);

        viewBalanceButton = new JButton("View Leave Balance");
        viewBalanceButton.setBackground(new Color(255, 105, 180)); 
        viewBalanceButton.setForeground(Color.WHITE);
        viewBalanceButton.addActionListener(this);

        // Adding components to the frame with proper grid positioning
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(employeeIdLabel, gbc);

        gbc.gridx = 1;
        add(employeeIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(leaveTypeLabel, gbc);

        gbc.gridx = 1;
        add(leaveTypeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(startDateLabel, gbc);

        gbc.gridx = 1;
        add(startDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(endDateLabel, gbc);

        gbc.gridx = 1;
        add(endDateField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(submitLeaveButton, gbc);

        gbc.gridy = 5;
        add(viewBalanceButton, gbc);

        setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15); // Set smaller size
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return textField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitLeaveButton) {
            submitLeaveRequest();
        } else if (e.getSource() == viewBalanceButton) {
            viewLeaveBalance();
        }
    }

    private void submitLeaveRequest() {
        String employeeId = employeeIdField.getText();
        String leaveType = leaveTypeField.getText();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();

        // Check if any field is empty
        if (employeeId.isEmpty() || leaveType.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO leave-requests (employee_id, leave_type, start_date, end_date, status) VALUES (?, ?, ?, ?, ?)")) {
            // If employeeId is an integer, use setInt instead of setString
            stmt.setInt(1, Integer.parseInt(employeeId)); // Use setInt if employee_id is an integer
            stmt.setString(2, leaveType);
            stmt.setString(3, startDate);
            stmt.setString(4, endDate);
            stmt.setString(5, "Pending"); 
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Leave request submitted successfully.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error submitting leave request: " + ex.getMessage());
        }
    }

    private void viewLeaveBalance() {
        String employeeId = employeeIdField.getText();
        
        // Check if employeeId field is empty
        if (employeeId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Employee ID must be entered.");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement("SELECT remaining_leave FROM employees WHERE id = ?")) {
            stmt.setInt(1, Integer.parseInt(employeeId)); // Use setInt for employee_id field
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int remainingLeave = rs.getInt("remaining_leave");
                JOptionPane.showMessageDialog(this, "Remaining Leave: " + remainingLeave);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error retrieving leave balance: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Leave::new);
    }
}
