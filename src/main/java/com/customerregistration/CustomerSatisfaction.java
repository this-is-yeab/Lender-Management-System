package com.customerregistration;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomerSatisfaction {

    public static class QualityLogEngine {
        private static final double MAX_LOAN_TO_COLLATERAL_RATIO = 0.800; 
        private static final double LOYALTY_DISCOUNT = 0.005;

        private String borrowerName;
        private double currentLoanBalance;
        private double collateralValue;    
        private double savedAmount;        
        private String customerFeedback;
        private int satisfactionScore;     

        public QualityLogEngine(String borrowerName, double currentLoanBalance, double collateralValue, double savedAmount, String customerFeedback, int satisfactionScore) {
            this.borrowerName = borrowerName;
            this.currentLoanBalance = currentLoanBalance;
            this.collateralValue = collateralValue;
            this.savedAmount = savedAmount;
            this.customerFeedback = customerFeedback;
            this.satisfactionScore = satisfactionScore;
        }
        
        public double loanToCollateralRatioCalculator() {
            if (this.collateralValue <= 0) {
                return 0.000;
            }
            return this.currentLoanBalance / this.collateralValue;
        }

        public void applyLoyaltyDiscountRate(double currentRate) {
            if (this.savedAmount > 50000 && this.satisfactionScore >= 4) {
                double adjustedRate = currentRate - LOYALTY_DISCOUNT;
                System.out.println("Loyalty Rate Adjusted to: " + adjustedRate);
            }
        }

        public String getFinancialHealthStatus() {
            double ratio = loanToCollateralRatioCalculator();
            if (ratio > MAX_LOAN_TO_COLLATERAL_RATIO) {
                return "Critical";
            } else if (ratio > 0.650) {
                return "Stressed";
            } else {
                return "Healthy";
            }
        }

        public String getBorrowerName() { return borrowerName; }
        public double getCurrentLoanBalance() { return currentLoanBalance; }
        public double getCollateralValue() { return collateralValue; }
        public double getSavedAmount() { return savedAmount; }
        public String getCustomerFeedback() { return customerFeedback; }
        public int getSatisfactionScore() { return satisfactionScore; }
    }
}

class CustomerSatisfactionDashboard extends JFrame {
    private CustomerSatisfaction.QualityLogEngine currentCustomer;
    private DefaultTableModel tableModel;
    
    // Dynamic choice component and database memory lists
    private JComboBox<String> cmbPendingCustomers;
    private ArrayList<String> customerNamesList = new ArrayList<>();
    private ArrayList<Double> customerBalancesList = new ArrayList<>();
    private ArrayList<Double> customerCollateralsList = new ArrayList<>();

    private JTextField txtLoan, txtCollateral, txtSavings, txtFeedback;
    private JComboBox<Integer> cmbScore;
    private JLabel lblRatioVal, lblStatusVal;

