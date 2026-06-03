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
    public static final String REGISTRATION = "Registration";
    public static final String MODIFICATION = "Modification";

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

    // SCANS DATABASE FOR THE REGISTRATION STATUS AND ASSIGNED ACTIVITIES
    private void loadActivitiesFromDatabase() {
        this.activityList.clear();
        File dbFile = new File(DATABASE_FILE);
        if (!dbFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            boolean matchFound = false;
            String currentId = "";
            String currentName = "";
            String currentStep = "";

            SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat defaultTimeFormat = new SimpleDateFormat("HH:mm");
            String currentDate = defaultDateFormat.format(new Date());
            String currentTime = defaultTimeFormat.format(new Date());

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) {
                    currentId = line.replace("Registration ID:", "").trim();
                    matchFound = currentId.equalsIgnoreCase(this.customerId);
                } else if (line.startsWith("Name:")) {
                    currentName = line.replace("Name:", "").trim();
                } else if (line.startsWith("Current Step:")) {
                    currentStep = line.replace("Current Step:", "").trim();
                } else if (line.startsWith("=========================================")) {
                    // When a customer block concludes, document their records if it matches
                    if (matchFound && !currentId.isEmpty()) {
                        // Generate automatic initial registration activity event log entry
                        activityList.add(buildActivityEntry(REGISTRATION, currentDate, currentTime, 
                                "Initial onboarding baseline profile recorded. Workspace Step: " + currentStep, "SUCCESS"));
                    }
                    // Reset single record scan space
                    matchFound = false;
                    currentId = "";
                } else if (line.contains("|") && matchFound) {
                    // Extract inline custom appended transaction activity logs matching current customer context
                    activityList.add(line);
                }
            }
            // Capture trailing unclosed text block data sets
            if (matchFound && !currentId.isEmpty()) {
                activityList.add(buildActivityEntry(REGISTRATION, currentDate, currentTime, 
                        "Initial onboarding baseline profile recorded. Workspace Step: " + currentStep, "SUCCESS"));
            }
        } catch (IOException e) {
            System.out.println("Exception parsing text database files context tracking logs: " + e.getMessage());
        }
    }

    public void addActivity(String activityType, String date, String time, String description) {
        String packedEntry = buildActivityEntry(activityType, date, time, description, "COMPLETED");
        this.activityList.add(packedEntry);
        writeActivityToDatabaseFile(packedEntry);
    }

    private void writeActivityToDatabaseFile(String packedEntry) {
        File dbFile = new File(DATABASE_FILE);
        if (!dbFile.exists()) return;

        ArrayList<String> fileLinesBuffer = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLinesBuffer.add(line);
            }
        } catch (IOException e) {
            return;
        }

        // Injects standard custom transaction lines right under the customer's ID block space
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, false))) {
            for (String fileLine : fileLinesBuffer) {
                writer.write(fileLine);
                writer.newLine();
                if (fileLine.trim().startsWith("Registration ID:") && 
                    fileLine.trim().replace("Registration ID:", "").trim().equalsIgnoreCase(this.customerId)) {
                    writer.write(packedEntry);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Exception writing activity: " + e.getMessage());
        }
    }

    public String getFormattedHistory() {
        if (activityList.isEmpty()) {
            return "No historical transaction history files logged for this system file key.";
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<String> chronologicallySorted = sortChronologically(activityList);
        for (String record : chronologicallySorted) {
            sb.append(formatEntryForDisplay(record)).append("\n");
        }
        return sb.toString();
    }

    public String getFormattedFilteredHistory(String type) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> chronologicallySorted = sortChronologically(activityList);
        int hitsCount = 0;
        for (String record : chronologicallySorted) {
            if (record.startsWith(type)) {
                sb.append(formatEntryForDisplay(record)).append("\n");
                hitsCount++;
            }
        }
        if (hitsCount == 0) {
            return "No specific entries found under transaction type: " + type;
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