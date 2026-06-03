package com.customerregistration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class RepaymentModuleGUI extends JFrame {
    private JComboBox<String> customerLookupCombo;
    private final ArrayList<String> dbCustomerIds = new ArrayList<>();
    private final ArrayList<String> dbCustomerNames = new ArrayList<>();
    private final ArrayList<Double> dbCustomerBalances = new ArrayList<>();

    private final JTextField repaymentIdField = new JTextField();
    private final JTextField loanIdField = new JTextField();
    private final JTextField customerNameField = new JTextField();
    private final JTextField scheduledDateField = new JTextField();
    private final JTextField scheduledAmountField = new JTextField();
    private final JTextField penaltyRateField = new JTextField();
    private final JTextField amountPaidField = new JTextField();
    private final JTextField paymentDateField = new JTextField();

    private final JTextField penaltyAmountField = new JTextField();
    private final JTextField totalDueField = new JTextField();
    private final JTextField statusField = new JTextField();

    private final JTextArea outputArea = new JTextArea();
    private Repayment repayment;

    public RepaymentModuleGUI() {
        setTitle("Loan Repayment Processing Terminal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);

        initComponents();
        loadActiveDatabaseProfiles();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Database Profile Selector"));
        topPanel.add(new JLabel("Select Profile Account:"));
        customerLookupCombo = new JComboBox<>();
        customerLookupCombo.setPreferredSize(new Dimension(300, 25));
        customerLookupCombo.addActionListener(e -> syncFormWithSelectedDatabaseCustomer());
        topPanel.add(customerLookupCombo);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(11, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Repayment Structure Parameters"));

        formPanel.add(new JLabel("Repayment Transaction ID:"));
        formPanel.add(repaymentIdField);
        formPanel.add(new JLabel("Associated Loan Contract ID:"));
        formPanel.add(loanIdField);
        formPanel.add(new JLabel("Borrower Registered Name:"));
        customerNameField.setEditable(false);
        formPanel.add(customerNameField);
        formPanel.add(new JLabel("Scheduled Expiry Date (YYYY-MM-DD):"));
        formPanel.add(scheduledDateField);
        formPanel.add(new JLabel("Scheduled Statement Balance ($):"));
        scheduledAmountField.setEditable(false);
        formPanel.add(scheduledAmountField);
        formPanel.add(new JLabel("Penalty Accumulation Rate (%):"));
        formPanel.add(penaltyRateField);
        formPanel.add(new JLabel("Execution Processing Amount Paid ($):"));
        formPanel.add(amountPaidField);
        formPanel.add(new JLabel("Payment Settlement Date (YYYY-MM-DD):"));
        formPanel.add(paymentDateField);

        formPanel.add(new JLabel("Calculated Overdue Penalty ($):"));
        penaltyAmountField.setEditable(false);
        formPanel.add(penaltyAmountField);
        formPanel.add(new JLabel("Total Consolidated Amount Due ($):"));
        totalDueField.setEditable(false);
        formPanel.add(totalDueField);
        formPanel.add(new JLabel("Calculated Statement Status:"));
        statusField.setEditable(false);
        formPanel.add(statusField);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Live Ledger Receipt Summary Console"));
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton processButton = new JButton("Commit Settlement Payment to DB");
        processButton.setBackground(new Color(39, 174, 96));
        processButton.setForeground(Color.WHITE);
        processButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton clearButton = new JButton("Reset Form Terminal View");

        actionButtonPanel.add(processButton);
        actionButtonPanel.add(clearButton);
        rightPanel.add(actionButtonPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, formPanel, rightPanel);
        splitPane.setDividerLocation(430);
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);

        DocumentListener recalculationListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { runLiveFormCalculations(); }
            public void removeUpdate(DocumentEvent e) { runLiveFormCalculations(); }
            public void changedUpdate(DocumentEvent e) { runLiveFormCalculations(); }
        };
        scheduledAmountField.getDocument().addDocumentListener(recalculationListener);
        penaltyRateField.getDocument().addDocumentListener(recalculationListener);
        amountPaidField.getDocument().addDocumentListener(recalculationListener);
        scheduledDateField.getDocument().addDocumentListener(recalculationListener);
        paymentDateField.getDocument().addDocumentListener(recalculationListener);

        processButton.addActionListener(e -> commitProcessedPaymentToDatabaseFile());
        clearButton.addActionListener(e -> clearFormFieldsView());
        clearFormFieldsView();
    }

    private void loadActiveDatabaseProfiles() {
        customerLookupCombo.removeAllItems();
        dbCustomerIds.clear();
        dbCustomerNames.clear();
        dbCustomerBalances.clear();

        File file = new File("customer_database.txt");
        if (!file.exists()) {
            customerLookupCombo.addItem("No Database File Located");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String id = "";
            String name = "";
            double balance = 0.0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) {
                    id = line.replace("Registration ID:", "").trim();
                } else if (line.startsWith("Name:")) {
                    name = line.replace("Name:", "").trim();
                } else if (line.startsWith("Balance:")) {
                    String rawBal = line.replace("Balance:", "").replace("$", "").trim();
                    balance = Double.parseDouble(rawBal);
                } else if (line.startsWith("=========================================")) {
                    if (!id.isEmpty() && !name.isEmpty()) {
                        customerLookupCombo.addItem(name + " (" + id + ") - Bal: $" + balance);
                        dbCustomerIds.add(id);
                        dbCustomerNames.add(name);
                        dbCustomerBalances.add(balance);
                    }
                    id = ""; name = ""; balance = 0.0;
                }
            }
            if (!id.isEmpty() && !name.isEmpty()) {
                customerLookupCombo.addItem(name + " (" + id + ") - Bal: $" + balance);
                dbCustomerIds.add(id);
                dbCustomerNames.add(name);
                dbCustomerBalances.add(balance);
            }
        } catch (Exception ex) {
            showError("Failure loading database arrays records: " + ex.getMessage());
        }

        if (customerLookupCombo.getItemCount() == 0) {
            customerLookupCombo.addItem("No Registered Account Logs Found");
        }
    }

    private void syncFormWithSelectedDatabaseCustomer() {
        int idx = customerLookupCombo.getSelectedIndex();
        if (idx < 0 || dbCustomerIds.isEmpty() || idx >= dbCustomerIds.size()) return;

        String randomTxId = "TXN-" + (10000 + (int)(Math.random() * 90000));
        String randomLoanId = "LON-" + (10000 + (int)(Math.random() * 90000));

        repaymentIdField.setText(randomTxId);
        loanIdField.setText(randomLoanId);
        customerNameField.setText(dbCustomerNames.get(idx));
        scheduledAmountField.setText(String.format("%.2f", dbCustomerBalances.get(idx)));
        
        scheduledDateField.setText(LocalDate.now().toString());
        paymentDateField.setText(LocalDate.now().toString());
        penaltyRateField.setText("5.0");
        amountPaidField.setText("0.00");
    }

    private void runLiveFormCalculations() {
        try {
            double schedAmt = parseDoubleOrDefault(scheduledAmountField.getText(), 0.0);
            double rate = parseDoubleOrDefault(penaltyRateField.getText(), 0.0) / 100.0;
            double paid = parseDoubleOrDefault(amountPaidField.getText(), 0.0);
            String sDate = scheduledDateField.getText().trim();
            String pDate = paymentDateField.getText().trim();

            if (sDate.isEmpty() || pDate.isEmpty() || schedAmt <= 0) return;

            repayment = new Repayment(
                    repaymentIdField.getText().trim(),
                    loanIdField.getText().trim(),
                    customerNameField.getText().trim(),
                    sDate, 
                    schedAmt, 
                    rate
            );

            // FIXED: Flipped parameters order to match (double amountPaid, String paymentDate) precisely
            repayment.processPayment(paid, pDate);

            penaltyAmountField.setText(formatMoney(repayment.getPenaltyAmount()));
            totalDueField.setText(formatMoney(repayment.getTotalAmountDue()));
            statusField.setText(repayment.getRepaymentStatus());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println("========================================");
            pw.println("     REPAYMENT COMPLIANCE AUDIT RECORD  ");
            pw.println("========================================");
            pw.println("Tx Reference ID : " + repayment.getRepaymentId());
            pw.println("Borrower Target : " + repayment.getCustomerName());
            pw.println("Initial Balance : $" + formatMoney(schedAmt));
            pw.println("Penalty Added   : $" + formatMoney(repayment.getPenaltyAmount()));
            pw.println("Settlement Paid : $" + formatMoney(paid));
            pw.println("Remaining Debt  : $" + formatMoney(repayment.getTotalAmountDue() - paid));
            pw.println("Settlement State: " + repayment.getRepaymentStatus().toUpperCase());
            pw.println("========================================");
            outputArea.setText(sw.toString());

        } catch (Exception ignored) {}
    }

    private void commitProcessedPaymentToDatabaseFile() {
        int idx = customerLookupCombo.getSelectedIndex();
        if (idx < 0 || dbCustomerIds.isEmpty() || repayment == null) {
            showError("Verification Failure: Select a valid profile entry stack target to process tracking logs data.");
            return;
        }

        double paymentValue = parseDoubleOrDefault(amountPaidField.getText(), 0.0);
        if (paymentValue <= 0) {
            showError("Transaction Processing Exception: Payments value payload parameter parameters must be greater than zero.");
            return;
        }

        String targetCustomerId = dbCustomerIds.get(idx);
        double targetRemainingBalance = Math.max(0.00, repayment.getTotalAmountDue() - paymentValue);

        File dbFile = new File("customer_database.txt");
        ArrayList<String> memoryBufferLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            boolean insideTargetBlock = false;
            ArrayList<String> currentBlock = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                currentBlock.add(line);

                if (line.trim().startsWith("Registration ID:")) {
                    String id = line.replace("Registration ID:", "").trim();
                    if (id.equalsIgnoreCase(targetCustomerId)) {
                        insideTargetBlock = true;
                    }
                }

                if (line.trim().startsWith("=========================================")) {
                    if (insideTargetBlock) {
                        for (int i = 0; i < currentBlock.size(); i++) {
                            String checkStr = currentBlock.get(i);
                            if (checkStr.startsWith("Balance:")) {
                                currentBlock.set(i, "Balance: $" + String.format("%.2f", targetRemainingBalance));
                            }
                        }
                    }
                    memoryBufferLines.addAll(currentBlock);
                    currentBlock.clear();
                    insideTargetBlock = false;
                }
            }
            memoryBufferLines.addAll(currentBlock);

        } catch (IOException e) {
            showError("Critical file reading failure descriptor error paths: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, false))) {
            for (String outputStrLine : memoryBufferLines) {
                writer.write(outputStrLine);
                writer.newLine();
            }
        } catch (IOException e) {
            showError("Critical tracking update save failure stream configuration: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Repayment transaction recorded successfully!\nNew remaining balance of $" + String.format("%.2f", targetRemainingBalance) + " committed into customer_database.txt.", "Payment Settlement Finalized", JOptionPane.INFORMATION_MESSAGE);
        
        loadActiveDatabaseProfiles(); 
        clearFormFieldsView();
    }

    private double parseDoubleOrDefault(String value, double defaultVal) {
        try { return Double.parseDouble(value.trim()); } catch (Exception e) { return defaultVal; }
    }

    private String formatMoney(double amount) { return String.format("%.2f", amount); }

    private void clearFormFieldsView() {
        repayment = null;
        repaymentIdField.setText("");
        loanIdField.setText("");
        customerNameField.setText("");
        scheduledDateField.setText("");
        scheduledAmountField.setText("");
        penaltyRateField.setText("");
        amountPaidField.setText("");
        paymentDateField.setText("");
        penaltyAmountField.setText("");
        totalDueField.setText("");
        statusField.setText("");
        outputArea.setText("Select a customer from the dropdown top menu to initialize repayment settlement processing ledger streams.");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "System Ledger Processing Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RepaymentModuleGUI().setVisible(true));
    }
}