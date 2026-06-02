package com.customerregistration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
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

    public CustomerActivityHistoryGUI() {
        setTitle("System Activity Records Database Verification Terminal");
        // FIXED: Safe dynamic sandbox close behavior execution target
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 550);
        setLocationRelativeTo(null);
        buildWindowLayoutStructure();
        loadActiveMockCustomers();
    }

    private void buildWindowLayoutStructure() {
        setLayout(new BorderLayout(10, 10));

        JPanel topSelectionGridPanel = new JPanel(new GridLayout(2, 2, 6, 6));
        topSelectionGridPanel.setBorder(BorderFactory.createTitledBorder("Target Client Log Target Workspace Parameters"));

        topSelectionGridPanel.add(new JLabel("Select Profile Customer Account Lookup:"));
        customerCombo = new JComboBox<>();
        topSelectionGridPanel.add(customerCombo);

        topSelectionGridPanel.add(new JLabel("Filter Activity Tracking Sub-Classification:"));
        activityTypeCombo = new JComboBox<>(new String[]{
                CustomerActivityHistory.LOAN_APPLICATION,
                CustomerActivityHistory.LOAN_REJECTION,
                CustomerActivityHistory.LOAN_DISBURSEMENT,
                CustomerActivityHistory.REPAYMENT,
                CustomerActivityHistory.MISSED_PAYMENT
        });
        topSelectionGridPanel.add(activityTypeCombo);
        add(topSelectionGridPanel, BorderLayout.NORTH);

        // Center Output View Log Consolidation Displays
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(historyArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Chronological Raw Logs Parsing Output Stream"));
        add(scroll, BorderLayout.CENTER);

        // West Insertion Interceptions Inputs Layout
        JPanel westPanel = new JPanel(new GridLayout(4, 2, 6, 6));
        westPanel.setBorder(BorderFactory.createTitledBorder("Inject Audit Compliance Activity Entry"));

        westPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        westPanel.add(dateField);

        westPanel.add(new JLabel("Time (HH:MM):"));
        timeField = new JTextField(new SimpleDateFormat("HH:mm").format(new Date()));
        westPanel.add(timeField);

        westPanel.add(new JLabel("Context Description:"));
        descriptionField = new JTextField();
        westPanel.add(descriptionField);

        JButton btnAdd = new JButton("Inject Event Log");
        westPanel.add(btnAdd);
        add(westPanel, BorderLayout.WEST);

        // South Operational Actions Control Row Bar Buttons
        JPanel southBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnFilter = new JButton("Apply Classification Filter Criteria");
        JButton btnRefreshAll = new JButton("Fetch Complete Log History");
        
        southBar.add(btnFilter);
        southBar.add(btnRefreshAll);
        add(southBar, BorderLayout.SOUTH);

        // Event Routing Handlers Interceptions Wire-up
        customerCombo.addActionListener(e -> handleSwitchCustomerContextTarget());
        btnAdd.addActionListener(e -> executeLogInjectionPipelineAction());
        btnFilter.addActionListener(e -> showFilteredHistoryLogView());
        btnRefreshAll.addActionListener(e -> showAllHistoryLogView());
    }

    private void loadActiveMockCustomers() {
        customerCombo.addItem("CUST-1001: Yabu");
        customerCombo.addItem("CUST-1002: Alpha Corp");
        customerCombo.addItem("CUST-1003: Omega Team Group");
    }

    private void handleSwitchCustomerContextTarget() {
        String selection = (String) customerCombo.getSelectedItem();
        if (selection == null) return;

        String id = selection.split(":")[0].trim();
        String name = selection.split(":")[1].trim();

        currentHistory = new CustomerActivityHistory(id, new String[]{name});
        showAllHistoryLogView();
    }

    private void executeLogInjectionPipelineAction() {
        if (currentHistory == null) {
            JOptionPane.showMessageDialog(this, "Configuration failure: Select an active profile tracking target client first.", "Context Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String type = (String) activityTypeCombo.getSelectedItem();
        String date = dateField.getText().trim();
        String time = timeField.getText().trim();
        String desc = descriptionField.getText().trim();

        if (date.isEmpty() || time.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Validation failure: Event payload descriptors fields parameters cannot be blank.", "Input Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }

        currentHistory.addActivity(type, date, time, desc);
        descriptionField.setText("");
        showAllHistoryLogView();
        JOptionPane.showMessageDialog(this, "Compliance transaction successfully injected and recorded to offline text data pools.", "Log Saved", JOptionPane.INFORMATION_MESSAGE);
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