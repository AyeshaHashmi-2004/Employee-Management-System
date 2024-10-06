
import javax.swing.JFrame;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class AttendanceForm extends JFrame {
    private JTextField employeeIdField, checkInField, checkOutField;
    private JButton submitButton;

    public AttendanceForm() {
        setTitle("Attendance Tracking");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel employeeIdLabel = new JLabel("Employee ID:");
        employeeIdLabel.setBounds(10, 10, 100, 25);
        add(employeeIdLabel);

        employeeIdField = new JTextField();
        employeeIdField.setBounds(120, 10, 150, 25);
        add(employeeIdField);

        submitButton = new JButton("Submit");
        submitButton.setBounds(100, 120, 80, 25);
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int employeeId = Integer.parseInt(employeeIdField.getText());
                // Insert the data into the database
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new AttendanceForm();
    }
}