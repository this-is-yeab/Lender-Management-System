package com.customerregistration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class CustomerRegistration {

    private String[] customerNames = new String[0];
    private String contactNumber = "";
    private String email = "";
    private String collateralDescription = "";
    private String registrationID = "NOT_ASSIGNED";
    private String status = "Pending";

    public CustomerRegistration() {}

    private CustomerRegistration(String id, String name, String contact, String email, String collateral, String status) {
        this.registrationID = id;
        this.customerNames = new String[]{name};
        this.contactNumber = contact;
        this.email = email;
        this.collateralDescription = collateral;
        this.status = status;
    }

    public void inputCustomerData(String name, String contact, String email) {
        this.contactNumber = (contact != null) ? contact : "";
        this.email = (email != null) ? email : "";
        if (name != null && !name.trim().isEmpty()) {
            addNewClientToArray(name);
        }
    }

    public void recordCollateralInfo(String description) {
        this.collateralDescription = (description != null) ? description : "";
    }

    public void addNewClientToArray(String newName) {
        String[] tempArray = new String[customerNames.length + 1];
        for (int i = 0; i < customerNames.length; i++) {
            tempArray[i] = customerNames[i];
        }
        tempArray[tempArray.length - 1] = newName;
        this.customerNames = tempArray;
    }

    public List<CustomerInformation> convertToSearchList() {
        List<CustomerInformation> searchList = new ArrayList<>();
        if (customerNames != null) {
            for (String name : customerNames) {
                CustomerInformation info = new CustomerInformation();
                info.setCustomerName(name); 
                searchList.add(info);
            }
        }
        return searchList;
    }

    public String saveRegistration() {
        if (customerNames.length > 0) {
            this.registrationID = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.status = "Complete";
            saveToFileDatabase();
            return "Success";
        }
        return "Failed";
    }

    private void saveToFileDatabase() {
        String latestName = customerNames[customerNames.length - 1];
        try (FileWriter fw = new FileWriter("customer_database.txt", true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("=========================================");
            pw.println("Registration ID: " + this.registrationID);
            pw.println("Name:            " + latestName);
            pw.println("Contact:         " + this.contactNumber);
            pw.println("Email:           " + this.email);
            pw.println("Collateral:      " + this.collateralDescription);
            pw.println("Status:          " + this.status);
            pw.println("=========================================\n");
            
        } catch (IOException e) {
            System.out.println("ALERT: Local database file write error occurred: " + e.getMessage());
        }
    }

    public List<String> getAllRegistrationIDs() {
        List<String> validIDs = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("customer_database.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Registration ID:")) {
                    validIDs.add(line.replace("Registration ID:", "").trim());
                }
            }
        } catch (IOException e) {
        }
        return validIDs;
    }

    public List<CustomerRegistration> getAllRegisteredCustomers() {
        List<CustomerRegistration> allProfiles = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("customer_database.txt"))) {
            String line;
            String id = "", name = "", contact = "", email = "", collateral = "", currentStatus = "";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) id = line.replace("Registration ID:", "").trim();
                else if (line.startsWith("Name:"))         name = line.replace("Name:", "").trim();
                else if (line.startsWith("Contact:"))      contact = line.replace("Contact:", "").trim();
                else if (line.startsWith("Email:"))        email = line.replace("Email:", "").trim();
                else if (line.startsWith("Collateral:"))   collateral = line.replace("Collateral:", "").trim();
                else if (line.startsWith("Status:"))       currentStatus = line.replace("Status:", "").trim();
                
                if (line.equals("=========================================") && !id.isEmpty()) {
                    CustomerRegistration profile = new CustomerRegistration(id, name, contact, email, collateral, currentStatus);
                    allProfiles.add(profile);
                    id = ""; name = ""; contact = ""; email = ""; collateral = ""; currentStatus = "";
                }
            }
        } catch (IOException e) {
        }
        return allProfiles;
    }

    public CustomerRegistration searchCustomerByID(String targetID) {
        if (targetID == null) return null;
        List<CustomerRegistration> allCustomers = getAllRegisteredCustomers();
        for (CustomerRegistration customer : allCustomers) {
            if (customer.getRegistrationID().equalsIgnoreCase(targetID.trim())) {
                return customer;
            }
        }
        return null;
    }

    public List<CustomerRegistration> searchCustomersByName(String partialName) {
        List<CustomerRegistration> matchingResults = new ArrayList<>();
        if (partialName == null || partialName.trim().isEmpty()) return matchingResults;
        
        List<CustomerRegistration> allCustomers = getAllRegisteredCustomers();
        for (CustomerRegistration customer : allCustomers) {
            String nameInRecord = customer.getCustomerNames()[0].toLowerCase();
            if (nameInRecord.contains(partialName.trim().toLowerCase())) {
                matchingResults.add(customer);
            }
        }
        return matchingResults;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CustomerRegistration registrationSystem = new CustomerRegistration();
        boolean running = true;
        
        System.out.println("=== Local Lender Management: Customer Registration System ===");
        
        while (running) {
            System.out.println("\n--- Enter Details for New Customer ---");
            System.out.print("Enter Customer Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Contact Number: ");
            String contact = scanner.nextLine();
            System.out.print("Enter Email Address: ");
            String email = scanner.nextLine();
            System.out.print("Enter Collateral Asset Description: ");
            String collateral = scanner.nextLine();
            
            registrationSystem.inputCustomerData(name, contact, email);
            registrationSystem.recordCollateralInfo(collateral);
            
            registrationSystem.saveRegistration();
            System.out.println("\n>> Progress saved to database.");
            
            System.out.print("\nDo you want to register another customer? (yes/no): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("no") || choice.equals("n")) {
                running = false;
            }
        }
        scanner.close();
    }

    public String[] getCustomerNames() { return customerNames; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
    public String getCollateralDescription() { return collateralDescription; }
    public String getRegistrationID() { return registrationID; }
    public String getStatus() { return status; }
}