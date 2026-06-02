package com.customerregistration;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RoleRegistry {

    // Main entry point for NetBeans Project Run configurations
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RoleRegistryDashboard().setVisible(true);
        });
    }

    public static class RegistryEngine {
        public static final String ROLE_MANAGER  = "MAN";
        public static final String ROLE_CLERK    = "CLE";
        public static final String ROLE_AUDITOR  = "AUD";
        public static final String ROLE_OFFICER  = "OFC";

        private Map<String, String> rolePrefixMap;
        private Map<String, Integer> roleSequenceMap;
        private Map<String, String> userRegistry;
        private Map<String, String> deactivationLog;

        public RegistryEngine() {
            rolePrefixMap = new HashMap<>();
            rolePrefixMap.put("MANAGER",  ROLE_MANAGER);
            rolePrefixMap.put("CLERK",    ROLE_CLERK);
            rolePrefixMap.put("AUDITOR",  ROLE_AUDITOR);
            rolePrefixMap.put("OFFICER",  ROLE_OFFICER);

            roleSequenceMap = new HashMap<>();
            roleSequenceMap.put("MANAGER",  0);
            roleSequenceMap.put("CLERK",    0);
            roleSequenceMap.put("AUDITOR",  0);
            roleSequenceMap.put("OFFICER",  0);

            userRegistry = new HashMap<>();
            deactivationLog = new HashMap<>();
        }

        public synchronized String registerNewUser(String username, String roleName) {
            String upperRole = roleName.toUpperCase();
            if (!rolePrefixMap.containsKey(upperRole)) {
                throw new IllegalArgumentException("Invalid internal system role assignment.");
            }

            int currentSeq = roleSequenceMap.get(upperRole) + 1;
            roleSequenceMap.put(upperRole, currentSeq);

            String prefix = rolePrefixMap.get(upperRole);
            String generatedId = String.format("%s-%04d", prefix, currentSeq);

            userRegistry.put(generatedId, upperRole);
            return generatedId;
        }

        public synchronized boolean revokeAccess(String id) {
            if (userRegistry.containsKey(id)) {
                userRegistry.remove(id);
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                deactivationLog.put(id, timestamp);
                return true;
            }
            return false;
        }

        public String getRolePrefix(String role) {
            return rolePrefixMap.get(role.toUpperCase());
        }

        public int getRegisteredCount(String role) {
            return roleSequenceMap.getOrDefault(role.toUpperCase(), 0);
        }
    }
}

class RoleRegistryDashboard extends JFrame {
    private RoleRegistry.RegistryEngine registry;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbRole;
    private JTextField txtUsername;
    private JLabel lblPrefixVal, lblSeqVal, lblCountVal;

    public RoleRegistryDashboard() {
        registry = new RoleRegistry.RegistryEngine();
        setTitle("System Security & Identity Registry Module");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildLayout();
        refreshPreview();
    }

