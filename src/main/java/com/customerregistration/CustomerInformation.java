package com.customerregistration;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

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
    
    // Main
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CustomerRegistration reg = new CustomerRegistration();
        boolean running = true;
        
        System.out.println("=== Customer Registration System ===");
        
        while (running) {
            System.out.println("\n--- Enter Customer Details ---");
            
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Contact Number: ");
            String contact = scanner.nextLine();
            
            System.out.print("Address: ");
            String address = scanner.nextLine();
            
            System.out.print("Email: ");
            String email = scanner.nextLine();
            
            System.out.print("Mortgage Type: ");
            String mortgage = scanner.nextLine();
            
            System.out.print("Estimated Value: $");
            double estValue = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Age: ");
            int age = Integer.parseInt(scanner.nextLine());
            
            System.out.print("Account Balance: $");
            double balance = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Monthly Transaction Volume: $");
            double volume = Double.parseDouble(scanner.nextLine());
            
            System.out.print("Is this a Group? (yes/no): ");
            boolean isGroup = scanner.nextLine().trim().equalsIgnoreCase("yes");
            
            System.out.print("Is this a Company? (yes/no): ");
            boolean isCompany = scanner.nextLine().trim().equalsIgnoreCase("yes");
            
            System.out.print("Documents Uploaded? (yes/no): ");
            boolean docs = scanner.nextLine().trim().equalsIgnoreCase("yes");
            
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
            
            System.out.println("\n=== Customer Registered Successfully ===");
            System.out.println(customer.getAllClassifications());
            
            System.out.print("\nRegister another? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("no") || choice.equals("n")) {
                running = false;
            }
        }
        
        System.out.println("\n=== All Registered Customers ===");
        for (CustomerInformation c : reg.getCustomerList()) {
            System.out.println(c.getAllClassifications());
            System.out.println("---");
        }
    }
}