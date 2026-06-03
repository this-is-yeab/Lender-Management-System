package com.customerregistration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

public class CustomerActivityHistoryGUI extends JFrame {

    private JComboBox<String> customerCombo;
    private JComboBox<String> activityTypeCombo;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField descriptionField;
    private JTextArea historyArea;
    private CustomerActivityHistory currentHistory;
    
    // Store parsed database reference IDs safely mappings
    private ArrayList<String> registeredCustomerIdsList = new ArrayList<>();

    public CustomerActivityHistoryGUI() {
        setTitle("System Activity Records Database Verification Terminal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);
        buildWindowLayoutStructure();
        loadActiveDatabaseCustomers();
    }

    private void buildWindowLayoutStructure() {
        setLayout(new BorderLayout(10, 10));

        JPanel topSelectionDeckPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topSelectionDeckPanel.setBorder(BorderFactory.createTitledBorder("Active Workspace Selector Matrix"));
        
        topSelectionDeckPanel.add(new JLabel("Target Client Profile File:"));
        customerCombo = new JComboBox<>();
        customerCombo.addActionListener(e -> synchronizeSelectedCustomerHistoryContext());
        topSelectionDeckPanel.add(customerCombo);

        add(topSelectionDeckPanel, BorderLayout.NORTH);

        JPanel leftEntryControlFormPanel = new JPanel(new GridLayout(6, 1, 8, 8));
        leftEntryControlFormPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Append Compliance Event Payload"));

        leftEntryControlFormPanel.add(new JLabel("Activity Code Category Type:"));
        activityTypeCombo = new JComboBox<>(new String[]{
            CustomerActivityHistory.LOAN_APPLICATION,
            CustomerActivityHistory.LOAN_REJECTION,
            CustomerActivityHistory.LOAN_DISBURSEMENT,
            CustomerActivityHistory.REPAYMENT,
            CustomerActivityHistory.MISSED_PAYMENT,
            CustomerActivityHistory.MODIFICATION
        });
        activityTypeCombo.addActionListener(e -> showFilteredHistoryLogView());
        leftEntryControlFormPanel.add(activityTypeCombo);

        leftEntryControlFormPanel.add(new JLabel("Log Execution Stamp Date (YYYY-MM-DD):"));
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        leftEntryControlFormPanel.add(dateField);

        leftEntryControlFormPanel.add(new JLabel("Log Execution Stamp Time (HH:MM):"));
        timeField = new JTextField(new SimpleDateFormat("HH:mm").format(new Date()));
        leftEntryControlFormPanel.add(timeField);

        leftEntryControlFormPanel.add(new JLabel("Audit Transaction Trail Description:"));
        descriptionField = new JTextField();
        leftEntryControlFormPanel.add(descriptionField);

        JButton btnCommitLog = new JButton("Inject Compliance Event Entry Line");
        btnCommitLog.addActionListener(e -> executeComplianceLogInjection());
        leftEntryControlFormPanel.add(btnCommitLog);

        JPanel wrapFormPanel = new JPanel(new BorderLayout());
        wrapFormPanel.add(leftEntryControlFormPanel, BorderLayout.NORTH);
        add(wrapFormPanel, BorderLayout.WEST);

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        
        JPanel centerDisplayAreaWrapperPanel = new JPanel(new BorderLayout());
        centerDisplayAreaWrapperPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Audited Operational Log Summary Ledger Output", TitledBorder.LEADING, TitledBorder.TOP));
        centerDisplayAreaWrapperPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);

        JPanel filterActionsBarDeck = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnViewAll = new JButton("Reset Clear Filters (Show All Logs)");
        btnViewAll.addActionListener(e -> showAllHistoryLogView());
        filterActionsBarDeck.add(btnViewAll);
        centerDisplayAreaWrapperPanel.add(filterActionsBarDeck, BorderLayout.SOUTH);

        add(centerDisplayAreaWrapperPanel, BorderLayout.CENTER);
    }

    // READS REGISTERED CUSTOMERS DIRECTLY FROM THE DATABASE TEXT FILE
    private void loadActiveDatabaseCustomers() {
        customerCombo.removeAllItems();
        registeredCustomerIdsList.clear();

        File file = new File(CustomerActivityHistory.DATABASE_FILE);
        if (!file.exists()) {
            customerCombo.addItem("No Database File Found");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            String id = "";
            String name = "";
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) {
                    id = line.replace("Registration ID:", "").trim();
                } else if (line.startsWith("Name:")) {
                    name = line.replace("Name:", "").trim();
                } else if (line.startsWith("=========================================")) {
                    if (!id.isEmpty() && !name.isEmpty()) {
                        customerCombo.addItem(name + " (" + id + ")");
                        registeredCustomerIdsList.add(id);
                    }
                    id = "";
                    name = "";
                }
            }
            // Capture tail files block elements
            if (!id.isEmpty() && !name.isEmpty()) {
                customerCombo.addItem(name + " (" + id + ")");
                registeredCustomerIdsList.add(id);
            }
        } catch (IOException e) {
            customerCombo.addItem("Error Reading Database Records");
        }

        if (customerCombo.getItemCount() == 0) {
            customerCombo.addItem("No Customers Registered Yet");
        }
    }

    private void synchronizeSelectedCustomerHistoryContext() {
        int index = customerCombo.getSelectedIndex();
        if (index < 0 || registeredCustomerIdsList.isEmpty() || index >= registeredCustomerIdsList.size()) {
            currentHistory = null;
            historyArea.setText("Select a customer from the top menu dropdown list to pull file logs.");
            return;
        }

        String chosenId = registeredCustomerIdsList.get(index);
        String labelStr = (String) customerCombo.getSelectedItem();
        String cleanName = labelStr.substring(0, labelStr.indexOf(" ("));

        currentHistory = CustomerActivityHistory.forCustomer(chosenId, cleanName);
        showAllHistoryLogView();
    }

    private void executeComplianceLogInjection() {
        if (currentHistory == null) {
            JOptionPane.showMessageDialog(this, "Please verify a target customer profile has been selected.", "Selection Required", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String type = (String) activityTypeCombo.getSelectedItem();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String desc = descriptionField.getText().trim();

        if (date.isEmpty() || time.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Validation failure: Event payload description parameters fields cannot be blank.", "Input Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentHistory.addActivity(type, date, time, desc);
        descriptionField.setText("");
        showAllHistoryLogView();
        JOptionPane.showMessageDialog(this, "Compliance event transaction successfully recorded to text database.", "Log Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAllHistoryLogView() {
        if (currentHistory == null) {
            historyArea.setText("Logs offline: Clear identity selection state context.");
            return;
        }
        historyArea.setText(currentHistory.getFormattedHistory());
    }

    private void showFilteredHistoryLogView() {
        if (currentHistory == null) return;
        String type = (String) activityTypeCombo.getSelectedItem();
        historyArea.setText(currentHistory.getFormattedFilteredHistory(type));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerActivityHistoryGUI().setVisible(true));
    }
}