import static com.sun.org.apache.xerces.internal.util.XMLChar.isValidName;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class SalarySlip extends JFrame implements ActionListener {

    // Database connection details
    private String dbURL = "jdbc:mysql://localhost:3306/employee management system";
    private String dbUser = "root"; 
    private String dbPassword = ""; 

    // GUI components
    private JLabel titleLabel, nameLabel, designationLabel, dateLabel;
    private JTextField nameField, designationField;
    private JComboBox<String> monthComboBox;
    private JButton generateButton;

    public SalarySlip() {
        // Set up the JFrame
        setTitle("Salary Slip Generator");
        setSize(500, 500); // Adjust size if needed
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Set the background color to sky blue
        getContentPane().setBackground(new Color(135, 206, 235));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Create GUI components
        titleLabel = new JLabel("Salary Slip Generator", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 25, 112)); // Dark blue for title

        nameLabel = new JLabel("Employee Name:");
        nameField = new JTextField(15);

        designationLabel = new JLabel("Designation:");
        designationField = new JTextField(15);

        dateLabel = new JLabel("Month:");
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        monthComboBox = new JComboBox<>(months);

        generateButton = new JButton("Generate Pay Slip");
        generateButton.setBackground(new Color(255, 182, 193)); // Baby pink color
        generateButton.setForeground(Color.WHITE);
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.addActionListener(this);

        // Add components to the frame
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span title across two columns
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset grid width
        gbc.gridy++;
        add(nameLabel, gbc);
        gbc.gridx++;
        add(nameField, gbc);

        gbc.gridx = 0; // Reset to first column
        gbc.gridy++;
        add(designationLabel, gbc);
        gbc.gridx++;
        add(designationField, gbc);

        gbc.gridx = 0; // Reset to first column
        gbc.gridy++;
        add(dateLabel, gbc);
        gbc.gridx++;
        add(monthComboBox, gbc);

        gbc.gridy++;
        gbc.gridx = 0; // Reset to first column
        gbc.gridwidth = 2; // Span button across two columns
        add(generateButton, gbc);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            // Get input values
            String employeeName = nameField.getText();
            String designation = designationField.getText();
            String month = (String) monthComboBox.getSelectedItem();

            // Validate inputs
            if (employeeName == null || employeeName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid Employee Name. Only alphabets and spaces are allowed (2-50 characters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidDesignation(designation)) {
                JOptionPane.showMessageDialog(this, "Invalid Designation. Only alphabets, spaces, hyphens, and slashes are allowed (2-100 characters).", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Log input for debugging
            System.out.println("Searching for employee: " + employeeName + " with designation: " + designation);

            // Load employee data from database
            Employee employee = loadEmployeeData(employeeName, designation, month);

            if (employee != null) {
                // Generate salary slip
                String salarySlip = generateSalarySlip(employee);
                // Save salary slip to the database
                saveSalarySlipToDatabase(employee);
                // Display salary slip in a dialog
                JOptionPane.showMessageDialog(this, salarySlip, "Salary Slip", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Employee not found or data is incomplete!");
            }
        }
    }

    private Employee loadEmployeeData(String employeeName, String designation, String month) {
        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM employees WHERE LOWER(name) = LOWER(?) AND LOWER(role) = LOWER(?)")) {

            stmt.setString(1, employeeName);
            stmt.setString(2, designation);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int employeeId = rs.getInt("id");
                    double basicPay = rs.getDouble("basic_pay");
                    double allowance = rs.getDouble("allowance");
                    double deduction = rs.getDouble("deduction");

                    // If basic pay is zero or missing, handle it
                    if (basicPay <= 0) {
                        // Log the issue and set a default value for basic pay
                        System.out.println("Basic pay is zero or missing for " + employeeName);
                        basicPay = 30000; // Set a default value for basic pay
                        System.out.println("Default basic pay set to: " + basicPay);
                    }

                    return new Employee(employeeId, employeeName, designation, basicPay, allowance, deduction, month);
                } else {
                    // Log error if no employee found
                    System.out.println("No employee found with the given name and designation.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage());
        }

        return null;
    }

    private void saveSalarySlipToDatabase(Employee employee) {
        String sql = "INSERT INTO salary_slips (employee_id, month, net_salary) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employee.id); // Use the employee ID
            pstmt.setString(2, employee.getMonth()); // Month
            pstmt.setDouble(3, employee.getNetSalary()); // Net Salary

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Salary slip saved to database.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving salary slip: " + e.getMessage());
        }
    }

    private String generateSalarySlip(Employee employee) {
        StringBuilder salarySlip = new StringBuilder();

        salarySlip.append("Salary Slip for ").append(employee.getName()).append(" (").append(employee.getDesignation()).append(")\n");
        salarySlip.append("Month: ").append(employee.getMonth()).append("\n");
        salarySlip.append("Basic Salary: ").append(employee.getBasicSalary()).append("\n");
        salarySlip.append("Allowance: ").append(employee.getAllowance()).append("\n");
        salarySlip.append("Deduction: ").append(employee.getDeduction()).append("\n");
        salarySlip.append("Net Salary: ").append(employee.getNetSalary()).append("\n");

        return salarySlip.toString();
    }

    private boolean isValidDesignation(String designation) {
        return designation != null && designation.matches("[A-Za-z\\s-\\/]{2,100}");
    }

    private class Employee {
        private int id;
        private String name;
        private String designation;
        private double basicSalary;
        private double allowance;
        private double deduction;
        private String month;

        public Employee(int id, String name, String designation, double basicSalary, double allowance, double deduction, String month) {
            this.id = id;
            this.name = name;
            this.designation = designation;
            this.basicSalary = basicSalary;
            this.allowance = allowance;
            this.deduction = deduction;
            this.month = month;
        }

        public String getName() {
            return name;
        }

        public String getDesignation() {
            return designation;
        }

        public double getBasicSalary() {
            return basicSalary;
        }

        public double getAllowance() {
            return allowance;
        }

        public double getDeduction() {
            return deduction;
        }

        public String getMonth() {
            return month;
        }

        public double getNetSalary() {
            return basicSalary + allowance - deduction;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SalarySlip());
    }
}

       
       
        
             
       
         
       
        
       

    
           
       

      
