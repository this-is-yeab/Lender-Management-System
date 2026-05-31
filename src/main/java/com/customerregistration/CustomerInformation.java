package com.customerregistration;
import java.util.ArrayList;
import java.util.UUID;
import javax.swing.*;
import java.awt.*;

public class CustomerInformation {

    // Constants
    public static final String PLATINUM = "Platinum";
    public static final String PREMIUM = "Premium";
    public static final String GOLD = "Gold";
    public static final String NORMAL = "Normal";
    
    public static final String INDIVIDUAL = "Individual";
    public static final String GROUP = "Group";
    public static final String COMPANY = "Company";
    
    public static final String UNDER_18 = "Under 18";
    public static final String AGE_18_35 = "18-35";
    public static final String AGE_35_60 = "35-60";
    public static final String AGE_60_PLUS = "60+";
    
    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inactive";
    public static final String SUSPENDED = "Suspended";
    
    public static final String PENDING = "Pending";
    public static final String COMPLETE = "Complete";
    
    // Fields
    private String customerName;
    private String contactNumber;
    private String address;
    private String email;
    private String mortgageType;
    private double estimatedValue;
    private String registrationID;
    private String accountNumber;
    private String currentStep;
    private boolean documentUploaded;
    private int age;
    private double accountBalance;
    private double monthlyTransactionVolume;
    private boolean isGroup;
    private boolean isCompany;
    private String accountTier;
    private String institutionalStatus;
    private String ageGroup;
    private String customerStatus;
    private ArrayList<String> transactions;
    
    // Constructor
    public CustomerInformation(String customerName, String contactNumber, String address,
                               String email, String mortgageType, double estimatedValue,
                               String registrationID, String currentStep, boolean documentUploaded,
                               int age, double accountBalance, double monthlyTransactionVolume,
                               boolean isGroup, boolean isCompany) {
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.address = address;
        this.email = email;
        this.mortgageType = mortgageType;
        this.estimatedValue = estimatedValue;
        this.registrationID = registrationID;
        this.accountNumber = "ACC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.currentStep = currentStep;
        this.documentUploaded = documentUploaded;
        this.age = age;
        this.accountBalance = accountBalance;
        this.monthlyTransactionVolume = monthlyTransactionVolume;
        this.isGroup = isGroup;
        this.isCompany = isCompany;
        
        this.accountTier = classifyAccountTier();
        this.institutionalStatus = classifyInstitutionalStatus();
        this.ageGroup = classifyAgeGroup();
        this.customerStatus = ACTIVE;
        this.transactions = new ArrayList<>();
    }
    
    // Classification
    private String classifyAccountTier() {
        if (accountBalance >= 100000 || monthlyTransactionVolume >= 50000) {
            return PLATINUM;
        } else if (accountBalance >= 50000 || monthlyTransactionVolume >= 10000) {
            return PREMIUM;
        } else if (accountBalance >= 20000 || monthlyTransactionVolume >= 5000) {
            return GOLD;
        } else {
            return NORMAL;
        }
    }
    
    private String classifyInstitutionalStatus() {
        if (isCompany) {
            return COMPANY;
        } else if (isGroup) {
            return GROUP;
        } else {
            return INDIVIDUAL;
        }
    }
    
    private String classifyAgeGroup() {
        if (age < 18) {
            return UNDER_18;
        } else if (age <= 35) {
            return AGE_18_35;
        } else if (age <= 60) {
            return AGE_35_60;
        } else {
            return AGE_60_PLUS;
        }
    }
    
    // Getters
    public String getCustomerName() { return customerName; }
    public String[] getCustomerNames() { return new String[]{customerName}; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getMortgageType() { return mortgageType; }
    public double getEstimatedValue() { return estimatedValue; }
    public String getRegistrationID() { return registrationID; }
    public String getId() { return registrationID; }
    public String getAccountNumber() { return accountNumber; }
    public String getCurrentStep() { return currentStep; }
    public boolean isDocumentUploaded() { return documentUploaded; }
    public int getAge() { return age; }
    public double getBalance() { return accountBalance; }
    public double getVolume() { return monthlyTransactionVolume; }
    public boolean isGroup() { return isGroup; }
    public boolean isCompany() { return isCompany; }
    public String getAccountTier() { return accountTier; }
    public String getInstitutionalStatus() { return institutionalStatus; }
    public String getAgeGroup() { return ageGroup; }
    public ArrayList<String> getTransactions() { return transactions; }
    public boolean isActive() { return customerStatus.equals(ACTIVE); }
    
    public String getAllClassifications() {
        return "Name: " + customerName +
               "\nContact: " + contactNumber +
               "\nAddress: " + address +
               "\nEmail: " + email +
               "\nMortgage Type: " + mortgageType +
               "\nEstimated Value: $" + estimatedValue +
               "\nRegistration ID: " + registrationID +
               "\nAccount: " + accountNumber +
               "\nAge: " + age +
               "\nBalance: $" + accountBalance +
               "\nMonthly Volume: $" + monthlyTransactionVolume +
               "\nIs Group: " + isGroup +
               "\nIs Company: " + isCompany +
               "\nTier: " + accountTier +
               "\nInstitutional Status: " + institutionalStatus +
               "\nAge Group: " + ageGroup +
               "\nCustomer Status: " + customerStatus +
               "\nCurrent Step: " + currentStep +
               "\nDocs Uploaded: " + documentUploaded;
    }
    
    // Setters
    public void setName(String name) {
        this.customerName = name;
    }
    
    public void setBalance(double balance) {
        this.accountBalance = balance;
        this.accountTier = classifyAccountTier();
    }
    
    public void setAge(int age) {
        this.age = age;
        this.ageGroup = classifyAgeGroup();
    }
    
    // Search
    public String searchByName(String name) {
        if (this.customerName.equalsIgnoreCase(name)) {
            return getAllClassifications();
        }
        return "Customer not found.";
    }
    
    public String searchByAccount(String accountNumber) {
        if (this.accountNumber.equals(accountNumber)) {
            return getAllClassifications();
        }
        return "Customer not found.";
    }
    
    public String searchByRegistrationID(String regID) {
        if (this.registrationID.equalsIgnoreCase(regID)) {
            return getAllClassifications();
        }
        return "Customer not found.";
    }
    
    // Display
    public void displayTransactions() {
        System.out.println("=== Transactions for " + customerName + " ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
            }
        }
    }
    
