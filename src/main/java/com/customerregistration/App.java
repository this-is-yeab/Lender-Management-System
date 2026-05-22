package com.customerregistration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {
    private JTextField txtName, txtContact, txtAddress, txtEmail, txtMortgage, txtValue, txtStep;
    private JTextField txtAge, txtBalance, txtVolume;
    private JCheckBox chkIndividual, chkGroup, chkCompany, chkDocs;
    private JButton btnRegister;
    
    private CustomerRegistration registrationEngine;

    public App() {
        registrationEngine = new CustomerRegistration();
        
        setTitle("Local Lender Management System (Mini Bank)");
        setSize(450, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(16, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Customer Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Age:"));
        txtAge = new JTextField();
        formPanel.add(txtAge);

        formPanel.add(new JLabel("Initial Balance ($):"));
        txtBalance = new JTextField();
        formPanel.add(txtBalance);

        formPanel.add(new JLabel("Loan Volume:"));
        txtVolume = new JTextField("0.0");
        txtVolume.setEditable(false);
        txtVolume.setBackground(new Color(235, 235, 235));
        formPanel.add(txtVolume);

        formPanel.add(new JLabel("Contact Number:"));
        txtContact = new JTextField();
        formPanel.add(txtContact);

        formPanel.add(new JLabel("Email Address:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Home Address:"));
        txtAddress = new JTextField();
        formPanel.add(txtAddress);

        formPanel.add(new JLabel("Mortgage Type:"));
        txtMortgage = new JTextField();
        formPanel.add(txtMortgage);

        formPanel.add(new JLabel("Estimated Asset Value ($):"));
        txtValue = new JTextField("0.0");
        txtValue.setEditable(false);
        txtValue.setBackground(new Color(235, 235, 235));
        formPanel.add(txtValue);

        formPanel.add(new JLabel("Current Processing Step:"));
        txtStep = new JTextField("Step 1");
        formPanel.add(txtStep);

        formPanel.add(new JLabel("Is Individual Account:"));
        chkIndividual = new JCheckBox();
        formPanel.add(chkIndividual);

        formPanel.add(new JLabel("Is Group Account:"));
        chkGroup = new JCheckBox();
        formPanel.add(chkGroup);

        formPanel.add(new JLabel("Is Corporate Company:"));
        chkCompany = new JCheckBox();
        formPanel.add(chkCompany);

        formPanel.add(new JLabel("Documents Uploaded:"));
        chkDocs = new JCheckBox();
        formPanel.add(chkDocs);

        btnRegister = new JButton("Register New Customer");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 14));
        btnRegister.setBackground(new Color(34, 139, 34));
        btnRegister.setForeground(Color.WHITE);

        add(formPanel, BorderLayout.CENTER);
        add(btnRegister, BorderLayout.SOUTH);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });
    }

    private void handleRegistration() {
        try {
            String name = txtName.getText().trim();
            int age = Integer.parseInt(txtAge.getText().trim());
            double balance = Double.parseDouble(txtBalance.getText().trim());
            double volume = Double.parseDouble(txtVolume.getText().trim());
            String contact = txtContact.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();
            String mortgage = txtMortgage.getText().trim();
            double estValue = Double.parseDouble(txtValue.getText().trim());
            String step = txtStep.getText().trim();
            boolean isGroup = chkGroup.isSelected();
            boolean isCompany = chkCompany.isSelected();
            boolean docs = chkDocs.isSelected();

            if (name.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Contact fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String genID = "REG-" + (1000 + new java.util.Random().nextInt(9000));

            CustomerInformation newCustomer = new CustomerInformation(
                name, contact, address, email, mortgage, estValue, 
                genID, step, docs, age, balance, volume, isGroup, isCompany
            );

            registrationEngine.registerCustomer(newCustomer);

            JOptionPane.showMessageDialog(this, "Customer Registered Successfully!\nAccount Number: " + newCustomer.getAccountNumber(), "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric inputs for Age and Balance.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtAge.setText("");
        txtBalance.setText("");
        txtVolume.setText("0.0");
        txtContact.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        txtMortgage.setText("");
        txtValue.setText("0.0");
        txtStep.setText("Step 1");
        chkIndividual.setSelected(false);
        chkGroup.setSelected(false);
        chkCompany.setSelected(false);
        chkDocs.setSelected(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App().setVisible(true);
            }
        });
    }
}