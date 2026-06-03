package com.customerregistration;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class LoanCalculator {

    // Constants 
    public static final double RATE_MIN = 0.01;
    public static final double RATE_MAX = 0.50;

    public static final int COMPOUND_MONTHLY = 12;
    public static final int COMPOUND_QUARTERLY = 4;
    public static final int COMPOUND_ANNUALLY = 1;

    public static final int MIN_TERM_MONTHS = 1;
    public static final int MAX_TERM_MONTHS = 360;

    // Private input fields 
    private double annualRate;
    private int termMonths;
    private int compoundFreq;
    private double appraisal;

    // Private result fields 
    private double principal;
    private double monthlyPayment;
    private double totalInterest;
    private double totalPaid;

    // Constructor 
    public LoanCalculator(double annualRate, int termMonths,
                          int compoundFreq, double appraisal) {
        this.annualRate = annualRate;
        this.termMonths = termMonths;
        this.compoundFreq = compoundFreq;
        this.appraisal = appraisal;

        calculatePrincipalFromAppraisal();
        calculateLoanTerms();
    }

    private void calculatePrincipalFromAppraisal() {
        this.principal = this.appraisal * 0.70;
    }

    private void calculateLoanTerms() {
        if (annualRate < RATE_MIN || annualRate > RATE_MAX) {
            throw new IllegalArgumentException("Interest rate out of structural system bounds.");
        }
        if (termMonths < MIN_TERM_MONTHS || termMonths > MAX_TERM_MONTHS) {
            throw new IllegalArgumentException("Term length out of compliance constraints limits.");
        }

        double periodicRate = annualRate / compoundFreq;
        int totalPeriods = termMonths; 

        if (periodicRate == 0) {
            this.monthlyPayment = principal / totalPeriods;
            this.totalPaid = principal;
            this.totalInterest = 0;
            return;
        }

        // Standard Amortization Math Equation Formulations 
        this.monthlyPayment = (principal * periodicRate * Math.pow(1 + periodicRate, totalPeriods)) /
                               (Math.pow(1 + periodicRate, totalPeriods) - 1);
        
        this.totalPaid = this.monthlyPayment * totalPeriods;
        this.totalInterest = this.totalPaid - this.principal;
    }

    public double calculateMonthlyPayment() { return monthlyPayment; }
    public double getTotalInterestPaid() { return totalInterest; }
    public double getTotalAmountPaid() { return totalPaid; }

    public double calculateCompoundInterest() {
        double p = principal;
        double r = annualRate;
        double n = compoundFreq;
        double t = (double) termMonths / 12.0;

        return p * Math.pow(1 + (r / n), n * t) - p;
    }

    public double getRemainingBalanceAfterMonths(int monthsPaid) {
        double periodicRate = annualRate / compoundFreq;
        double monthlyRate = annualRate / 12.0; 
        
        if (monthsPaid >= termMonths) return 0.0;
        return principal * Math.pow(1 + monthlyRate, monthsPaid) - 
               (monthlyPayment * (Math.pow(1 + monthlyRate, monthsPaid) - 1) / monthlyRate);
    }

    public boolean isFullyRepaid() { return totalPaid <= 0; }

    // Getters 
    public double getPrincipal() { return principal; }
    public double getAnnualRate() { return annualRate; }
    public int getTermMonths() { return termMonths; }
    public int getCompoundFreq() { return compoundFreq; }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoanCalculatorGUI().setVisible(true));
    }
}

// ============================================================================
// DATABASE-INTEGRATED GUI CLASS TERMINAL MODULE
// ============================================================================
class LoanCalculatorGUI extends JFrame {
    private JComboBox<String> customerDropdown;
    private final ArrayList<String> customerIds = new ArrayList<>();
    private final ArrayList<String> customerNames = new ArrayList<>();
    private final ArrayList<Double> customerBalances = new ArrayList<>();

    private JTextField txtRate, txtTerm, txtAppraisal;
    private JComboBox<String> cmbCompound;
    private JTextArea txtResult;
    private LoanCalculator activeEngineInstance;

    public LoanCalculatorGUI() {
        setTitle("Quantitative Loan Evaluation & Debt Structure Module");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(650, 600);
        setLocationRelativeTo(null);
        buildLayoutDeck();
        refreshDatabaseProfiles();
    }

