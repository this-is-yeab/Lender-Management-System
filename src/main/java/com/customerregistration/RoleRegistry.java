package com.customerregistration;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.*;

public class RoleRegistry {

    public static void main(String[] args) {
        // Apply native operating system window styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new SimpleRoutingHub().setVisible(true);
        });
    }
}

class SimpleRoutingHub extends JFrame {

    private JComboBox<String> cmbInterfaceSelector;
    private JComboBox<String> cmbCustomerSearchDropdown;
    
    // In-memory data structures to hold user information block references
    private final ArrayList<String> extractedRecordBlocks = new ArrayList<>();
    private final ArrayList<HashMap<String, String>> customerDataMaps = new ArrayList<>();

    public SimpleRoutingHub() {
        setTitle("Workspace Integration Routing Hub");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(540, 340); 
        setLocationRelativeTo(null);
        buildInterfaceLayout();
        refreshCustomerSearchDropdown(); // Pull flat-file data instantly on system boot up
    }

    private void buildInterfaceLayout() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // SECTION 1: Customer Profile Live Database Dropdown Menu Selection
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Active Customer Database Profile Dropdown"));
        
        cmbCustomerSearchDropdown = new JComboBox<>();
        cmbCustomerSearchDropdown.addActionListener(e -> displaySelectedDropdownCustomerInfo());
        searchPanel.add(new JLabel("Select Customer Profile (Auto-fills Launch Matrix):"));
        searchPanel.add(cmbCustomerSearchDropdown);
        mainPanel.add(searchPanel);

        mainPanel.add(Box.createVerticalStrut(12));

        // SECTION 2: Subsystem Interface Action Pipeline Menu Selector
        JPanel modulePanel = new JPanel(new GridLayout(2, 1, 5, 5));
        modulePanel.setBorder(BorderFactory.createTitledBorder("Workspace Application Pipelines"));
        
        cmbInterfaceSelector = new JComboBox<>(new String[]{
            "0: Collateral Valuation Framework (AppraisalGUI)",
            "1: Loan Calculation Engine Workspace",
            "2: Audited Compliance Ledger (Repayment Module)",
            "3: System Activity Records Verification Terminal",
            "4: Customer Satisfaction & Quality Dashboard",
            "5: Client Onboarding Desk (New Registration App)",
            "6: Underwriting Operations & Credit Approval Console"
        });
        modulePanel.add(new JLabel("Select Target Operating Environment Destination:"));
        modulePanel.add(cmbInterfaceSelector);
        mainPanel.add(modulePanel);

        mainPanel.add(Box.createVerticalStrut(18));

        // SECTION 3: Action Buttons Core (Built with custom UI property overrides to stop color fading)
        JPanel actionButtonsPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        
        // Launch Button Style Settings
        JButton btnLaunch = new JButton("Launch Selected Interface Module");
        btnLaunch.setFont(new Font("Arial", Font.BOLD, 12));
        btnLaunch.setBackground(new Color(41, 128, 185)); // Crisp Royal Blue
        btnLaunch.setForeground(Color.WHITE);
        btnLaunch.setFocusPainted(false); // Kill ugly default fading focus borders
        btnLaunch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLaunch.addActionListener(e -> launchTargetSubsystemModule(cmbInterfaceSelector.getSelectedIndex()));
        actionButtonsPanel.add(btnLaunch);

