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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(databaseFile, true))) {
            writer.write("=========================================\n");
            writer.write("Registration ID: " + customer.getRegistrationID() + "\n");
            writer.write("Account Number:  " + customer.getAccountNumber() + "\n");
            writer.write("Name:            " + customer.getCustomerNames()[0] + "\n");
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
            writer.write("Docs Uploaded:   " + customer.isDocumentUploaded() + "\n");
            writer.write("=========================================\n\n");
        } catch (IOException e) {
            System.out.println("Database Error.");
        }
    }

    private void loadCustomersFromFile() {
        customerList.clear();
        File file = new File(databaseFile);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String id = "", accNum = "", name = "", contact = "", email = "", address = "", mortgage = "", step = "";
            int age = 0;
            double balance = 0, volume = 0, estValue = 0;
            boolean isGroup = false, isCompany = false, docs = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Registration ID: ")) id = line.replace("Registration ID: ", "").trim();
                else if (line.startsWith("Account Number: ")) accNum = line.replace("Account Number: ", "").trim();
                else if (line.startsWith("Name: ")) name = line.replace("Name: ", "").trim();
                else if (line.startsWith("Age: ")) age = Integer.parseInt(line.replace("Age: ", "").trim());
                else if (line.startsWith("Balance: ")) balance = Double.parseDouble(line.replace("Balance: ", "").replace("$", "").trim());
                else if (line.startsWith("Volume: ")) volume = Double.parseDouble(line.replace("Volume: ", "").trim());
                else if (line.startsWith("Is Group: ")) isGroup = Boolean.parseBoolean(line.replace("Is Group: ", "").trim());
                else if (line.startsWith("Is Company: ")) isCompany = Boolean.parseBoolean(line.replace("Is Company: ", "").trim());
                else if (line.startsWith("Contact: ")) contact = line.replace("Contact: ", "").trim();
                else if (line.startsWith("Email: ")) email = line.replace("Email: ", "").trim();
                else if (line.startsWith("Address: ")) address = line.replace("Address: ", "").trim();
                else if (line.startsWith("Mortgage Type: ")) mortgage = line.replace("Mortgage Type: ", "").trim();
                else if (line.startsWith("Est. Value: ")) estValue = Double.parseDouble(line.replace("Est. Value: ", "").replace("$", "").trim());
                else if (line.startsWith("Current Step: ")) step = line.replace("Current Step: ", "").trim();
                else if (line.startsWith("Docs Uploaded: ")) docs = Boolean.parseBoolean(line.replace("Docs Uploaded: ", "").trim());
                else if (line.startsWith("=========================================") && !id.isEmpty()) {
                    CustomerInformation customer = new CustomerInformation(name, contact, address, email, mortgage, estValue, id, step, docs, age, balance, volume, isGroup, isCompany);
                    customerList.add(customer);
                    id = ""; 
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Parsing Error.");
        }
    }
}