    private void buildLayoutDeck() {
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Top Selection Bar
        JPanel topSelectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topSelectorPanel.setBorder(BorderFactory.createTitledBorder("Database Target Selector"));
        topSelectorPanel.add(new JLabel("Select Borrower Profile:"));
        customerDropdown = new JComboBox<>();
        customerDropdown.setPreferredSize(new Dimension(320, 26));
        customerDropdown.addActionListener(e -> displaySelectedUserInfo());
        topSelectorPanel.add(customerDropdown);
        mainPanel.add(topSelectorPanel, BorderLayout.NORTH);

        // Parameters Form Grid
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Financial Parameters Entry Matrix"));

        formPanel.add(new JLabel(" Annual Base Interest Rate (e.g. 0.12):"));
        txtRate = new JTextField("0.12");
        formPanel.add(txtRate);

        formPanel.add(new JLabel(" Amortization Term Duration (Months):"));
        txtTerm = new JTextField("60");
        formPanel.add(txtTerm);

        formPanel.add(new JLabel(" Collateral Asset Appraisal Value ($):"));
        txtAppraisal = new JTextField("500000");
        formPanel.add(txtAppraisal);

        formPanel.add(new JLabel(" Interest Compounding Interval:"));
        cmbCompound = new JComboBox<>(new String[]{"Monthly", "Quarterly", "Annually"});
        formPanel.add(cmbCompound);

        JButton btnRunMath = new JButton("Run Underwriting Analytics");
        btnRunMath.setBackground(new Color(41, 128, 185));
        btnRunMath.setForeground(Color.WHITE);
        btnRunMath.setFont(new Font("Arial", Font.BOLD, 12));
        btnRunMath.addActionListener(e -> processQuantitativeLoanCalculations());
        formPanel.add(btnRunMath);

        JButton btnIssueLoan = new JButton("Approve Loan & Issue Credit");
        btnIssueLoan.setBackground(new Color(39, 174, 96));
        btnIssueLoan.setForeground(Color.WHITE);
        btnIssueLoan.setFont(new Font("Arial", Font.BOLD, 12));
        btnIssueLoan.addActionListener(e -> commitLoanCreditToDatabaseFile());
        formPanel.add(btnIssueLoan);

        // Center Wrapper Output Area
        txtResult = new JTextArea();
        txtResult.setEditable(false);
        txtResult.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtResult.setBackground(new Color(248, 249, 250));
        
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBorder(BorderFactory.createTitledBorder("Amortization Credit Underwriting Report"));
        centerWrapper.add(new JScrollPane(txtResult), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, centerWrapper);
        splitPane.setDividerLocation(200);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void refreshDatabaseProfiles() {
        customerDropdown.removeAllItems();
        customerIds.clear();
        customerNames.clear();
        customerBalances.clear();

        File dbFile = new File("customer_database.txt");
        if (!dbFile.exists()) {
            customerDropdown.addItem("No customer_database.txt detected.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            String id = "", name = "";
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
                        customerDropdown.addItem(name + " (ID: " + id + ")");
                        customerIds.add(id);
                        customerNames.add(name);
                        customerBalances.add(balance);
                    }
                    id = ""; name = ""; balance = 0.0;
                }
            }
            // Tail block capture
            if (!id.isEmpty() && !name.isEmpty()) {
                customerDropdown.addItem(name + " (ID: " + id + ")");
                customerIds.add(id);
                customerNames.add(name);
                customerBalances.add(balance);
            }
        } catch (Exception ex) {
            txtResult.setText("Error reading tracking profile blocks: " + ex.getMessage());
        }

