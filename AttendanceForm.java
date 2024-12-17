import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EmployeeAttendance extends JFrame implements ActionListener {

    // Database connection details
    private String dbUrl = "jdbc:mysql://localhost:3306/employee management system"; 
    private String dbUser = "root";
    private String dbPassword = "";

    // Components
    private JLabel titleLabel, nameLabel, basicPayLabel, todayPaymentLabel, attendanceLabel;
    private JTextField nameField, basicPayField, todayPaymentField;
    private JTextField advanceField, bonusField;
    private JButton submitButton;
    private JRadioButton presentButton, absentButton, halfDayButton, holidayButton;
    private ButtonGroup attendanceGroup;
    private int currentEmployeeId;
    private Employee employee;

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

        // Initialize Employee object
        employee = new Employee(currentEmployeeId);

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
        nameField.setText(employee.getName());
        addComponent(gbc, nameLabel, nameField, 1);

        // Basic pay field
        basicPayLabel = ComponentFactory.createStyledLabel("Basic Pay:", 14);
        basicPayField = ComponentFactory.createStyledTextField();
        basicPayField.setEditable(false); // Make it read-only
        basicPayField.setText(String.valueOf(employee.getBasicPay()));
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

    double calculateTodayPayment() {
        double basicPay = Double.parseDouble(basicPayField.getText());
        double advance = 0;
        double bonus = 0;

        try {
            advance = Double.parseDouble(advanceField.getText());
        } catch (NumberFormatException e) {
            // If invalid, leave advance as 0
        }

        try {
            bonus = Double.parseDouble(bonusField.getText());
        } catch (NumberFormatException e) {
            // If invalid, leave bonus as 0
        }

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

        try (Connection conn = employee.getConnection()) {
            String query = "INSERT INTO attendance (employee_id, attendance_status, today_payment, advance, bonus) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, currentEmployeeId);
            stmt.setString(2, attendanceStatus);
            stmt.setDouble(3, todayPayment);
            stmt.setDouble(4, Double.parseDouble(advanceField.getText()));
            stmt.setDouble(5, Double.parseDouble(bonusField.getText()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating attendance: " + e.getMessage());
        }
    }

    // Employee class
    public class Employee {
        private int id;
        private String name;
        private double basicPay;
        private Connection connection;

        // Employee constructor initializes after successful connection
        public Employee(int id) {
            this.id = id;
            try {
                // Initialize database connection
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                loadEmployeeData();  // Load employee data after connection is established
            } catch (SQLException e) {
                System.out.println("Error connecting to database: " + e.getMessage());
            }
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getBasicPay() {
            return basicPay;
        }

        public Connection getConnection() {
            return connection;
        }

        // Loads employee data from the database
        public void loadEmployeeData() {
            try {
                String query = "SELECT * FROM employees WHERE id = ?";
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    this.name = rs.getString("name");
                    this.basicPay = rs.getDouble("basic_pay");
                } else {
                    System.out.println("No employee found with ID: " + id);
                    name = "Unknown";
                    basicPay = 0;
                }
            } catch (SQLException e) {
                System.out.println("Error loading employee data: " + e.getMessage());
            }
        }

        public void closeConnection() {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    // Component factory class to create components
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
    // Add these getter and setter methods to your EmployeeAttendance class
public JTextField getBasicPayField() {
    return basicPayField;
}

public void setBasicPayField(String value) {
    basicPayField.setText(value);
}

public JTextField getAdvanceField() {
    return advanceField;
}

public void setAdvanceField(String value) {
    advanceField.setText(value);
}

public JTextField getBonusField() {
    return bonusField;
}

public void setBonusField(String value) {
    bonusField.setText(value);
}
public JRadioButton getPresentButton() {
    return presentButton;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(EmployeeAttendance::new);
    }
}


  