    private void buildLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 8, 8));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Onboard Operational Staff"));

        pnlForm.add(new JLabel("Username / Name:"));
        txtUsername = new JTextField();
        pnlForm.add(txtUsername);

        pnlForm.add(new JLabel("Functional Assignment:"));
        cmbRole = new JComboBox<>(new String[]{"MANAGER", "CLERK", "AUDITOR", "OFFICER"});
        pnlForm.add(cmbRole);

        pnlForm.add(new JLabel("Resolved System Identifier Prefix:"));
        lblPrefixVal = new JLabel("???");
        pnlForm.add(lblPrefixVal);

        pnlForm.add(new JLabel("Assigned Structural Sequence Slot:"));
        lblSeqVal = new JLabel("0000");
        pnlForm.add(lblSeqVal);

        pnlForm.add(new JLabel("Active Registry Count (This Role):"));
        lblCountVal = new JLabel("0");
        pnlForm.add(lblCountVal);

        JButton btnProvision = new JButton("Provision System Access Identity");
        pnlForm.add(btnProvision);

        add(pnlForm, BorderLayout.WEST);

        String[] cols = {"Generated ID", "Operator Name", "Role Level", "Landing Endpoint"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable tblUsers = new JTable(tableModel);
        add(new JScrollPane(tblUsers), BorderLayout.CENTER);

        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDeactivate = new JButton("Revoke Account Permissions");
        pnlSouth.add(btnDeactivate);
        add(pnlSouth, BorderLayout.SOUTH);

        cmbRole.addActionListener(e -> refreshPreview());

        btnProvision.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String role = (String) cmbRole.getSelectedItem();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Operational identifier name cannot be empty.");
                return;
            }
            String generatedId = registry.registerNewUser(user, role);
            String targetRoute = resolveInterfaceTargetEndpoint(generatedId);
            tableModel.addRow(new Object[]{generatedId, user, role, targetRoute});
            txtUsername.setText("");
            refreshPreview();

            // Open the module switcher routing directly into your components
            promptModuleLauncher(user, role);
        });

        btnDeactivate.addActionListener(e -> {
            int selectedRow = tblUsers.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Select an account row from the registry log map to deactivate.");
                return;
            }
            String targetId = (String) tableModel.getValueAt(selectedRow, 0);
            if (registry.revokeAccess(targetId)) {
                tableModel.setValueAt("SUSPENDED / REVOKED", selectedRow, 3);
                JOptionPane.showMessageDialog(this, "System clearances wiped for account token ID: " + targetId);
            }
        });
    }

    private void promptModuleLauncher(String username, String role) {
        // Complete menu mapping selections directly to your actual files
        String[] options = {
            "1. Customer Registration (App)", 
            "2. Collateral Appraisal (AppraisalGUI)", 
            "3. Loan Repayments (RepaymentModuleGUI)",
            "4. Activity History Panel (CustomerActivityHistoryGUI)",
            "5. Satisfaction Analysis Dashboard",
            "Cancel / Stay Here"
        };
        
        int selection = JOptionPane.showOptionDialog(
            this,
            "User Identity Provisioned Successfully!\n\n" +
            "Operator: " + username + " (" + role + ")\n" +
            "Which localized team module component window would you like to load?",
            "Core Operations Gateway Hub",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        // Dynamically instantiates and opens the correct individual class interface windows
        switch (selection) {
            case 0:
                SwingUtilities.invokeLater(() -> new App().setVisible(true));
                break;
            case 1:
                SwingUtilities.invokeLater(() -> new AppraisalGUI().setVisible(true));
                break;
            case 2:
                SwingUtilities.invokeLater(() -> new RepaymentModuleGUI().setVisible(true));
                break;
            case 3:
                SwingUtilities.invokeLater(() -> new CustomerActivityHistoryGUI().setVisible(true));
                break;
            case 4:
                // Triggers your group's customer feedback metrics pane
                SwingUtilities.invokeLater(() -> new CustomerSatisfactionDashboard().setVisible(true));
                break;
            default:
                // User closed or hit cancel
                break;
        }
    }

    private String resolveInterfaceTargetEndpoint(String id) {
        if (id == null || id.length() < 3) return "/unknown";
        String prefix = id.substring(0, 3);
        switch (prefix) {
            case "MAN": return "/manager/dashboard";
            case "CLE": return "/clerk/dashboard";
            case "AUD": return "/auditor/dashboard";
            case "OFC": return "/officer/dashboard";
            default:    return "/unknown";
        }
    }

    private void refreshPreview() {
        String role   = (String) cmbRole.getSelectedItem();
        String prefix = registry.getRolePrefix(role);

        int nextSeq = registry.getRegisteredCount(role) + 1;

        lblPrefixVal.setText(prefix != null ? prefix : "???");
        lblSeqVal.setText(String.format("%04d", nextSeq));
        lblCountVal.setText(String.valueOf(registry.getRegisteredCount(role)));
    }
}