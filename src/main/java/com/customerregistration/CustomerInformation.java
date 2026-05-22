package com.customerregistration;

import java.util.Random;

public class CustomerInformation {
    private String[] customerNames = new String[1];
    private String contactNumber;
    private String address;
    private String email;
    private String mortgageType;
    private double estimatedValue;
    private String registrationID;
    private String currentStep;
    private boolean isDocumentUploaded;
    private int age;
    private double balance;
    private double volume;
    private boolean isGroup;
    private boolean isCompany;
    private String accountNumber;

    public CustomerInformation(String name, String contact, String address, String email, 
                               String mortgageType, double estimatedValue, String registrationID, 
                               String currentStep, boolean isDocumentUploaded, int age, 
                               double balance, double volume, boolean isGroup, boolean isCompany) {
        this.customerNames[0] = name;
        this.contactNumber = contact;
        this.address = address;
        this.email = email;
        this.mortgageType = mortgageType;
        this.estimatedValue = estimatedValue;
        this.registrationID = registrationID;
        this.currentStep = currentStep;
        this.isDocumentUploaded = isDocumentUploaded;
        this.age = age;
        this.balance = balance;
        this.volume = volume;
        this.isGroup = isGroup;
        this.isCompany = isCompany;
        this.accountNumber = "ACC-" + (100000 + new Random().nextInt(900000));
    }

    public String[] getCustomerNames() { return customerNames; }
    public String getContactNumber() { return contactNumber; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getMortgageType() { return mortgageType; }
    public double getEstimatedValue() { return estimatedValue; }
    public String getRegistrationID() { return registrationID; }
    public String getCurrentStep() { return currentStep; }
    public boolean isDocumentUploaded() { return isDocumentUploaded; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public double getVolume() { return volume; }
    public boolean isGroup() { return isGroup; }
    public boolean isCompany() { return isCompany; }
    public String getAccountNumber() { return accountNumber; }
}