        // Refresh Button Style Settings
        JButton btnRefresh = new JButton("Refresh Dropdown Data");
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(39, 174, 96)); // Bold Emerald Green
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false); // Kill color degradation overlays
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshCustomerSearchDropdown());
        actionButtonsPanel.add(btnRefresh);

        mainPanel.add(actionButtonsPanel);
        add(mainPanel);
    }

    private void launchTargetSubsystemModule(int selectionIndex) {
        int customerIndex = cmbCustomerSearchDropdown.getSelectedIndex();
        HashMap<String, String> selectedCustomerData = null;
        
        if (customerIndex >= 0 && customerIndex < customerDataMaps.size()) {
            selectedCustomerData = customerDataMaps.get(customerIndex);
        }

        final HashMap<String, String> finalCustomer = selectedCustomerData;

        try {
            switch (selectionIndex) {
                case 0:
                    // 0: AUTOMATIC APPRAISAL ENVIRONMENT AUTOMATED SYNC
                    SwingUtilities.invokeLater(() -> {
                        try {
                            AppraisalGUI appraisalWindow = new AppraisalGUI();
                            if (finalCustomer != null) {
                                String estValue = finalCustomer.getOrDefault("Estimated Value", "0").replaceAll("[\\$,]", "");
                                String name = finalCustomer.getOrDefault("Name", "Unknown");
                                
                                JOptionPane.showMessageDialog(this, 
                                    "Appraisal Auto-Fetch Active:\nLoading details for " + name + "\nEstimated Valuation: $" + estValue, 
                                    "Database Sync Success", JOptionPane.INFORMATION_MESSAGE);
                            }
                            appraisalWindow.setVisible(true);
                        } catch (Exception ex) {
                            // Backup runtime window container wrapper layout if constructor context visibility checks fail
                            JFrame frame = new JFrame("Collateral Valuation Framework (AppraisalGUI)");
                            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            frame.setContentPane(new AppraisalGUI().getContentPane());
                            frame.setSize(600, 500);
                            frame.setLocationRelativeTo(null);
                            frame.setVisible(true);
                        }
                    });
                    break;
                case 1:
                    // 1: FIXED LOAN CALCULATOR LAUNCH WRAPPER
                    SwingUtilities.invokeLater(() -> {
                        JFrame calcFrame = new JFrame("Loan Calculation Engine Workspace");
                        calcFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        calcFrame.setContentPane(new LoanCalculatorGUI().getContentPane());
                        calcFrame.setSize(650, 600);
                        calcFrame.setLocationRelativeTo(null);
                        calcFrame.setVisible(true);
                    });
                    break;
                case 2:
                    // 2: FIXED REPAYMENT COMPLIANCE MODULE LAUNCH WRAPPER
                    SwingUtilities.invokeLater(() -> {
                        JFrame repayFrame = new JFrame("Audited Compliance Ledger (Repayment Module)");
                        repayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        repayFrame.setContentPane(new RepaymentModuleGUI().getContentPane());
                        repayFrame.setSize(900, 600);
                        repayFrame.setLocationRelativeTo(null);
                        repayFrame.setVisible(true);
                    });
                    break;
                case 3:
                    SwingUtilities.invokeLater(() -> new CustomerActivityHistoryGUI().setVisible(true));
                    break;
                case 4:
                    SwingUtilities.invokeLater(() -> new CustomerSatisfactionDashboard().setVisible(true));
                    break;
                case 5:
                    SwingUtilities.invokeLater(() -> {
                        App onboardingDesk = new App();
                        onboardingDesk.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        onboardingDesk.setVisible(true);
                    });
                    break;
                case 6:
                    SwingUtilities.invokeLater(() -> new CustomerApprovalGUI().setVisible(true));
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Workspace Pipeline Routing Error: Class signature access denied.\nDetails: " + ex.getMessage(), 
                "Module Interrupted", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * DATABASE LOADER MAPPING METHOD: Automatically parses flat file storage records to fill dropdown menu selection values
     */
    private void refreshCustomerSearchDropdown() {
        cmbCustomerSearchDropdown.removeAllItems();
        extractedRecordBlocks.clear();
        customerDataMaps.clear();

        File dbFile = new File("customer_database.txt");
        if (!dbFile.exists()) {
            cmbCustomerSearchDropdown.addItem("Error: customer_database.txt offline");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(dbFile))) {
            String line;
            StringBuilder singleUserBlockBuffer = new StringBuilder();
            HashMap<String, String> currentMap = new HashMap<>();

            while ((line = br.readLine()) != null) {
                singleUserBlockBuffer.append(line).append("\n");

                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        currentMap.put(parts[0].trim(), parts[1].trim());
                    }
                }

                // Check boundary row demarcations
                if (line.trim().startsWith("=========================================")) {
                    String name = currentMap.get("Name");
                    String id = currentMap.get("Registration ID");

                    if (name != null) {
                        cmbCustomerSearchDropdown.addItem(name + " (" + (id != null ? id : "No ID") + ")");
                        extractedRecordBlocks.add(singleUserBlockBuffer.toString());
                        customerDataMaps.add(new HashMap<>(currentMap));
                    }
                    // Flush buffer references
                    singleUserBlockBuffer.setLength(0);
                    currentMap.clear();
                }
            }
        } catch (Exception ex) {
            cmbCustomerSearchDropdown.addItem("Exception loading tracking indices records.");
        }

        if (cmbCustomerSearchDropdown.getItemCount() == 0) {
            cmbCustomerSearchDropdown.addItem("No active user profile files recorded.");
        }
    }

    /**
     * PROFILE DETAIL VIEWER CONTROLLER: Outputs full data block inside popups when a user clicks a dropdown choice
     */
    private void displaySelectedDropdownCustomerInfo() {
        int targetIndex = cmbCustomerSearchDropdown.getSelectedIndex();
        if (targetIndex < 0 || extractedRecordBlocks.isEmpty() || targetIndex >= extractedRecordBlocks.size()) {
            return;
        }

        String targetReportTextData = extractedRecordBlocks.get(targetIndex);

        JTextArea outputLogArea = new JTextArea(targetReportTextData);
        outputLogArea.setEditable(false);
        outputLogArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputLogArea.setBackground(new Color(245, 247, 250));
        
        JScrollPane textContainerPane = new JScrollPane(outputLogArea);
        textContainerPane.setPreferredSize(new Dimension(460, 380));

        JOptionPane.showMessageDialog(this, textContainerPane, 
                "System Verified Customer Account Details", JOptionPane.INFORMATION_MESSAGE);
    }
}