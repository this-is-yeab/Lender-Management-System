package com.customerregistration;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JTextField txtName, txtLoan, txtCollateral, txtSavings, txtFeedback;
    private JComboBox<Integer> cmbScore;
    private JLabel lblRatioVal, lblStatusVal;

    public CustomerSatisfactionDashboard() {
        setTitle("Customer Satisfaction & Portfolio Risk Analyzer");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildLayout();
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(8, 2, 6, 6));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Log Quality Review"));

        pnlForm.add(new JLabel("Borrower Full Name:")); txtName = new JTextField(); pnlForm.add(txtName);
        pnlForm.add(new JLabel("Current Loan Balance ($):")); txtLoan = new JTextField(); pnlForm.add(txtLoan);
        pnlForm.add(new JLabel("Assessed Collateral Value ($):")); txtCollateral = new JTextField(); pnlForm.add(txtCollateral);
        pnlForm.add(new JLabel("Customer Savings Balance ($):")); txtSavings = new JTextField(); pnlForm.add(txtSavings);
        pnlForm.add(new JLabel("General Feedback Text:")); txtFeedback = new JTextField(); pnlForm.add(txtFeedback);
        
        pnlForm.add(new JLabel("Satisfaction Score Index:"));
        cmbScore = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        pnlForm.add(cmbScore);

        pnlForm.add(new JLabel("Loan-to-Collateral Ratio:")); lblRatioVal = new JLabel("0.000"); pnlForm.add(lblRatioVal);
        pnlForm.add(new JLabel("Financial Portfolio Risk:")); lblStatusVal = new JLabel("Unknown"); pnlForm.add(lblStatusVal);

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
                String name = txtName.getText().trim();
                double loan = Double.parseDouble(txtLoan.getText().trim());
                double col = Double.parseDouble(txtCollateral.getText().trim());
                double sav = Double.parseDouble(txtSavings.getText().trim());
                String feed = txtFeedback.getText().trim();
                int score = (Integer) cmbScore.getSelectedItem();

                currentCustomer = new CustomerSatisfaction.QualityLogEngine(name, loan, col, sav, feed, score);
                updateDashboardLabels();
                refreshLogTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please check numeric data entries.");
            }
        });
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerSatisfactionDashboard().setVisible(true));
    }
}