    public CustomerSatisfactionDashboard() {
        setTitle("Customer Satisfaction & Portfolio Risk Analyzer");
        setSize(950, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        buildLayout();
        loadCustomerDatabaseProfiles();
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(9, 2, 6, 6));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Log Quality Review"));

        // Replacing old manual text box with the new dynamic database selector lookup menu
        pnlForm.add(new JLabel("Target Lookup Profile:"));
        cmbPendingCustomers = new JComboBox<>();
        cmbPendingCustomers.addActionListener(e -> syncFormWithSelectedCustomer());
        pnlForm.add(cmbPendingCustomers);

        pnlForm.add(new JLabel("Current Loan Balance ($):")); 
        txtLoan = new JTextField(); 
        pnlForm.add(txtLoan);
        
        pnlForm.add(new JLabel("Assessed Collateral Value ($):")); 
        txtCollateral = new JTextField(); 
        pnlForm.add(txtCollateral);
        
        pnlForm.add(new JLabel("Customer Savings Balance ($):")); 
        txtSavings = new JTextField("0.00"); 
        pnlForm.add(txtSavings);
        
        pnlForm.add(new JLabel("General Feedback Text:")); 
        txtFeedback = new JTextField(); 
        pnlForm.add(txtFeedback);
        
        pnlForm.add(new JLabel("Satisfaction Score Index:"));
        cmbScore = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        pnlForm.add(cmbScore);

        pnlForm.add(new JLabel("Loan-to-Collateral Ratio:")); 
        lblRatioVal = new JLabel("0.000"); 
        pnlForm.add(lblRatioVal);
        
        pnlForm.add(new JLabel("Financial Portfolio Risk:")); 
        lblStatusVal = new JLabel("Unknown"); 
        pnlForm.add(lblStatusVal);

        add(pnlForm, BorderLayout.WEST);

        String[] cols = {"Name", "Loan Bal", "Collateral", "Savings", "Score", "Feedback", "LTV Ratio", "Health Status"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable tblData = new JTable(tableModel);
        add(new JScrollPane(tblData), BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCalculate = new JButton("Run Metric Diagnostic Check");
        pnlSouth.add(btnCalculate);
        add(pnlSouth, BorderLayout.SOUTH);

        btnCalculate.addActionListener(e -> {
            try {
                String name = (String) cmbPendingCustomers.getSelectedItem();
                if (name == null || customerNamesList.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No valid target customer profile selected.");
                    return;
                }
                
                // Keep name clean of selection meta tag brackets
                if (name.contains("(")) {
                    name = name.substring(0, name.indexOf("(")).trim();
                }

                double loan = Double.parseDouble(txtLoan.getText().trim());
                double col = Double.parseDouble(txtCollateral.getText().trim());
                double sav = Double.parseDouble(txtSavings.getText().trim());
                String feed = txtFeedback.getText().trim();
                int score = (Integer) cmbScore.getSelectedItem();

                currentCustomer = new CustomerSatisfaction.QualityLogEngine(name, loan, col, sav, feed, score);
                updateDashboardLabels();
                refreshLogTable();
                saveToSatisfactionDatabaseFile();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please check numeric data entries structure validation formulas.");
            }
        });
    }

    /**
     * Loops through customer_database.txt and aggregates the data blocks dynamically
     */
    private void loadCustomerDatabaseProfiles() {
        cmbPendingCustomers.removeAllItems();
        customerNamesList.clear();
        customerBalancesList.clear();
        customerCollateralsList.clear();

        File dbFile = new File("customer_database.txt");
        if (!dbFile.exists()) {
            cmbPendingCustomers.addItem("No Database File Located");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            String currentId = "";
            String currentName = "";
            double currentBalance = 0.0;
            double currentCollateral = 0.0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) {
                    currentId = line.replace("Registration ID:", "").trim();
                } else if (line.startsWith("Name:")) {
                    currentName = line.replace("Name:", "").trim();
                } else if (line.startsWith("Balance:")) {
                    String cleanBal = line.replace("Balance:", "").replace("$", "").trim();
                    currentBalance = Double.parseDouble(cleanBal);
                } else if (line.startsWith("Est. Value:")) {
                    String cleanVal = line.replace("Est. Value:", "").replace("$", "").trim();
                    currentCollateral = Double.parseDouble(cleanVal);
                } else if (line.startsWith("=========================================")) {
                    if (!currentId.isEmpty() && !currentName.isEmpty()) {
                        cmbPendingCustomers.addItem(currentName + " (" + currentId + ")");
                        customerNamesList.add(currentName);
                        customerBalancesList.add(currentBalance);
                        customerCollateralsList.add(currentCollateral);
                    }
                    currentId = ""; currentName = ""; currentBalance = 0.0; currentCollateral = 0.0;
                }
            }
            
            // Check for a trailing block configuration that didn't end with a divider row
            if (!currentId.isEmpty() && !currentName.isEmpty()) {
                cmbPendingCustomers.addItem(currentName + " (" + currentId + ")");
                customerNamesList.add(currentName);
                customerBalancesList.add(currentBalance);
                customerCollateralsList.add(currentCollateral);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error parsing database variables: " + e.getMessage());
        }

        if (cmbPendingCustomers.getItemCount() == 0) {
            cmbPendingCustomers.addItem("No Profiles Tracked");
        }
    }

    /**
     * Instantly grabs the parsed values and drops them directly into the visual inputs
     */
    private void syncFormWithSelectedCustomer() {
        int idx = cmbPendingCustomers.getSelectedIndex();
        if (idx < 0 || customerNamesList.isEmpty() || idx >= customerNamesList.size()) {
            txtLoan.setText("");
            txtCollateral.setText("");
            return;
        }

        txtLoan.setText(String.format("%.2f", customerBalancesList.get(idx)));
        txtCollateral.setText(String.format("%.2f", customerCollateralsList.get(idx)));
        txtSavings.setText("0.00"); 
        txtFeedback.setText("");
    }

    private void updateDashboardLabels() {
        if (currentCustomer == null) return;
        lblRatioVal.setText(String.format("%.3f", currentCustomer.loanToCollateralRatioCalculator()));

        String status = currentCustomer.getFinancialHealthStatus();
        lblStatusVal.setText(status);

        if (status.equals("Critical")) {
            lblStatusVal.setForeground(Color.RED);
        } else if (status.equals("Stressed")) {
            lblStatusVal.setForeground(Color.ORANGE);
        } else {
            lblStatusVal.setForeground(new Color(39, 174, 96));
        }
    }

    private void refreshLogTable() {
        if (currentCustomer == null) return;
        Object[] row = {
                currentCustomer.getBorrowerName(),
                String.format("%.3f", currentCustomer.getCurrentLoanBalance()),
                String.format("%.3f", currentCustomer.getCollateralValue()),
                String.format("%.3f", currentCustomer.getSavedAmount()),
                currentCustomer.getSatisfactionScore(),
                currentCustomer.getCustomerFeedback(),
                String.format("%.3f", currentCustomer.loanToCollateralRatioCalculator()),
                currentCustomer.getFinancialHealthStatus()
        };
        tableModel.addRow(row);
    }

    /**
     * Saves processed data to your dedicated customer satisfaction database file
     */
    private void saveToSatisfactionDatabaseFile() {
        if (currentCustomer == null) return;

        File satFile = new File("customer_satisfaction_database.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(satFile, true))) {
            writer.write("Customer Review Record");
            writer.newLine();
            writer.write("Name: " + currentCustomer.getBorrowerName());
            writer.newLine();
            writer.write("Current Loan Balance: $" + String.format("%.2f", currentCustomer.getCurrentLoanBalance()));
            writer.newLine();
            writer.write("Assessed Collateral Value: $" + String.format("%.2f", currentCustomer.getCollateralValue()));
            writer.newLine();
            writer.write("Customer Savings Balance: $" + String.format("%.2f", currentCustomer.getSavedAmount()));
            writer.newLine();
            writer.write("Satisfaction Score Index: " + currentCustomer.getSatisfactionScore());
            writer.newLine();
            writer.write("General Feedback Text: " + (currentCustomer.getCustomerFeedback().isEmpty() ? "None Provided" : currentCustomer.getCustomerFeedback()));
            writer.newLine();
            writer.write("Loan-to-Collateral Ratio: " + String.format("%.3f", currentCustomer.loanToCollateralRatioCalculator()));
            writer.newLine();
            writer.write("Financial Portfolio Risk Status: " + currentCustomer.getFinancialHealthStatus());
            writer.newLine();
            writer.write("=========================================");
            writer.newLine();
            
            JOptionPane.showMessageDialog(this, "Survey logs saved to customer_satisfaction_database.txt!", "Satisfaction Record Appended", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failure trying to log transaction: " + e.getMessage(), "File I/O Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerSatisfactionDashboard().setVisible(true));
    }
}