    // Manage
    public void addTransaction(String description) {
        transactions.add(description);
    }
    
    public void updateStatus(String newStatus) {
        if (newStatus.equals(ACTIVE) || newStatus.equals(INACTIVE) || newStatus.equals(SUSPENDED)) {
            this.customerStatus = newStatus;
        }
    }

    // Main - GUI
    public static void main(String[] args) {
        CustomerRegistration reg = new CustomerRegistration();
        
        JFrame frame = new JFrame("Customer Registration System");
        frame.setSize(500, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(14, 2, 5, 5));
        
        inputPanel.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);
        
        inputPanel.add(new JLabel("Contact Number:"));
        JTextField contactField = new JTextField();
        inputPanel.add(contactField);
        
        inputPanel.add(new JLabel("Address:"));
        JTextField addressField = new JTextField();
        inputPanel.add(addressField);
        
        inputPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        inputPanel.add(emailField);
        
        inputPanel.add(new JLabel("Mortgage Type:"));
        JTextField mortgageField = new JTextField();
        inputPanel.add(mortgageField);
        
        inputPanel.add(new JLabel("Estimated Value:"));
        JTextField estValueField = new JTextField();
        inputPanel.add(estValueField);
        
        inputPanel.add(new JLabel("Age:"));
        JTextField ageField = new JTextField();
        inputPanel.add(ageField);
        
        inputPanel.add(new JLabel("Account Balance:"));
        JTextField balanceField = new JTextField();
        inputPanel.add(balanceField);
        
        inputPanel.add(new JLabel("Monthly Volume:"));
        JTextField volumeField = new JTextField();
        inputPanel.add(volumeField);
        
        inputPanel.add(new JLabel("Is Group?"));
        JCheckBox groupBox = new JCheckBox();
        inputPanel.add(groupBox);
        
        inputPanel.add(new JLabel("Is Company?"));
        JCheckBox companyBox = new JCheckBox();
        inputPanel.add(companyBox);
        
        inputPanel.add(new JLabel("Docs Uploaded?"));
        JCheckBox docsBox = new JCheckBox();
        inputPanel.add(docsBox);
        
        JButton registerButton = new JButton("Register Customer");
        JButton viewAllButton = new JButton("View All Customers");
        JButton clearButton = new JButton("Clear");
        
        inputPanel.add(registerButton);
        inputPanel.add(viewAllButton);
        inputPanel.add(clearButton);
        inputPanel.add(new JLabel());
        
        JTextArea outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String contact = contactField.getText();
            String address = addressField.getText();
            String email = emailField.getText();
            String mortgage = mortgageField.getText();
            double estValue = Double.parseDouble(estValueField.getText());
            int age = Integer.parseInt(ageField.getText());
            double balance = Double.parseDouble(balanceField.getText());
            double volume = Double.parseDouble(volumeField.getText());
            boolean isGroup = groupBox.isSelected();
            boolean isCompany = companyBox.isSelected();
            boolean docs = docsBox.isSelected();
            
            String regID = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            String step;
            if (docs) {
                step = COMPLETE;
            } else {
                step = PENDING;
            }
            
            CustomerInformation customer = new CustomerInformation(
                name, contact, address, email, mortgage, estValue,
                regID, step, docs,
                age, balance, volume,
                isGroup, isCompany
            );
            
            reg.registerCustomer(customer);
            
            outputArea.setText("Customer Registered Successfully!\n\n");
            outputArea.append(customer.getAllClassifications());
        });
        
        viewAllButton.addActionListener(e -> {
            outputArea.setText("=== All Registered Customers ===\n\n");
            for (CustomerInformation c : reg.getCustomerList()) {
                outputArea.append(c.getAllClassifications());
                outputArea.append("\n---\n\n");
            }
        });
        
        clearButton.addActionListener(e -> {
            nameField.setText("");
            contactField.setText("");
            addressField.setText("");
            emailField.setText("");
            mortgageField.setText("");
            estValueField.setText("");
            ageField.setText("");
            balanceField.setText("");
            volumeField.setText("");
            groupBox.setSelected(false);
            companyBox.setSelected(false);
            docsBox.setSelected(false);
            outputArea.setText("");
        });
        
        frame.setVisible(true);
    }
    }