        if (customerDropdown.getItemCount() == 0) {
            customerDropdown.addItem("No Registered User Account Logs Found");
        }
    }

    private void displaySelectedUserInfo() {
        int idx = customerDropdown.getSelectedIndex();
        if (idx < 0 || customerIds.isEmpty() || idx >= customerIds.size()) return;

        txtResult.setText("Target Profile Account Loaded:\n" +
                "Name: " + customerNames.get(idx) + "\n" +
                "Registration Reference ID: " + customerIds.get(idx) + "\n" +
                "Current Database Balance: $" + String.format("%.2f", customerBalances.get(idx)) + "\n\n" +
                "Configure fields above and tap 'Run Underwriting Analytics' to calculate the loan metrics.");
    }

    private void processQuantitativeLoanCalculations() {
        try {
            double rawRate = Double.parseDouble(txtRate.getText().trim());
            int rawTerm = Integer.parseInt(txtTerm.getText().trim());
            double rawAppraisal = Double.parseDouble(txtAppraisal.getText().trim());
            
            int chosenFreq = LoanCalculator.COMPOUND_MONTHLY;
            String modeStr = (String) cmbCompound.getSelectedItem();
            if ("Quarterly".equals(modeStr)) {
                chosenFreq = LoanCalculator.COMPOUND_QUARTERLY;
            } else if ("Annually".equals(modeStr)) {
                chosenFreq = LoanCalculator.COMPOUND_ANNUALLY;
            }

            activeEngineInstance = new LoanCalculator(rawRate, rawTerm, chosenFreq, rawAppraisal);

            int idx = customerDropdown.getSelectedIndex();
            String activeName = (idx >= 0 && !customerNames.isEmpty()) ? customerNames.get(idx) : "N/A";

            StringBuilder ledgerReportBuffer = new StringBuilder();
            ledgerReportBuffer.append("==================================================\n");
            ledgerReportBuffer.append(String.format("  CREDIT PROFILE TARGET     : %s\n", activeName));
            ledgerReportBuffer.append(String.format("  COLLATERAL VALUATION BASE : $%,.2f\n", rawAppraisal));
            ledgerReportBuffer.append(String.format("  PRINCIPAL AMOUNT (70%% LTV): $%,.2f\n", activeEngineInstance.getPrincipal()));
            ledgerReportBuffer.append(String.format("  INTEREST COMPOUNDING MODE : %s\n", modeStr));
            ledgerReportBuffer.append("--------------------------------------------------\n");
            ledgerReportBuffer.append(String.format("  ESTIMATED MONTHLY MATURITY: $%,.2f\n", activeEngineInstance.calculateMonthlyPayment()));
            ledgerReportBuffer.append(String.format("  TOTAL LIFESPAN INTEREST    : $%,.2f\n", activeEngineInstance.getTotalInterestPaid()));
            ledgerReportBuffer.append(String.format("  GROSS MATURED REDEMPTION  : $%,.2f\n", activeEngineInstance.getTotalAmountPaid()));
            ledgerReportBuffer.append("==================================================\n");
            ledgerReportBuffer.append("Tap 'Approve Loan & Issue Credit' to write this Principal value to the database balance.");

            txtResult.setText(ledgerReportBuffer.toString());
        } catch (NumberFormatException nfe) {
            txtResult.setText("Data Mapping Exception:\nNumeric conversion parameters contain structural formatting inconsistencies.");
        } catch (IllegalArgumentException iae) {
            txtResult.setText("Compliance Bound Constraint Violation Checked:\n" + iae.getMessage());
        }
    }

    private void commitLoanCreditToDatabaseFile() {
        int idx = customerDropdown.getSelectedIndex();
        if (idx < 0 || customerIds.isEmpty() || activeEngineInstance == null) {
            JOptionPane.showMessageDialog(this, "Underwriting Failure: Please calculate standard loan parameters before executing credit transfers.", "Processing Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String targetId = customerIds.get(idx);
        double loanPrincipalAmount = activeEngineInstance.getPrincipal();
        double currentDatabaseBalance = customerBalances.get(idx);
        double updatedDatabaseBalance = currentDatabaseBalance + loanPrincipalAmount;

        File dbFile = new File("customer_database.txt");
        ArrayList<String> fileMemoryBuffer = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dbFile))) {
            String line;
            boolean processingTargetUserBlock = false;
            ArrayList<String> userBlockBuffer = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                userBlockBuffer.add(line);

                if (line.trim().startsWith("Registration ID:")) {
                    String extractedId = line.replace("Registration ID:", "").trim();
                    if (extractedId.equalsIgnoreCase(targetId)) {
                        processingTargetUserBlock = true;
                    }
                }

                if (line.trim().startsWith("=========================================")) {
                    if (processingTargetUserBlock) {
                        for (int i = 0; i < userBlockBuffer.size(); i++) {
                            if (userBlockBuffer.get(i).trim().startsWith("Balance:")) {
                                userBlockBuffer.set(i, "Balance: $" + String.format("%.2f", updatedDatabaseBalance));
                            }
                        }
                    }
                    fileMemoryBuffer.addAll(userBlockBuffer);
                    userBlockBuffer.clear();
                    processingTargetUserBlock = false;
                }
            }
            fileMemoryBuffer.addAll(userBlockBuffer); // Catch trailing sets

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "File read structural interruption error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Commit updated tracking metrics back to text storage file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile, false))) {
            for (String outputLine : fileMemoryBuffer) {
                writer.write(outputLine);
                writer.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "File storage persistence write stream error: " + ex.getMessage(), "System Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, String.format("Loan approved successfully!\nIssued $%,.2f principal credit to %s.\nNew Database Balance: $%,.2f", loanPrincipalAmount, customerNames.get(idx), updatedDatabaseBalance), "Credit Transfer Finalized", JOptionPane.INFORMATION_MESSAGE);
        
        refreshDatabaseProfiles(); // Reload records dropdown context variables
        activeEngineInstance = null;
        displaySelectedUserInfo();
    }
}