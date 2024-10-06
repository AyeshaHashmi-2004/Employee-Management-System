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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CertificationForm extends JFrame {
    private JComboBox<String> cbEmployees; // Load employee names dynamically
    private JComboBox<String> cbTrainingPrograms; // Load training program names dynamically
    private JTextField txtDateObtained, txtExpirationDate;
    private JButton btnSave;

    public CertificationForm() {
        setTitle("Employee Certification Form");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblEmployee = new JLabel("Employee:");
        lblEmployee.setBounds(30, 30, 100, 30);
        cbEmployees = new JComboBox<>();
        cbEmployees.setBounds(150, 30, 200, 30);
        // Load employee names into cbEmployees from the database here

        JLabel lblTrainingProgram = new JLabel("Training Program:");
        lblTrainingProgram.setBounds(30, 70, 120, 30);
        cbTrainingPrograms = new JComboBox<>();
        cbTrainingPrograms.setBounds(150, 70, 200, 30);
        // Load training program names into cbTrainingPrograms from the database here

        JLabel lblDateObtained = new JLabel("Date Obtained:");
        lblDateObtained.setBounds(30, 110, 100, 30);
        txtDateObtained = new JTextField();
        txtDateObtained.setBounds(150, 110, 200, 30);

        JLabel lblExpirationDate = new JLabel("Expiration Date:");
        lblExpirationDate.setBounds(30, 150, 100, 30);
        txtExpirationDate = new JTextField();
        txtExpirationDate.setBounds(150, 150, 200, 30);

        btnSave = new JButton("Save");
        btnSave.setBounds(150, 190, 100, 30);
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCertification();
            }
        });

        add(lblEmployee);
        add(cbEmployees);
        add(lblTrainingProgram);
        add(cbTrainingPrograms);
        add(lblDateObtained);
        add(txtDateObtained);
        add(lblExpirationDate);
        add(txtExpirationDate);
        add(btnSave);
    }

    private void saveCertification() {
        String employee = (String) cbEmployees.getSelectedItem();
        String trainingProgram = (String) cbTrainingPrograms.getSelectedItem();
        String dateObtained = txtDateObtained.getText();
        String expirationDate = txtExpirationDate.getText();

        try (Connection connection = DatabaseConnection.connect()) {
            String query = "INSERT INTO EmployeeCertifications (employee_id, program_id, date_obtained, expiration_date) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, getEmployeeId(employee));
            preparedStatement.setInt(2, getProgramId(trainingProgram));
            preparedStatement.setString(3, dateObtained);
            preparedStatement.setString(4, expirationDate);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Certification saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving certification.");
        }
    }

    private int getEmployeeId(String employee) {
        // Implement a method to get employee ID based on the selected employee name
        return 1; // Placeholder
    }

    private int getProgramId(String program) {
        // Implement a method to get program ID based on the selected program name
        return 1; // Placeholder
    }

    public static void main(String[] args) {
        new CertificationForm().setVisible(true);
    }
}
