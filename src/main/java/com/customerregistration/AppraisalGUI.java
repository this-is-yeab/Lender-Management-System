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

    private JPanel dynamicAssetConfigCardPanel;
    private CardLayout cardLayoutEngine;

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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(950, 680);
        setLocationRelativeTo(null);
        buildMainFormLayout();
    }

    private void buildMainFormLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel topHeaderPanel = new JPanel(new GridLayout(4, 4, 8, 8));
        topHeaderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Collateral Dossier Metadata Assignments", TitledBorder.LEFT, TitledBorder.TOP));

        topHeaderPanel.add(new JLabel("Customer Registration ID:"));
        idField = new JTextField();
        topHeaderPanel.add(idField);

        topHeaderPanel.add(new JLabel("Collateral Asset Type:"));
        assetTypeField = new JTextField("RealEstate");
        assetTypeField.setEditable(false);
        topHeaderPanel.add(assetTypeField);

        topHeaderPanel.add(new JLabel("Legal Owner Profile Name:"));
        ownerField = new JTextField();
        topHeaderPanel.add(ownerField);

        topHeaderPanel.add(new JLabel("Valuation Log Date (YYYY-MM-DD):"));
        valuationDateField = new JTextField(LocalDate.now().toString());
        topHeaderPanel.add(valuationDateField);

        topHeaderPanel.add(new JLabel("Assigned Appraiser Agent Name:"));
        appraiserField = new JTextField();
        topHeaderPanel.add(appraiserField);

        topHeaderPanel.add(new JLabel("Borrower Segment Profile:"));
        borrowerTypeCombo = new JComboBox<>(new String[]{"Individual", "Group", "Company"});
        topHeaderPanel.add(borrowerTypeCombo);

        topHeaderPanel.add(new JLabel("Asset Structural Condition Score (1-5):"));
        conditionSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        topHeaderPanel.add(conditionSpinner);

        topHeaderPanel.add(new JLabel("Collateral Ownership Title Status:"));
        titleCombo = new JComboBox<>(new String[]{"clear", "disputed", "encumbered"});
        topHeaderPanel.add(titleCombo);

        add(topHeaderPanel, BorderLayout.NORTH);

        cardLayoutEngine = new CardLayout();
        dynamicAssetConfigCardPanel = new JPanel(cardLayoutEngine);
        dynamicAssetConfigCardPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Dynamic Valuation Parameter Configuration Models", TitledBorder.LEFT, TitledBorder.TOP));

        buildAssetConfigurationCards();
        add(dynamicAssetConfigCardPanel, BorderLayout.CENTER);

        JPanel westControlDeckPanel = new JPanel(new BorderLayout(5, 5));
        westControlDeckPanel.setPreferredSize(new Dimension(300, 400));
        westControlDeckPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));

        String[] coreAssetCategories = {"RealEstate", "Vehicle", "Equipment", "Inventory", "AccountsReceivable", "Securities", "IntellectualProperty", "ArtOrJewelry", "Cash", "Goodwill"};
        assetSelectorCombo = new JComboBox<>(coreAssetCategories);
        assetSelectorCombo.setFont(new Font("Arial", Font.BOLD, 13));
        assetSelectorCombo.addActionListener(e -> {
            String selectedAsset = (String) assetSelectorCombo.getSelectedItem();
            assetTypeField.setText(selectedAsset);
            cardLayoutEngine.show(dynamicAssetConfigCardPanel, selectedAsset);
        });

        JPanel selectionWrapperPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        selectionWrapperPanel.add(new JLabel("Select Valuation Class Profile:"));
        selectionWrapperPanel.add(assetSelectorCombo);
        encumbranceCheck = new JCheckBox("Asset Has Active External Liens/Encumbrances");
        selectionWrapperPanel.add(encumbranceCheck);

        westControlDeckPanel.add(selectionWrapperPanel, BorderLayout.NORTH);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        westControlDeckPanel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton executeAppraisalCalcButton = new JButton("Execute Strategic Collateral Evaluation & Save");
        executeAppraisalCalcButton.setFont(new Font("Arial", Font.BOLD, 14));
        executeAppraisalCalcButton.setBackground(new Color(41, 128, 185));
        executeAppraisalCalcButton.setForeground(Color.WHITE);
        executeAppraisalCalcButton.addActionListener(e -> calculateAndSyncAppraisalToDatabase());
        westControlDeckPanel.add(executeAppraisalCalcButton, BorderLayout.SOUTH);

        add(westControlDeckPanel, BorderLayout.WEST);
    }

    private void buildAssetConfigurationCards() {
        // 1. RealEstate Card Panel Setup
        JPanel realEstateCardPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        realEstateCardPanel.add(new JLabel("Total Parcel Area Size (Sq Meters):"));
        areaField = new JTextField("250");
        realEstateCardPanel.add(areaField);
        realEstateCardPanel.add(new JLabel("Regional Price Index Rate ($ per SqM):"));
        pricePerSqMField = new JTextField("1200");
        realEstateCardPanel.add(pricePerSqMField);
        dynamicAssetConfigCardPanel.add(realEstateCardPanel, "RealEstate");

        // 2. Vehicle Card Panel Setup
        JPanel vehicleCardPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        vehicleCardPanel.add(new JLabel("Original Purchase Booking Price ($):"));
        vehiclePriceField = new JTextField("35000");
        vehicleCardPanel.add(vehiclePriceField);
        vehicleCardPanel.add(new JLabel("Recorded Total Mileage Odometer Count (Km):"));
        mileageField = new JTextField("45000");
        vehicleCardPanel.add(mileageField);
        vehicleCardPanel.add(new JLabel("Vehicle Manufacture Release Year:"));
        vehicleYearField = new JTextField("2021");
        vehicleCardPanel.add(vehicleYearField);
        dynamicAssetConfigCardPanel.add(vehicleCardPanel, "Vehicle");

        // 3. Equipment Card Panel Setup
        JPanel equipmentCardPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        equipmentCardPanel.add(new JLabel("Historical Procurement Capital Cost ($):"));
        equipCostField = new JTextField("150000");
        equipmentCardPanel.add(equipCostField);
        equipmentCardPanel.add(new JLabel("Expected Salvage Asset Useful Lifespan (Years):"));
        equipLifeField = new JTextField("10");
        equipmentCardPanel.add(equipLifeField);
        equipmentCardPanel.add(new JLabel("Current Machinery Operations Age (Years):"));
        equipAgeField = new JTextField("3");
        equipmentCardPanel.add(equipAgeField);
        dynamicAssetConfigCardPanel.add(equipmentCardPanel, "Equipment");

        // 4. Inventory Card Panel Setup
        JPanel inventoryCardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inventoryCardPanel.add(new JLabel("Stock Current Gross Book Cost Value ($):"));
        invBookField = new JTextField("85000");
        inventoryCardPanel.add(invBookField);
        inventoryCardPanel.add(new JLabel("Perishability Liquidation Discount Haircut Rate (0.0-1.0):"));
        invDiscountField = new JTextField("0.35");
        inventoryCardPanel.add(invDiscountField);
        dynamicAssetConfigCardPanel.add(inventoryCardPanel, "Inventory");

        // 5. AccountsReceivable Card Panel Setup
        JPanel arCardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        arCardPanel.add(new JLabel("Total Ledger Outstanding Invoice Value Balance ($):"));
        recTotalField = new JTextField("60000");
        arCardPanel.add(recTotalField);
        arCardPanel.add(new JLabel("Historical Default/Collectibility Loss Risk Weight (0.0-1.0):"));
        recRateField = new JTextField("0.15");
        arCardPanel.add(recRateField);
        dynamicAssetConfigCardPanel.add(arCardPanel, "AccountsReceivable");

        // 6. Securities Card Panel Setup
        JPanel securitiesCardPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        securitiesCardPanel.add(new JLabel("Market Trading Value Valuation Price Tag ($):"));
        secMarketField = new JTextField("125000");
        securitiesCardPanel.add(secMarketField);
        dynamicAssetConfigCardPanel.add(securitiesCardPanel, "Securities");

        // 7. IntellectualProperty Card Panel Setup
        JPanel ipCardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        ipCardPanel.add(new JLabel("Annual Net Royalty Inflow Returns Yield Generated ($):"));
        ipIncomeField = new JTextField("24000");
        ipCardPanel.add(ipIncomeField);
        ipCardPanel.add(new JLabel("Capitalization Risk Yield Multiplier Rate Factor (0.0-1.0):"));
        ipRateField = new JTextField("0.12");
        ipCardPanel.add(ipRateField);
        dynamicAssetConfigCardPanel.add(ipCardPanel, "IntellectualProperty");

        // 8. ArtOrJewelry Card Panel Setup
        JPanel artCardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        artCardPanel.add(new JLabel("Certified Acquisition Base Appraisal Cost ($):"));
        artPriceField = new JTextField("45000");
        artCardPanel.add(artPriceField);
        artCardPanel.add(new JLabel("Asset Rarity Appraisal Scarcity Index Premium (1.0-5.0):"));
        artRarityField = new JTextField("1.5");
        artCardPanel.add(artRarityField);
        dynamicAssetConfigCardPanel.add(artCardPanel, "ArtOrJewelry");

        // 9. Cash Card Panel Setup
        JPanel cashCardPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        cashCardPanel.add(new JLabel("Liquid Escrow Certificate Ledger Balance ($):"));
        cashBalanceField = new JTextField("50000");
        cashCardPanel.add(cashBalanceField);
        dynamicAssetConfigCardPanel.add(cashCardPanel, "Cash");

        // 10. Goodwill Card Panel Setup
        JPanel goodwillCardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        goodwillCardPanel.add(new JLabel("Excess Premium Annual Corporate Earnings Profile ($):"));
        goodwillEarningsField = new JTextField("30000");
        goodwillCardPanel.add(goodwillEarningsField);
        goodwillCardPanel.add(new JLabel("Strategic Brand Equity Scalability Multiplier (1-10):"));
        goodwillMultiplierField = new JTextField("4");
        goodwillCardPanel.add(goodwillMultiplierField);
        dynamicAssetConfigCardPanel.add(goodwillCardPanel, "Goodwill");
    }

    private void calculateAndSyncAppraisalToDatabase() {
        try {
            // Generate a valid mock structural UUID to initialize the underlying Appraisal business model instance
            String temporaryEngineUUID = UUID.randomUUID().toString();

            CollateralAppraisal appraisal = new CollateralAppraisal(
                    temporaryEngineUUID,
                    assetTypeField.getText(),
                    ownerField.getText().isEmpty() ? "Standard Applicant Profile" : ownerField.getText(),
                    valuationDateField.getText()
            );

            appraisal.setAppraiserName(appraiserField.getText().isEmpty() ? "Automated Risk Hub Engine" : appraiserField.getText());
            appraisal.setConditionScore((Integer) conditionSpinner.getValue());
            appraisal.setTitleStatus((String) titleCombo.getSelectedItem());
            appraisal.setHasEncumbrance(encumbranceCheck.isSelected());

            double baseValue = 0.0;
            String selectedModelType = (String) assetSelectorCombo.getSelectedItem();

            switch (selectedModelType) {
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
                case "AccountsReceivable":
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

            // --- DATABASE SYNC COMPONENT START ---
            double totalAppraisedAssetMetric = appraisal.getAppraisedValue();
            String targetCustomerRegID = idField.getText().trim();

            if (!targetCustomerRegID.isEmpty()) {
                CustomerRegistration dbEngine = new CustomerRegistration();
                boolean targetCustomerRecordFound = false;

                // Scan registration logs array elements to confirm customer row mapping existence
                for (CustomerInformation customer : dbEngine.getCustomerList()) {
                    if (customer.getRegistrationID().equalsIgnoreCase(targetCustomerRegID)) {
                        targetCustomerRecordFound = true;
                        break;
                    }
                }

                if (targetCustomerRecordFound) {
                    // Update field indexes inside customer_database.txt
                    dbEngine.updateCustomerInDatabase(targetCustomerRegID, totalAppraisedAssetMetric, selectedModelType);
                    reportArea.setText(appraisal.generateReport() + "\n\n✅ DATABASE STATUS: Updated successfully!");
                } else {
                    reportArea.setText(appraisal.generateReport() + "\n\n⚠️ DATABASE STATUS: Profile evaluated, but ID context was not found in 'customer_database.txt'.");
                }
            } else {
                reportArea.setText(appraisal.generateReport() + "\n\n⚠️ DATABASE STATUS: Locally processed. Provide a 'Customer Registration ID' value parameter to sync database metrics.");
            }
            // --- DATABASE SYNC COMPONENT END ---

        } catch (NumberFormatException nfe) {
            reportArea.setText("Mathematical processing parsing break: Please enter valid format values.\n" + nfe.getMessage());
        } catch (IllegalArgumentException iae) {
            reportArea.setText("Operational validation error constraint violation check: " + iae.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AppraisalGUI().setVisible(true));
    }
}