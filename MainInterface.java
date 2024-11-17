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

public class MainInterface extends JFrame implements ActionListener {

    private JLabel welcomeLabel;
    private JButton loginButton;
    private JButton attendanceButton;
    private JButton salarySlipButton;
    private JButton newEmployeeButton;
    private JButton leaveButton;
    private JLabel imageLabel; 

    public MainInterface() {
        super("Employee Management System");
        setSize(600, 400); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
        welcomeLabel = new JLabel("Welcome to Employee Management System", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20)); 
        welcomeLabel.setForeground(new Color(0, 102, 204)); 
        add(welcomeLabel, BorderLayout.NORTH);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 0, 9, 9));
        buttonPanel.setBackground(new Color(173, 216, 230)); 

        
        loginButton = createButton("Login");
        buttonPanel.add(loginButton);

        attendanceButton = createButton("Employee Attendance");
        buttonPanel.add(attendanceButton);

        salarySlipButton = createButton("Salary Slip");
        buttonPanel.add(salarySlipButton);

        newEmployeeButton = createButton("New Employee Form");
        buttonPanel.add(newEmployeeButton);
        
        leaveButton = createButton("Leave");
        buttonPanel.add(leaveButton);

      
        ImageIcon imageIcon = new ImageIcon("C:\\Users\\hp\\Downloads\\Futuristic Approach to Human Resource Management and Applicant Tracking system.jfif"); 
        Image img = imageIcon.getImage().getScaledInstance(400, 350, Image.SCALE_SMOOTH); 
        imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setPreferredSize(new Dimension(400, 500)); 

        
        add(imageLabel, BorderLayout.EAST); 
        add(buttonPanel, BorderLayout.CENTER); 

        
        addFadeInEffect(welcomeLabel);

        
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 30)); 
        button.setBackground(new Color(255, 182, 193)); 
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); 

        button.setFocusPainted(false); 
        button.addActionListener(this);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 105, 180)); 
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 182, 193)); 
            }
        });

        return button;
    }

    private void addFadeInEffect(JLabel label) {
        Timer timer = new Timer(50, null);
        timer.addActionListener(new ActionListener() {
            float alpha = 0.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha < 1.0f) {
                    alpha += 0.05f; // Increase alpha
                    label.setForeground(new Color(0, 102, 204, Math.min((int)(alpha * 255), 255))); 
                } else {
                    timer.stop(); // Stop the timer when fully visible
                }
            }
        });
        timer.start(); // Start the timer
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button clicks
       if (e.getSource() == loginButton) {
            openLoginInterface();
        } else if (e.getSource() == attendanceButton) {
            openAttendanceInterface();
        } else if (e.getSource() == salarySlipButton) {
            openSalarySlipInterface();
        } else if (e.getSource() == newEmployeeButton) {
            openNewEmployeeInterface();
        } else if (e.getSource() == leaveButton) {
            openLeaveInterface();
        }
    }

    private void openLoginInterface() {
        Login login = new Login(); // Replace with your actual login interface
    }

    private void openAttendanceInterface() {
        new EmployeeAttendance(); // Replace with your actual attendance interface
    }

    private void openSalarySlipInterface() {
        new SalarySlip(); // Replace with your actual salary slip interface
    }

    private void openNewEmployeeInterface() {
        new NewEmployeeForm(); // Replace with your actual new employee interface
    }

    private void openLeaveInterface() {
        new Leave(); // Replace with your actual leave interface
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainInterface::new);
    }
}