package com.customerregistration;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RepaymentModuleGUI extends JFrame {
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

    private final JTextArea outputArea = new JTextArea("Repayment summary will appear here.");
    private Repayment repayment;

    public RepaymentModuleGUI() {
        setTitle("Loan Repayment Ledger Management Module");
        // FIXED: Safe dynamic sandbox close behavior execution target to prevent complete app crashes
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(850, 580);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Initialize Schedule Entry"));

        formPanel.add(new JLabel("Repayment ID:")); formPanel.add(repaymentIdField);
        formPanel.add(new JLabel("Loan Reference ID:")); formPanel.add(loanIdField);
        formPanel.add(new JLabel("Customer Target Name:")); formPanel.add(customerNameField);
        formPanel.add(new JLabel("Scheduled Date (YYYY-MM-DD):")); formPanel.add(scheduledDateField);
        formPanel.add(new JLabel("Scheduled Installment Base ($):")); formPanel.add(scheduledAmountField);
        formPanel.add(new JLabel("Structural Penalty Rate (e.g. 0.05):")); formPanel.add(penaltyRateField);
        formPanel.add(new JLabel("Operational Amount Paid ($):")); formPanel.add(amountPaidField);
        formPanel.add(new JLabel("Actual Processing Payment Date (YYYY-MM-DD):")); formPanel.add(paymentDateField);

        JButton createRecordBtn = new JButton("Initialize Ledger Target");
        formPanel.add(createRecordBtn);
        mainPanel.add(formPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JPanel statePanel = new JPanel(new GridLayout(3, 2, 8, 8));
        statePanel.setBorder(BorderFactory.createTitledBorder("System Computed Status Monitor"));

        penaltyAmountField.setEditable(false); totalDueField.setEditable(false); statusField.setEditable(false);
        statePanel.add(new JLabel("Computed Penalty Charge:")); statePanel.add(penaltyAmountField);
        statePanel.add(new JLabel("Adjusted Ledger Due Total:")); statePanel.add(totalDueField);
        statePanel.add(new JLabel("Realtime Status Verification:")); statePanel.add(statusField);
        rightPanel.add(statePanel);

        rightPanel.add(Box.createVerticalStrut(10));

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Console Output View"));
        rightPanel.add(scrollPane);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new BorderLayout(5, 5));
        JPanel logicButtons = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton processPaymentBtn = new JButton("Apply Execution Receipt");
        JButton showHistoryBtn = new JButton("Log History Out");
        JButton clearFormBtn = new JButton("Reset Panel");
        logicButtons.add(processPaymentBtn); logicButtons.add(showHistoryBtn); logicButtons.add(clearFormBtn);
        actionsPanel.add(logicButtons, BorderLayout.CENTER);

        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        add(mainPanel);

        DocumentListener realtimeListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { triggerRealtimeRecalculate(); }
            public void removeUpdate(DocumentEvent e) { triggerRealtimeRecalculate(); }
            public void changedUpdate(DocumentEvent e) { triggerRealtimeRecalculate(); }
        };
        paymentDateField.getDocument().addDocumentListener(realtimeListener);

        createRecordBtn.addActionListener(e -> handleInitializeLedger());
        processPaymentBtn.addActionListener(e -> handleProcessReceipt());
        showHistoryBtn.addActionListener(e -> handlePrintHistory());
        clearFormBtn.addActionListener(e -> clearForm());
    }

    private void triggerRealtimeRecalculate() {
        if (repayment == null) return;
        String pDate = paymentDateField.getText().trim();
        if (pDate.length() == 10) {
            try {
                repayment.calculatePenalties(pDate);
                updateStatusFields();
            } catch (Exception ignored) {}
        }
    }

    private void handleInitializeLedger() {
        try {
            String rId = repaymentIdField.getText().trim();
            String lId = loanIdField.getText().trim();
            String name = customerNameField.getText().trim();
            String sDate = scheduledDateField.getText().trim();

            double sAmt = parseDoubleOrThrow(scheduledAmountField.getText().trim(), "Scheduled Amount");
            double pRate = parseDoubleOrThrow(penaltyRateField.getText().trim(), "Penalty Rate");

            LocalDate.parse(sDate);

            repayment = new Repayment(rId, lId, name, sDate, sAmt, pRate);
            updateStatusFields();
            outputArea.setText("Repayment target initialized successfully in volatile container.\nReady for processing transactions.");
        } catch (DateTimeParseException ex) {
            showError("Scheduled date structure must strictly align to YYYY-MM-DD template formatting constraints.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleProcessReceipt() {
        if (repayment == null) {
            showError("No ledger target entity currently tracked. Initialize base operational framework mapping parameters first.");
            return;
        }
        try {
            double amtPaid = parseDoubleOrThrow(amountPaidField.getText().trim(), "Amount Paid");
            String pDate = paymentDateField.getText().trim();
            LocalDate.parse(pDate);

            repayment.processPayment(amtPaid, pDate);
            updateStatusFields();

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.io.PrintStream ps = new java.io.PrintStream(baos);
            java.io.PrintStream old = System.out;
            System.setOut(ps);
            repayment.displayRepaymentSummary();
            System.out.flush();
            System.setOut(old);
            outputArea.setText(baos.toString());

            JOptionPane.showMessageDialog(this, "Receipt committed. Repayment successfully synchronized to ledger metrics.", "Processing Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (DateTimeParseException ex) {
            showError("Execution process requires a standard confirmation timeline matching YYYY-MM-DD pattern validation parameters.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handlePrintHistory() {
        if (repayment == null) {
            showError("Operation failed. Structural tracking entity is blank.");
            return;
        }
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        java.io.PrintStream old = System.out;
        System.setOut(ps);
        repayment.displayRepaymentHistory();
        System.out.flush();
        System.setOut(old);
        outputArea.setText(baos.toString());
    }

    private void updateStatusFields() {
        if (repayment == null) return;
        penaltyAmountField.setText(formatMoney(repayment.getPenaltyAmount()));
        totalDueField.setText(formatMoney(repayment.getTotalAmountDue()));
        statusField.setText(repayment.getRepaymentStatus());
    }

    private double parseDoubleOrThrow(String value, String fieldName) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    private String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }

    private void clearForm() {
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
        outputArea.setText("Repayment summary will appear here.");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation/Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ignored) {
        }
        SwingUtilities.invokeLater(() -> new RepaymentModuleGUI().setVisible(true));
    }
}