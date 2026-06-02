package com.customerregistration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class CustomerActivityHistory {

    public static final String DATABASE_FILE = "customer_database.txt";

    public static final String LOAN_APPLICATION = "Loan Applied";
    public static final String LOAN_REJECTION = "Loan Rejected";
    public static final String LOAN_DISBURSEMENT = "Loan Disbursed";
    public static final String REPAYMENT = "Repayment Made";
    public static final String MISSED_PAYMENT = "Missed Payment";

    private String customerId;
    private String[] customerNames;
    private ArrayList<String> activityList;

    public CustomerActivityHistory(String customerId, String[] customerNames) {
        this.customerId = customerId;
        this.customerNames = customerNames;
        this.activityList = new ArrayList<>();
        loadActivitiesFromDatabase();
    }

    public static CustomerActivityHistory forCustomer(String id, String name) {
        return new CustomerActivityHistory(id, new String[]{name});
    }

    public void addActivity(String type, String date, String time, String description) {
        String entry = buildActivityEntry(type, date, time, description, "Processed");
        activityList.add(entry);
        saveActivityToDatabase(entry);
    }

    private void saveActivityToDatabase(String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE, true))) {
            writer.write("--- ACTIVITY_LOG ---" + "\n");
            writer.write("CustID: " + this.customerId + "\n");
            writer.write("LogPayload: " + entry + "\n");
            writer.write("--------------------" + "\n");
        } catch (IOException e) {
            System.out.println("Error writing history tracking entry log: " + e.getMessage());
        }
    }

    private void loadActivitiesFromDatabase() {
        File file = new File(DATABASE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentRecordCustId = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("CustID:")) {
                    currentRecordCustId = line.replace("CustID:", "").trim();
                } else if (line.startsWith("LogPayload:") && currentRecordCustId.equalsIgnoreCase(this.customerId)) {
                    String payload = line.replace("LogPayload:", "").trim();
                    activityList.add(payload);
                }
            }
        } catch (IOException e) {
            System.out.println("Parsing error loading customer timeline activities: " + e.getMessage());
        }
    }

    public String getFormattedHistory() {
        if (activityList.isEmpty()) {
            return "No registered logs found for customer matching structural profile reference identification: " + customerId;
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<String> sorted = sortChronologically(activityList);
        for (String entry : sorted) {
            sb.append(formatEntryForDisplay(entry)).append("\n");
        }
        return sb.toString();
    }

    public String getFormattedFilteredHistory(String type) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> sorted = sortChronologically(activityList);
        int matchedCount = 0;
        for (String entry : sorted) {
            if (entry.startsWith(type + "|")) {
                sb.append(formatEntryForDisplay(entry)).append("\n");
                matchedCount++;
            }
        }
        if (matchedCount == 0) {
            return "No tracking metrics found matching structural category criteria: " + type;
        }
        return sb.toString();
    }

    private static String buildActivityEntry(String activityType, String date, String time, String description, String status) {
        return activityType + "|" + date + "|" + time + "|" + description + "|" + status;
    }

    private static String formatEntryForDisplay(String entry) {
        String[] parts = entry.split("\\|");
        if (parts.length < 5) {
            return entry;
        }
        return "[" + parts[1] + " " + parts[2] + "] Action: " + parts[0] + " - Description: " + parts[3] + " (" + parts[4] + ")";
    }

    private static ArrayList<String> sortChronologically(ArrayList<String> entries) {
        ArrayList<String> sorted = new ArrayList<>(entries);
        Collections.sort(sorted, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                Date dateA = parseDateTime(a);
                Date dateB = parseDateTime(b);
                return dateA.compareTo(dateB);
            }
        });
        return sorted;
    }

    private static Date parseDateTime(String entry) {
        String[] parts = entry.split("\\|");
        if (parts.length < 3) {
            return new Date(0);
        }
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            return dateTimeFormat.parse(parts[1] + " " + parts[2]);
        } catch (ParseException e) {
            return new Date(0);
        }
    }
}