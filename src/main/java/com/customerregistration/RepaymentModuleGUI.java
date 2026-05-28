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

    private final JTextArea outputArea = new JTextArea();

    private Repayment repayment;

    public RepaymentModuleGUI() {
        setTitle("Local Lender Management System - Repayment Module");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 650);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(14, 14));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 248, 245));
        setContentPane(mainPanel);

        mainPanel.add(buildHeader(), BorderLayout.NORTH);
        mainPanel.add(buildCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(buildActionPanel(), BorderLayout.SOUTH);

        registerAutoCalculationListeners();
        setReadOnlyFields();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("Repayment Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(20, 80, 35));

        JLabel subtitle = new JLabel("Create repayment records, post payments, and review repayment status instantly.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(70, 70, 70));

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitle);
        return panel;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setOpaque(false);
        panel.add(buildInputPanel());
        panel.add(buildResultPanel());
        return panel;
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 8, 8));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(221, 231, 221)),
            new EmptyBorder(14, 14, 14, 14)
        ));

        addField(panel, "Repayment ID", repaymentIdField, "RP-1001");
        addField(panel, "Loan ID", loanIdField, "LN-2001");
        addField(panel, "Customer Name", customerNameField, "John Doe");
        addField(panel, "Scheduled Date (yyyy-MM-dd)", scheduledDateField, "2026-06-01");
        addField(panel, "Scheduled Amount", scheduledAmountField, "1000");
        addField(panel, "Penalty Rate (e.g., 0.05)", penaltyRateField, "0.05");
        addField(panel, "Amount Paid", amountPaidField, "0");
        addField(panel, "Payment Date (yyyy-MM-dd)", paymentDateField, "2026-06-01");

        return panel;
    }

    private JPanel buildResultPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(221, 231, 221)),
            new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel topResultPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        topResultPanel.setOpaque(false);
        addField(topResultPanel, "Penalty Amount", penaltyAmountField, "");
        addField(topResultPanel, "Total Amount Due", totalDueField, "");
        addField(topResultPanel, "Repayment Status", statusField, "");

        outputArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setBackground(new Color(249, 250, 249));
        outputArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        outputArea.setText("Repayment summary will appear here.");

        panel.add(topResultPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildActionPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton createButton = createActionButton("Create Repayment");
        JButton payButton = createActionButton("Post Payment");
        JButton summaryButton = createActionButton("View Summary");
        JButton clearButton = createActionButton("Clear Form");
        clearButton.setBackground(new Color(80, 80, 80));

        createButton.addActionListener(e -> createRepayment());
        payButton.addActionListener(e -> postPayment());
        summaryButton.addActionListener(e -> showSummary());
        clearButton.addActionListener(e -> clearForm());

        panel.add(createButton);
        panel.add(payButton);
        panel.add(summaryButton);
        panel.add(clearButton);
        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(150, 36));
        button.setBackground(new Color(34, 139, 34));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }

    private void addField(JPanel panel, String label, JTextField field, String defaultText) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setText(defaultText);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(jLabel);
        panel.add(field);
    }

    private void setReadOnlyFields() {
        penaltyAmountField.setEditable(false);
        totalDueField.setEditable(false);
        statusField.setEditable(false);
        penaltyAmountField.setBackground(new Color(240, 245, 240));
        totalDueField.setBackground(new Color(240, 245, 240));
        statusField.setBackground(new Color(240, 245, 240));
    }

    private void registerAutoCalculationListeners() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                recalculatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                recalculatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                recalculatePreview();
            }
        };

        scheduledAmountField.getDocument().addDocumentListener(listener);
        penaltyRateField.getDocument().addDocumentListener(listener);
        amountPaidField.getDocument().addDocumentListener(listener);
        paymentDateField.getDocument().addDocumentListener(listener);
        scheduledDateField.getDocument().addDocumentListener(listener);
    }

    private void createRepayment() {
        try {
            validateRequiredFields();
            double scheduledAmount = parsePositiveDouble(scheduledAmountField.getText().trim(), "Scheduled Amount");
            double penaltyRate = parseNonNegativeDouble(penaltyRateField.getText().trim(), "Penalty Rate");
            validateDateField(scheduledDateField.getText().trim(), "Scheduled Date");

            repayment = new Repayment(
                repaymentIdField.getText().trim(),
                loanIdField.getText().trim(),
                customerNameField.getText().trim(),
                scheduledDateField.getText().trim(),
                scheduledAmount,
                penaltyRate
            );

            statusField.setText(Repayment.PENDING);
            recalculatePreview();
            outputArea.setText("Repayment record created successfully.\nYou can now post a payment.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Unexpected error while creating repayment: " + ex.getMessage());
        }
    }

    private void postPayment() {
        try {
            ensureRepaymentExists();
            double amountPaid = parseNonNegativeDouble(amountPaidField.getText().trim(), "Amount Paid");
            String paymentDate = paymentDateField.getText().trim();
            validateDateField(paymentDate, "Payment Date");

            repayment.makePayment(amountPaid, paymentDate);

            penaltyAmountField.setText(formatMoney(repayment.getPenaltyAmount()));
            totalDueField.setText(formatMoney(repayment.getTotalAmountDue()));
            statusField.setText(repayment.getRepaymentStatus());

            outputArea.setText(buildRepaymentDetails());
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError("Unexpected error while posting payment: " + ex.getMessage());
        }
    }

    private void showSummary() {
        try {
            ensureRepaymentExists();
            outputArea.setText(buildRepaymentDetails());
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void recalculatePreview() {
        try {
            if (repayment == null) {
                penaltyAmountField.setText("-");
                totalDueField.setText("-");
                statusField.setText("-");
                return;
            }

            double amountPaid = parseNonNegativeDouble(amountPaidField.getText().trim(), "Amount Paid");
            String paymentDate = paymentDateField.getText().trim();
            if (paymentDate.isEmpty()) {
                penaltyAmountField.setText(formatMoney(0.0));
                totalDueField.setText(formatMoney(repayment.getScheduledAmount()));
                statusField.setText(repayment.getRepaymentStatus());
                return;
            }

            validateDateField(paymentDate, "Payment Date");
            double previewPenalty = repayment.previewPenalty(amountPaid, paymentDate);
            double previewTotalDue = repayment.previewTotalDue(amountPaid, paymentDate);

            penaltyAmountField.setText(formatMoney(previewPenalty));
            totalDueField.setText(formatMoney(previewTotalDue));

            if (amountPaid >= previewTotalDue) {
                statusField.setText(Repayment.COMPLETED);
            } else if (LocalDate.parse(paymentDate).isAfter(LocalDate.parse(repayment.getScheduledDate()))) {
                statusField.setText(Repayment.OVERDUE);
            } else if (amountPaid > 0) {
                statusField.setText(Repayment.PARTIAL);
            } else {
                statusField.setText(Repayment.PENDING);
            }
        } catch (IllegalArgumentException ex) {
            penaltyAmountField.setText("-");
            totalDueField.setText("-");
            statusField.setText("Invalid input");
        }
    }

    private String buildRepaymentDetails() {
        return "Repayment ID: " + repayment.getRepaymentId() + "\n"
            + "Loan ID: " + repayment.getLoanId() + "\n"
            + "Customer Name: " + repayment.getCustomerName() + "\n"
            + "Scheduled Date: " + repayment.getScheduledDate() + "\n"
            + "Scheduled Amount: $" + formatMoney(repayment.getScheduledAmount()) + "\n"
            + "Amount Paid: $" + formatMoney(repayment.getAmountPaid()) + "\n"
            + "Payment Date: " + (repayment.getPaymentDate() == null ? "N/A" : repayment.getPaymentDate()) + "\n"
            + "Penalty Amount: $" + formatMoney(repayment.getPenaltyAmount()) + "\n"
            + "Total Due: $" + formatMoney(repayment.getTotalAmountDue()) + "\n"
            + "Status: " + repayment.getRepaymentStatus();
    }

    private void validateRequiredFields() {
        if (repaymentIdField.getText().trim().isEmpty()
            || loanIdField.getText().trim().isEmpty()
            || customerNameField.getText().trim().isEmpty()
            || scheduledDateField.getText().trim().isEmpty()
            || scheduledAmountField.getText().trim().isEmpty()
            || penaltyRateField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Please fill in all required repayment fields.");
        }
    }

    private void ensureRepaymentExists() {
        if (repayment == null) {
            throw new IllegalArgumentException("Create a repayment record first.");
        }
    }

    private void validateDateField(String dateText, String fieldName) {
        if (dateText == null || dateText.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        try {
            LocalDate.parse(dateText.trim());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format.");
        }
    }

    private double parsePositiveDouble(String value, String fieldName) {
        double parsed = parseDouble(value, fieldName);
        if (parsed <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0.");
        }
        return parsed;
    }

    private double parseNonNegativeDouble(String value, String fieldName) {
        double parsed = parseDouble(value, fieldName);
        if (parsed < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }
        return parsed;
    }

    private double parseDouble(String value, String fieldName) {
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
            // Falls back to default look-and-feel if system look-and-feel is unavailable.
        }

        SwingUtilities.invokeLater(() -> new RepaymentModuleGUI().setVisible(true));
    }
}
