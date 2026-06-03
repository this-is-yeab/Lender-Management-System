package com.customerregistration;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class CustomerApprovalGUI extends JFrame {

    private JComboBox<String> cmbPendingCustomers;
    private JComboBox<String> cmbAccountTierSelection; 
    private JLabel lblEngineDecision;
    private JTextField txtMaxLendAmount;
    private JComboBox<String> cmbApprovalAction;
    private JTextArea txtNotesReason;
    private DefaultTableModel tableModel;
    
    private ArrayList<String> customerIdsList = new ArrayList<>();
    private ArrayList<String> customerTiersList = new ArrayList<>();
    private ArrayList<String> customerAgeGroupsList = new ArrayList<>();
    private ArrayList<Boolean> customerActiveStatesList = new ArrayList<>();
    private ArrayList<CustomerInformation> customerObjectsList = new ArrayList<>();

    public CustomerApprovalGUI() {
        setTitle("Audited Corporate Underwriting & Credit Risk Evaluation Terminal");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 520);
        setLocationRelativeTo(null);
        buildLayoutDeck();
        loadPendingDatabaseProfiles();
    }

    private void buildLayoutDeck() {
        setLayout(new BorderLayout(12, 12));

        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTop.setBorder(BorderFactory.createTitledBorder("Compliance Queue Selection"));
        pnlTop.add(new JLabel("Target Review Profile:"));
        cmbPendingCustomers = new JComboBox<>();
        cmbPendingCustomers.addActionListener(e -> syncFormWithSelectedCustomer());
        pnlTop.add(cmbPendingCustomers);
        add(pnlTop, BorderLayout.NORTH);

        JPanel pnlLeftForm = new JPanel(new GridLayout(7, 2, 8, 8));
        pnlLeftForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Risk Metric Review Deck", TitledBorder.LEADING, TitledBorder.TOP));
        pnlLeftForm.setPreferredSize(new Dimension(400, 350));

        pnlLeftForm.add(new JLabel("Profile Account Tier:"));
        cmbAccountTierSelection = new JComboBox<>(new String[]{"Premium", "Gold", "Bronze", "Regular"});
        cmbAccountTierSelection.addActionListener(e -> recalculateLoanLimitBasedOnSelectedTier());
        pnlLeftForm.add(cmbAccountTierSelection);

        pnlLeftForm.add(new JLabel("Engine Eligibility Evaluation:"));
        lblEngineDecision = new JLabel("PENDING");
        lblEngineDecision.setFont(new Font("Arial", Font.BOLD, 12));
        pnlLeftForm.add(lblEngineDecision);

        pnlLeftForm.add(new JLabel("Calculated Loan Cap Limit ($):"));
        txtMaxLendAmount = new JTextField("0.00");
        pnlLeftForm.add(txtMaxLendAmount);

        pnlLeftForm.add(new JLabel("Final Status Overwrite Decision:"));
        cmbApprovalAction = new JComboBox<>(new String[]{"Approved", "Rejected", "Conditional Approval"});
        pnlLeftForm.add(cmbApprovalAction);

        pnlLeftForm.add(new JLabel("Underwriter Notes / Justification:"));
        txtNotesReason = new JTextArea();
        txtNotesReason.setLineWrap(true);
        txtNotesReason.setWrapStyleWord(true);
        pnlLeftForm.add(new JScrollPane(txtNotesReason));

        JButton btnCommit = new JButton("Commit Underwriting Decision to DB");
        btnCommit.setBackground(new Color(39, 174, 96));
        btnCommit.setForeground(Color.WHITE);
        btnCommit.setFont(new Font("Arial", Font.BOLD, 12));
        btnCommit.addActionListener(e -> saveUnderwritingDecisionToTxtDatabase());
        pnlLeftForm.add(btnCommit);

        JPanel westWrapper = new JPanel(new BorderLayout());
        westWrapper.add(pnlLeftForm, BorderLayout.NORTH);
        add(westWrapper, BorderLayout.WEST);

        String[] columns = {"ID Key", "Borrower Identity", "Tier Class", "Allowance Limit", "Approval Decision Status", "Reason Notes"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable tblHistory = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tblHistory);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Live Credit Facility Approval Ledger"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPendingDatabaseProfiles() {
        cmbPendingCustomers.removeAllItems();
        customerIdsList.clear();
        customerTiersList.clear();
        customerAgeGroupsList.clear();
        customerActiveStatesList.clear();
        customerObjectsList.clear();
        tableModel.setRowCount(0);

        File dbFile = new File("customer_database.txt");
        if (!dbFile.exists()) {
            cmbPendingCustomers.addItem("No Database File Located");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            String currentId = "";
            String currentName = "";
            String currentTier = "Regular";
            String currentAge = "Adult";
            boolean currentActive = true;
            String approvalStatus = "Pending Assessment";
            String lendLimit = "0.00";
            String noteReason = "Initial Registration Baseline Entry Stack";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("Registration ID:")) {
                    currentId = line.replace("Registration ID:", "").trim();
                } else if (line.startsWith("Name:")) {
                    currentName = line.replace("Name:", "").trim();
                } else if (line.startsWith("Account Tier:")) {
                    currentTier = line.replace("Account Tier:", "").trim();
                } else if (line.startsWith("Social Tier:")) { 
                    currentTier = line.replace("Social Tier:", "").trim();
                } else if (line.startsWith("Age Group:")) {
                    currentAge = line.replace("Age Group:", "").trim();
                } else if (line.startsWith("Status:")) {
                    currentActive = line.toLowerCase().contains("active");
                } else if (line.startsWith("Approval Decision Status:")) {
                    approvalStatus = line.replace("Approval Decision Status:", "").trim();
                } else if (line.startsWith("Assigned Credit Limit allowance:")) {
                    lendLimit = line.replace("Assigned Credit Limit allowance:", "").trim();
                } else if (line.startsWith("Underwriter Analytics Reason:")) {
                    noteReason = line.replace("Underwriter Analytics Reason:", "").trim();
                } else if (line.startsWith("=========================================")) {
                    if (!currentId.isEmpty() && !currentName.isEmpty()) {
                        cmbPendingCustomers.addItem(currentName + " (" + currentId + ")");
                        customerIdsList.add(currentId);
                        customerTiersList.add(currentTier);
                        customerAgeGroupsList.add(currentAge);
                        customerActiveStatesList.add(currentActive);

                        CustomerInformation tempCust = new CustomerInformation(
                            currentId, currentName, currentAge, currentTier,
                            currentActive ? "Active" : "Inactive",
                            0.0, "", "", false, 0, 0.0, 0.0, false, false
                        );
                        customerObjectsList.add(tempCust);

                        tableModel.addRow(new Object[]{
                            currentId, currentName, currentTier, "$" + lendLimit, approvalStatus, noteReason
                        });
                    }
                    currentId = ""; currentName = ""; currentTier = "Regular"; 
                    currentAge = "Adult"; currentActive = true;
                    approvalStatus = "Pending Assessment"; lendLimit = "0.00";
                    noteReason = "Initial Registration Baseline Entry Stack";
                }
            }
            
            if (!currentId.isEmpty() && !currentName.isEmpty()) {
                cmbPendingCustomers.addItem(currentName + " (" + currentId + ")");
                customerIdsList.add(currentId);
                customerTiersList.add(currentTier);
                customerAgeGroupsList.add(currentAge);
                customerActiveStatesList.add(currentActive);
                
                CustomerInformation tempCust = new CustomerInformation(
                    currentId, currentName, currentAge, currentTier,
                    currentActive ? "Active" : "Inactive",
                    0.0, "", "", false, 0, 0.0, 0.0, false, false
                );
                customerObjectsList.add(tempCust);

                tableModel.addRow(new Object[]{
                    currentId, currentName, currentTier, "$" + lendLimit, approvalStatus, noteReason
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Exception reading database: " + e.getMessage());
        }

        if (cmbPendingCustomers.getItemCount() == 0) {
            cmbPendingCustomers.addItem("No Registered Accounts Profiles Found");
        }
    }

    private void syncFormWithSelectedCustomer() {
        int idx = cmbPendingCustomers.getSelectedIndex();
        if (idx < 0 || customerIdsList.isEmpty() || idx >= customerIdsList.size()) {
            lblEngineDecision.setText("PENDING");
            txtMaxLendAmount.setText("0.00");
            return;
        }

        String tier = customerTiersList.get(idx);
        CustomerInformation selectedCustomer = customerObjectsList.get(idx);

        for (int i = 0; i < cmbAccountTierSelection.getItemCount(); i++) {
            if (cmbAccountTierSelection.getItemAt(i).equalsIgnoreCase(tier)) {
                cmbAccountTierSelection.setSelectedIndex(i);
                break;
            }
        }

        CustomerApproval evaluationEngine = new CustomerApproval(selectedCustomer);
        String engineVerdict = evaluationEngine.evaluateApproval();
        
        lblEngineDecision.setText(engineVerdict.toUpperCase());
        if (CustomerApproval.APPROVED.equalsIgnoreCase(engineVerdict)) {
            lblEngineDecision.setForeground(new Color(39, 174, 96));
            cmbApprovalAction.setSelectedIndex(0);
            txtNotesReason.setText(evaluationEngine.getApprovalReason());
        } else if (CustomerApproval.REJECTED.equalsIgnoreCase(engineVerdict)) {
            lblEngineDecision.setForeground(Color.RED);
            cmbApprovalAction.setSelectedIndex(1);
            txtNotesReason.setText(evaluationEngine.getApprovalReason());
        } else {
            lblEngineDecision.setForeground(Color.ORANGE);
            cmbApprovalAction.setSelectedIndex(2);
            txtNotesReason.setText(evaluationEngine.getApprovalReason());
        }

        recalculateLoanLimitBasedOnSelectedTier();
    }

    private void recalculateLoanLimitBasedOnSelectedTier() {
        String selectedTier = (String) cmbAccountTierSelection.getSelectedItem();
        if (selectedTier == null) return;

        double baseAllowance = 10000.00;
        if ("Premium".equalsIgnoreCase(selectedTier)) {
            baseAllowance = 150000.00;
        } else if ("Gold".equalsIgnoreCase(selectedTier)) {
            baseAllowance = 75000.00;
        } else if ("Bronze".equalsIgnoreCase(selectedTier)) {
            baseAllowance = 30000.00;
        } else if ("Regular".equalsIgnoreCase(selectedTier)) {
            baseAllowance = 15000.00;
        }

        if ("REJECTED".equalsIgnoreCase(lblEngineDecision.getText())) {
            baseAllowance = 0.00;
        }

        txtMaxLendAmount.setText(String.format("%.2f", baseAllowance));
    }

    private void saveUnderwritingDecisionToTxtDatabase() {
        int idx = cmbPendingCustomers.getSelectedIndex();
        if (idx < 0 || customerIdsList.isEmpty()) return;

        String targetId = customerIdsList.get(idx);
        String selectedTier = (String) cmbAccountTierSelection.getSelectedItem(); 
        String decisionAction = (String) cmbApprovalAction.getSelectedItem();
        String finalLimitStr = txtMaxLendAmount.getText().trim();
        String justificationNotes = txtNotesReason.getText().trim();

        File dbFile = new File("customer_database.txt");
        ArrayList<String> memoryBufferLines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            boolean processingTargetBlock = false;
            
            boolean hasTierLine = false;
            boolean hasDecisionLine = false;
            boolean hasLimitLine = false;
            boolean hasReasonLine = false;

            ArrayList<String> currentBlockBuffer = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                currentBlockBuffer.add(line);

                if (line.trim().startsWith("Registration ID:")) {
                    String blockId = line.replace("Registration ID:", "").trim();
                    if (blockId.equalsIgnoreCase(targetId)) {
                        processingTargetBlock = true;
                    }
                }

                if (processingTargetBlock) {
                    if (line.trim().startsWith("Account Tier:") || line.trim().startsWith("Social Tier:")) hasTierLine = true;
                    if (line.trim().startsWith("Approval Decision Status:")) hasDecisionLine = true;
                    if (line.trim().startsWith("Assigned Credit Limit allowance:")) hasLimitLine = true;
                    if (line.trim().startsWith("Underwriter Analytics Reason:")) hasReasonLine = true;
                }

                if (line.trim().startsWith("=========================================")) {
                    if (processingTargetBlock) {
                        for (int i = 0; i < currentBlockBuffer.size(); i++) {
                            String targetStr = currentBlockBuffer.get(i);
                            
                            if (targetStr.startsWith("Account Tier:")) {
                                currentBlockBuffer.set(i, "Account Tier: " + selectedTier);
                            } else if (targetStr.startsWith("Social Tier:")) {
                                currentBlockBuffer.set(i, "Social Tier: " + selectedTier);
                            } else if (targetStr.startsWith("Approval Decision Status:")) {
                                currentBlockBuffer.set(i, "Approval Decision Status: " + decisionAction);
                            } else if (targetStr.startsWith("Assigned Credit Limit allowance:")) {
                                currentBlockBuffer.set(i, "Assigned Credit Limit allowance: " + finalLimitStr);
                            } else if (targetStr.startsWith("Underwriter Analytics Reason:")) {
                                currentBlockBuffer.set(i, "Underwriter Analytics Reason: " + justificationNotes);
                            }
                        }

                        // FIXED: If the database block completely lacks an explicit Tier descriptor row, we inject it directly into the file layout now
                        int insertionPoint = currentBlockBuffer.size() - 1; 
                        if (!hasReasonLine) currentBlockBuffer.add(insertionPoint, "Underwriter Analytics Reason: " + justificationNotes);
                        if (!hasLimitLine) currentBlockBuffer.add(insertionPoint, "Assigned Credit Limit allowance: " + finalLimitStr);
                        if (!hasDecisionLine) currentBlockBuffer.add(insertionPoint, "Approval Decision Status: " + decisionAction);
                        if (!hasTierLine) currentBlockBuffer.add(insertionPoint, "Account Tier: " + selectedTier);
                    }

                    memoryBufferLines.addAll(currentBlockBuffer);
                    currentBlockBuffer.clear();
                    processingTargetBlock = false;
                    hasTierLine = false;
                    hasDecisionLine = false;
                    hasLimitLine = false;
                    hasReasonLine = false;
                }
            }
            memoryBufferLines.addAll(currentBlockBuffer);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Critical failure reading data stream: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, false))) {
            for (String outputLine : memoryBufferLines) {
                writer.write(outputLine);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Critical failure writing data block changes: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, "Database updated successfully!\nDecision status and modified tier classification recorded inside customer_database.txt.", "Underwriting System Synced", JOptionPane.INFORMATION_MESSAGE);
        loadPendingDatabaseProfiles(); 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerApprovalGUI().setVisible(true));
    }
}