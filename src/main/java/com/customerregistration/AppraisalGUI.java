package com.customerregistration;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

public class AppraisalGUI extends JFrame {

    private JComboBox<String> borrowerTypeCombo, titleCombo, assetSelectorCombo;
    private JTextField idField, assetTypeField, ownerField, valuationDateField, appraiserField;
    private JSpinner conditionSpinner;
    private JCheckBox encumbranceCheck;
    private JTextArea reportArea;

    // Sub-panel reference parameters for card swapping actions
    private JPanel dynamicAssetConfigCardPanel;
    private CardLayout cardLayoutEngine;

    // Form input field configurations maps
    private JTextField areaField, pricePerSqMField;
    private JTextField vehiclePriceField, mileageField, vehicleYearField;
    private JTextField equipCostField, equipLifeField, equipAgeField;
    private JTextField invBookField, invDiscountField;
    private JTextField recTotalField, recRateField;
    private JTextField secMarketField;
    private JTextField ipIncomeField, ipRateField;
    private JTextField artPriceField, artRarityField;
    private JTextField cashBalanceField;
    private JTextField goodwillEarningsField, goodwillMultiplierField;

    public AppraisalGUI() {
        setTitle("Collateral Asset Appraisal & Risk Assessment Workspace");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 680);
        setLocationRelativeTo(null);
        buildLayoutStructure();
    }

    private void buildLayoutStructure() {
        setLayout(new BorderLayout(12, 12));

        // West side core parameter controls pane
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel metadataGrid = new JPanel(new GridLayout(6, 2, 6, 6));
        metadataGrid.setBorder(BorderFactory.createTitledBorder("Core Registry Parameters"));

        metadataGrid.add(new JLabel("Identification ID:"));
        idField = new JTextField(UUID.randomUUID().toString());
        idField.setEditable(false);
        metadataGrid.add(idField);

        metadataGrid.add(new JLabel("Asset Category Type:"));
        assetTypeField = new JTextField("RealEstate");
        metadataGrid.add(assetTypeField);

        metadataGrid.add(new JLabel("Owner Target Full Name:"));
        ownerField = new JTextField();
        metadataGrid.add(ownerField);

        metadataGrid.add(new JLabel("Borrower Structural Grouping:"));
        borrowerTypeCombo = new JComboBox<>(new String[]{"Individual", "Group", "Company"});
        metadataGrid.add(borrowerTypeCombo);

        metadataGrid.add(new JLabel("Valuation System Timestamp:"));
        valuationDateField = new JTextField(LocalDate.now().toString());
        metadataGrid.add(valuationDateField);

        metadataGrid.add(new JLabel("Responsible Appraiser Name:"));
        appraiserField = new JTextField();
        metadataGrid.add(appraiserField);

        westPanel.add(metadataGrid);
        westPanel.add(Box.createVerticalStrut(10));

        // Adjustments modification controls grid
        JPanel adjustmentGrid = new JPanel(new GridLayout(3, 2, 6, 6));
        adjustmentGrid.setBorder(BorderFactory.createTitledBorder("Condition & Title Directives"));

        adjustmentGrid.add(new JLabel("Condition Factor Rating (1-5):"));
        conditionSpinner = new JSpinner(new SpinnerNumberModel(4, 1, 5, 1));
        adjustmentGrid.add(conditionSpinner);

        adjustmentGrid.add(new JLabel("Active Liens / Claims:"));
        encumbranceCheck = new JCheckBox("Encumbered");
        adjustmentGrid.add(encumbranceCheck);

        adjustmentGrid.add(new JLabel("Legal Title Status Registration:"));
        titleCombo = new JComboBox<>(new String[]{"clear", "disputed", "missing"});
        adjustmentGrid.add(titleCombo);

        westPanel.add(adjustmentGrid);
        westPanel.add(Box.createVerticalStrut(15));

        // Card Selection Control Form Row
        JPanel selectionRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionRowPanel.add(new JLabel("Select Valuation Logic Architecture: "));
        assetSelectorCombo = new JComboBox<>(new String[]{
                "RealEstate", "Vehicle", "Equipment", "Inventory", "Receivables",
                "Securities", "IntellectualProperty", "ArtOrJewelry", "Cash", "Goodwill"
        });
        selectionRowPanel.add(assetSelectorCombo);
        westPanel.add(selectionRowPanel);

        // Volatile Card Swapping Sub Panels Initialization
        cardLayoutEngine = new CardLayout();
        dynamicAssetConfigCardPanel = new JPanel(cardLayoutEngine);
        dynamicAssetConfigCardPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Dynamic Mathematical Mapping Inputs", TitledBorder.LEFT, TitledBorder.TOP));

        initValuationSubCards();
        westPanel.add(dynamicAssetConfigCardPanel);

        add(westPanel, BorderLayout.WEST);

        // Center console reporting output views panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Analytical Report Console Log"));
        reportArea = new JTextArea("Ready to run diagnostics...");
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        centerPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // South global processing actions triggers bar
        JPanel southActionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnAppraise = new JButton("Execute Appraisal Metrics Pipeline");
        JButton btnReset = new JButton("Reset Form Configuration");
        btnAppraise.setFont(new Font("Arial", Font.BOLD, 12));

        southActionBar.add(btnReset);
        southActionBar.add(btnAppraise);
        add(southActionBar, BorderLayout.SOUTH);

        // Wire Event Listeners
        assetSelectorCombo.addActionListener(e -> {
            String choice = (String) assetSelectorCombo.getSelectedItem();
            assetTypeField.setText(choice);
            cardLayoutEngine.show(dynamicAssetConfigCardPanel, choice);
        });

        btnReset.addActionListener(e -> {
            idField.setText(UUID.randomUUID().toString());
            ownerField.setText("");
            appraiserField.setText("");
            conditionSpinner.setValue(4);
            encumbranceCheck.setSelected(false);
            titleCombo.setSelectedIndex(0);
            assetSelectorCombo.setSelectedIndex(0);
            reportArea.setText("Panel inputs restored to safe tracking state parameters defaults.");
        });

        btnAppraise.addActionListener(e -> handleExecutionPipelineCalculationTrigger());
    }

    private void initValuationSubCards() {
        // Card 1: RealEstate
        JPanel cardRealEstate = new JPanel(new GridLayout(2, 2, 4, 4));
        cardRealEstate.add(new JLabel("Total Dimension Area (SqM):")); areaField = new JTextField("120.0"); cardRealEstate.add(areaField);
        cardRealEstate.add(new JLabel("Target Market Rate per SqM ($):")); pricePerSqMField = new JTextField("800.0"); cardRealEstate.add(pricePerSqMField);
        dynamicAssetConfigCardPanel.add(cardRealEstate, "RealEstate");

        // Card 2: Vehicle
        JPanel cardVehicle = new JPanel(new GridLayout(3, 2, 4, 4));
        cardVehicle.add(new JLabel("Original Purchase Book Value ($):")); vehiclePriceField = new JTextField("25000.0"); cardVehicle.add(vehiclePriceField);
        cardVehicle.add(new JLabel("Total Accumulated Mileage (Km):")); mileageField = new JTextField("45000.0"); cardVehicle.add(mileageField);
        cardVehicle.add(new JLabel("Manufacturing Year (YYYY):")); vehicleYearField = new JTextField("2020"); cardVehicle.add(vehicleYearField);
        dynamicAssetConfigCardPanel.add(cardVehicle, "Vehicle");

        // Card 3: Equipment
        JPanel cardEquipment = new JPanel(new GridLayout(3, 2, 4, 4));
        cardEquipment.add(new JLabel("Historical Purchase Cost ($):")); equipCostField = new JTextField("15000.0"); cardEquipment.add(equipCostField);
        cardEquipment.add(new JLabel("Useful Lifecycle Span (Years):")); equipLifeField = new JTextField("10.0"); cardEquipment.add(equipLifeField);
        cardEquipment.add(new JLabel("Current Operational Age (Years):")); equipAgeField = new JTextField("3.5"); cardEquipment.add(equipAgeField);
        dynamicAssetConfigCardPanel.add(cardEquipment, "Equipment");

        // Card 4: Inventory
        JPanel cardInventory = new JPanel(new GridLayout(2, 2, 4, 4));
        cardInventory.add(new JLabel("Current Registry Book Value ($):")); invBookField = new JTextField("50000.0"); cardInventory.add(invBookField);
        cardInventory.add(new JLabel("Obsolescence Discount Rate (0-1):")); invDiscountField = new JTextField("0.15"); cardInventory.add(invDiscountField);
        dynamicAssetConfigCardPanel.add(cardInventory, "Inventory");

        // Card 5: Receivables
        JPanel cardReceivables = new JPanel(new GridLayout(2, 2, 4, 4));
        cardReceivables.add(new JLabel("Total Ledger Invoice Claims ($):")); recTotalField = new JTextField("32000.0"); cardReceivables.add(recTotalField);
        cardReceivables.add(new JLabel("Historical Recovery Rate (0-1):")); recRateField = new JTextField("0.85"); cardReceivables.add(recRateField);
        dynamicAssetConfigCardPanel.add(cardReceivables, "Receivables");

        // Card 6: Securities
        JPanel cardSecurities = new JPanel(new GridLayout(1, 2, 4, 4));
        cardSecurities.add(new JLabel("Market Trading Value ($):")); secMarketField = new JTextField("75000.0"); cardSecurities.add(secMarketField);
        dynamicAssetConfigCardPanel.add(cardSecurities, "Securities");

        // Card 7: IntellectualProperty
        JPanel cardIP = new JPanel(new GridLayout(2, 2, 4, 4));
        cardIP.add(new JLabel("Projected Annual Royalty Flow ($):")); ipIncomeField = new JTextField("12000.0"); cardIP.add(ipIncomeField);
        cardIP.add(new JLabel("Capitalization Threshold Rate (0-1):")); ipRateField = new JTextField("0.10"); cardIP.add(ipRateField);
        dynamicAssetConfigCardPanel.add(cardIP, "IntellectualProperty");

        // Card 8: ArtOrJewelry
        JPanel cardArt = new JPanel(new GridLayout(2, 2, 4, 4));
        cardArt.add(new JLabel("Auction Appraiser Base Reserve ($):")); artPriceField = new JTextField("8500.0"); cardArt.add(artPriceField);
        cardArt.add(new JLabel("Rarity Context Factor Multiplier (>=1.0):")); artRarityField = new JTextField("1.25"); cardArt.add(artRarityField);
        dynamicAssetConfigCardPanel.add(cardArt, "ArtOrJewelry");

        // Card 9: Cash
        JPanel cardCash = new JPanel(new GridLayout(1, 2, 4, 4));
        cardCash.add(new JLabel("Verified Statement Liquid Balance ($):")); cashBalanceField = new JTextField("12500.0"); cardCash.add(cashBalanceField);
        dynamicAssetConfigCardPanel.add(cardCash, "Cash");

        // Card 10: Goodwill
        JPanel cardGoodwill = new JPanel(new GridLayout(2, 2, 4, 4));
        cardGoodwill.add(new JLabel("Average Excess Earnings Metrics ($):")); goodwillEarningsField = new JTextField("5000.0"); cardGoodwill.add(goodwillEarningsField);
        cardGoodwill.add(new JLabel("Capitalization Factor Multiplier:")); goodwillMultiplierField = new JTextField("4.0"); cardGoodwill.add(goodwillMultiplierField);
        dynamicAssetConfigCardPanel.add(cardGoodwill, "Goodwill");
    }

    private void handleExecutionPipelineCalculationTrigger() {
        try {
            String idVal = idField.getText().trim();
            String typeVal = assetTypeField.getText().trim();
            String ownerVal = ownerField.getText().trim();
            String dateVal = valuationDateField.getText().trim();
            String appraiserVal = appraiserField.getText().trim();

            if (ownerVal.isEmpty() || appraiserVal.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Validation Error: Legal Owner and Appraiser Name are required fields.", "Input Mismatch", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Factory constructor generation
            CollateralAppraisal appraisal = new CollateralAppraisal(idVal, typeVal, ownerVal, dateVal);
            appraisal.setAppraiserName(appraiserVal);
            appraisal.setConditionScore((Integer) conditionSpinner.getValue());
            appraisal.setHasEncumbrance(encumbranceCheck.isSelected());
            appraisal.setTitleStatus((String) titleCombo.getSelectedItem());
            appraisal.setBorrowerType((String) borrowerTypeCombo.getSelectedItem());

            double baseValue = 0.0;
            String mode = (String) assetSelectorCombo.getSelectedItem();

            switch (mode) {
                case "RealEstate":
                    baseValue = appraisal.valueRealEstate(Double.parseDouble(areaField.getText()), Double.parseDouble(pricePerSqMField.getText()));
                    break;
                case "Vehicle":
                    baseValue = appraisal.valueVehicle(Double.parseDouble(vehiclePriceField.getText()), Double.parseDouble(mileageField.getText()), Integer.parseInt(vehicleYearField.getText()));
                    break;
                case "Equipment":
                    baseValue = appraisal.valueEquipment(Double.parseDouble(equipCostField.getText()), Double.parseDouble(equipLifeField.getText()), Double.parseDouble(equipAgeField.getText()));
                    break;
                case "Inventory":
                    baseValue = appraisal.valueInventory(Double.parseDouble(invBookField.getText()), Double.parseDouble(invDiscountField.getText()));
                    break;
                case "Receivables":
                    baseValue = appraisal.valueAccountsReceivable(Double.parseDouble(recTotalField.getText()), Double.parseDouble(recRateField.getText()));
                    break;
                case "Securities":
                    baseValue = appraisal.valueSecurities(Double.parseDouble(secMarketField.getText()));
                    break;
                case "IntellectualProperty":
                    baseValue = appraisal.valueIntellectualProperty(Double.parseDouble(ipIncomeField.getText()), Double.parseDouble(ipRateField.getText()));
                    break;
                case "ArtOrJewelry":
                    baseValue = appraisal.valueJewelryOrArt(Double.parseDouble(artPriceField.getText()), Double.parseDouble(artRarityField.getText()));
                    break;
                case "Cash":
                    baseValue = appraisal.valueCashDeposit(Double.parseDouble(cashBalanceField.getText()));
                    break;
                case "Goodwill":
                    baseValue = appraisal.valueGoodwill(Double.parseDouble(goodwillEarningsField.getText()), Double.parseDouble(goodwillMultiplierField.getText()));
                    break;
            }

            appraisal.appraise(baseValue);
            reportArea.setText(appraisal.generateReport());

        } catch (NumberFormatException nfe) {
            reportArea.setText("Mathematical processing parsing break: Please enter valid format integers/doubles values context.\n" + nfe.getMessage());
        } catch (IllegalArgumentException iae) {
            reportArea.setText("Operational validation error constraint violation check: " + iae.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppraisalGUI().setVisible(true));
    }
}