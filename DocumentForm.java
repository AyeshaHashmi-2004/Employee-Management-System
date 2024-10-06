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

public class DocumentForm extends JFrame {
    private JComboBox<String> cbEmployees; // Load employee names dynamically
    private JTextField txtDocumentName, txtUploadDate;
    private JButton btnSave;

    public DocumentForm() {
        setTitle("Document Form");
        setSize(400, 300);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblEmployee = new JLabel("Employee:");
        lblEmployee.setBounds(30, 30, 100, 30);
        cbEmployees = new JComboBox<>();
        cbEmployees.setBounds(150, 30, 200, 30);
        // Load employee names into cbEmployees from the database here

        JLabel lblDocumentName = new JLabel("Document Name:");
        lblDocumentName.setBounds(30, 70, 120, 30);
        txtDocumentName = new JTextField();
        txtDocumentName.setBounds(150, 70, 200, 30);

        JLabel lblUploadDate = new JLabel("Upload Date:");
        lblUploadDate.setBounds(30, 110, 100, 30);
        txtUploadDate = new JTextField();
        txtUploadDate.setBounds(150, 110, 200, 30);

        btnSave = new JButton("Save");
        btnSave.setBounds(150, 150, 100, 30);
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveDocument();
            }
        });

        add(lblEmployee);
        add(cbEmployees);
        add(lblDocumentName);
        add(txtDocumentName);
        add(lblUploadDate);
        add(txtUploadDate);
        add(btnSave);
    }

    private void saveDocument() {
        String employee = (String) cbEmployees.getSelectedItem();
        String documentName = txtDocumentName.getText();
        String uploadDate = txtUploadDate.getText();

        try (Connection connection = DatabaseConnection.connect()) {
            String query = "INSERT INTO Documents (employee_id, document_name, upload_date) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, getEmployeeId(employee));
            preparedStatement.setString(2, documentName);
            preparedStatement.setString(3, uploadDate);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(this, "Document saved successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving document.");
        }
    }

    private int getEmployeeId(String employee) {
        // Implement a method to get employee ID based on the selected employee name
        return 1; // Placeholder
    }

    public static void main(String[] args) {
        new DocumentForm().setVisible(true);
    }
}

