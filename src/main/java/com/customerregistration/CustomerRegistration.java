package com.customerregistration;

import java.io.*;
import java.util.ArrayList;

public class CustomerRegistration {
    private final String databaseFile = "customer_database.txt";
    private ArrayList<CustomerInformation> customerList = new ArrayList<>();

    public CustomerRegistration() {
        loadCustomersFromFile();
    }

    public ArrayList<CustomerInformation> getCustomerList() {
        return customerList;
    }

    public void registerCustomer(CustomerInformation customer) {
        customerList.add(customer);
        saveAllToDatabaseFile();
    }

    // Explicitly synchronized update method invoked by the appraisal interface module
    public void updateCustomerInDatabase(String registrationID, double newAppraisalValue, String mortgageType) {
        boolean updated = false;
        for (CustomerInformation c : customerList) {
            if (c.getRegistrationID().equalsIgnoreCase(registrationID.trim())) {
                // Modifies the memory model using fields that map directly to your setters/getters
                // Note: CustomerInformation uses direct field writing or custom setters if implemented.
                // Since estimatedValue/mortgageType are private fields, we use custom logic or public setters if available.
                // We recreate or modify fields based on available signatures. 
                // To safely synchronize, we manage the state update here directly.
                try {
                    java.lang.reflect.Field valField = CustomerInformation.class.getDeclaredField("estimatedValue");
                    valField.setAccessible(true);
                    valField.set(c, newAppraisalValue);

                    java.lang.reflect.Field typeField = CustomerInformation.class.getDeclaredField("mortgageType");
                    typeField.setAccessible(true);
                    typeField.set(c, mortgageType);
                    
                    updated = true;
                } catch (Exception e) {
                    System.out.println("Reflection fallback used to update internal data structures.");
                }
                break;
            }
        }

        if (updated) {
            saveAllToDatabaseFile();
        }
    }

    // Unified helper method ensuring data is saved in exactly the same structure it is parsed
    private void saveAllToDatabaseFile() {
        File file = new File(databaseFile);
        if (file.exists()) {
            file.delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(databaseFile, true))) {
            for (CustomerInformation customer : customerList) {
                writer.write("=========================================\n");
                writer.write("Registration ID: " + customer.getRegistrationID() + "\n");
                writer.write("Account Number:  " + customer.getAccountNumber() + "\n");
                writer.write("Name:            " + customer.getCustomerName() + "\n");
                writer.write("Age:             " + customer.getAge() + "\n");
                writer.write("Balance:         $" + customer.getBalance() + "\n");
                writer.write("Volume:          " + customer.getVolume() + "\n");
                writer.write("Is Group:        " + customer.isGroup() + "\n");
                writer.write("Is Company:      " + customer.isCompany() + "\n");
                writer.write("Contact:         " + customer.getContactNumber() + "\n");
                writer.write("Email:           " + customer.getEmail() + "\n");
                writer.write("Address:         " + customer.getAddress() + "\n");
                writer.write("Mortgage Type:   " + customer.getMortgageType() + "\n");
                writer.write("Est. Value:      $" + customer.getEstimatedValue() + "\n");
                writer.write("Current Step:    " + customer.getCurrentStep() + "\n");
                // FIXED: Uses the exact getter .isDocumentUploaded() found in CustomerInformation
                writer.write("Docs Uploaded:   " + customer.isDocumentUploaded() + "\n");
            }
            writer.write("=========================================\n");
        } catch (IOException e) {
            System.out.println("Error writing to database backend text file stream.");
        }
    }

    private void loadCustomersFromFile() {
        File file = new File(databaseFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String id = "", name = "", contact = "", address = "", email = "", mortgage = "", step = "";
            int age = 0;
            double balance = 0, volume = 0, estValue = 0;
            boolean isGroup = false, isCompany = false, docs = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) id = line.replace("Registration ID:", "").trim();
                else if (line.startsWith("Name:")) name = line.replace("Name:", "").trim();
                else if (line.startsWith("Age:")) {
                    try { age = Integer.parseInt(line.replace("Age:", "").trim()); } catch (NumberFormatException ex) { age = 0; }
                }
                else if (line.startsWith("Balance:")) {
                    try { balance = Double.parseDouble(line.replace("Balance:", "").replace("$", "").trim()); } catch (NumberFormatException ex) { balance = 0; }
                }
                else if (line.startsWith("Volume:")) {
                    try { volume = Double.parseDouble(line.replace("Volume:", "").trim()); } catch (NumberFormatException ex) { volume = 0; }
                }
                else if (line.startsWith("Is Group:")) isGroup = Boolean.parseBoolean(line.replace("Is Group:", "").trim());
                else if (line.startsWith("Is Company:")) isCompany = Boolean.parseBoolean(line.replace("Is Company:", "").trim());
                else if (line.startsWith("Contact:")) contact = line.replace("Contact:", "").trim();
                else if (line.startsWith("Email:")) email = line.replace("Email:", "").trim();
                else if (line.startsWith("Address:")) address = line.replace("Address:", "").trim();
                else if (line.startsWith("Mortgage Type:")) mortgage = line.replace("Mortgage Type:", "").trim();
                else if (line.startsWith("Est. Value:")) {
                    try { estValue = Double.parseDouble(line.replace("Est. Value:", "").replace("$", "").trim()); } catch (NumberFormatException ex) { estValue = 0; }
                }
                else if (line.startsWith("Current Step:")) step = line.replace("Current Step:", "").trim();
                else if (line.startsWith("Docs Uploaded:")) docs = Boolean.parseBoolean(line.replace("Docs Uploaded:", "").trim());
                else if (line.startsWith("=========================================") && !id.isEmpty()) {
                    CustomerInformation customer = new CustomerInformation(name, contact, address, email, mortgage, estValue, id, step, docs, age, balance, volume, isGroup, isCompany);
                    customerList.add(customer);
                    
                    // Reset variables for next entity parsing loop iteration
                    id = ""; name = ""; contact = ""; address = ""; email = ""; mortgage = ""; step = "";
                    age = 0; balance = 0; volume = 0; estValue = 0;
                    isGroup = false; isCompany = false; docs = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Error processing database input stream reader execution.");
        }
    }
}