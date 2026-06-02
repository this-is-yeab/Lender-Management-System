package com.customerregistration;

import java.util.UUID;

public class CollateralAppraisal {

    // Immutable fields
    private final String id;
    private final String assetType;
    private final String ownerName;
    private final String valuationDate;

    // Editable fields
    private String appraiserName;
    private int conditionScore;
    private boolean hasEncumbrance;
    private String titleStatus;

    // Computed fields
    private double appraisedValue;
    private String borrowerType;

    private static final double DEFAULT_ENCUMBRANCE_HAIRCUT = 0.20;

    public CollateralAppraisal(String id, String assetType, String ownerName, String valuationDate) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset Identification ID cannot be blank.");
        }
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Asset validation failed: ID must be a valid structural UUID string.");
        }

        if (assetType == null || assetType.trim().isEmpty()) {
            throw new IllegalArgumentException("Asset Classification Type context cannot be empty.");
        }
        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Legal Owner identity field cannot be empty.");
        }
        if (valuationDate == null || valuationDate.trim().isEmpty()) {
            throw new IllegalArgumentException("Processing evaluation date timestamp cannot be empty.");
        }

        this.id = id;
        this.assetType = assetType;
        this.ownerName = ownerName;
        this.valuationDate = valuationDate;

        // Sensible tracking defaults
        this.appraiserName = "UNASSIGNED";
        this.conditionScore = 3; // Neutral median tier
        this.hasEncumbrance = false;
        this.titleStatus = "unknown";
        this.appraisedValue = 0.0;
        this.borrowerType = "Individual";
    }

    public static CollateralAppraisal fromExternalObject(ExternalAsset ext) {
        if (ext == null) {
            throw new IllegalArgumentException("External data frame mapping adapter target cannot be null.");
        }
        CollateralAppraisal instance = new CollateralAppraisal(ext.getId(), ext.getAssetType(), ext.getOwnerName(), ext.getValuationDate());
        instance.setAppraiserName(ext.getAppraiserName());
        return instance;
    }

    // Valuation Sub-Algorithms per Asset Classification Type
    public double valueRealEstate(double areaSqM, double pricePerSqM) {
        if (areaSqM <= 0 || pricePerSqM <= 0) return 0.0;
        return areaSqM * pricePerSqM;
    }

    public double valueVehicle(double basePrice, double mileage, int year) {
        if (basePrice <= 0) return 0.0;
        double ageFactor = Math.max(0.2, 1.0 - (2026 - year) * 0.08);
        double mileageFactor = Math.max(0.4, 1.0 - (mileage / 250000.0));
        return basePrice * ageFactor * mileageFactor;
    }

    public double valueEquipment(double purchaseCost, double usefulLifeYears, double currentAgeYears) {
        if (purchaseCost <= 0 || usefulLifeYears <= 0) return 0.0;
        if (currentAgeYears >= usefulLifeYears) return purchaseCost * 0.10; // Salvage floor value
        return purchaseCost * (1.0 - (currentAgeYears / usefulLifeYears));
    }

    public double valueInventory(double bookValue, double obsolescenceDiscountRate) {
        if (bookValue <= 0) return 0.0;
        double discount = Math.max(0.0, Math.min(1.0, obsolescenceDiscountRate));
        return bookValue * (1.0 - discount);
    }

    public double valueAccountsReceivable(double totalAmount, double collectionSuccessRate) {
        if (totalAmount <= 0) return 0.0;
        double rate = Math.max(0.0, Math.min(1.0, collectionSuccessRate));
        return totalAmount * rate;
    }

    public double valueSecurities(double marketValue) {
        if (marketValue <= 0) return 0.0;
        return marketValue * 0.90; // Standard 10% market volatility buffer haircut
    }

    public double valueIntellectualProperty(double annualRoyaltyIncome, double capitalizationRate) {
        if (annualRoyaltyIncome <= 0 || capitalizationRate <= 0) return 0.0;
        return annualRoyaltyIncome / capitalizationRate;
    }

    public double valueJewelryOrArt(double auctionReservePrice, double rarityMultiplier) {
        if (auctionReservePrice <= 0) return 0.0;
        double mult = Math.max(1.0, rarityMultiplier);
        return auctionReservePrice * mult;
    }

    public double valueCashDeposit(double statementBalance) {
        if (statementBalance <= 0) return 0.0;
        return statementBalance; // Cash features a absolute risk profile conversion factor of 1.0
    }

    public double valueGoodwill(double averageExcessEarnings, double capitalizationMultiplier) {
        if (averageExcessEarnings <= 0 || capitalizationMultiplier <= 0) return 0.0;
        return averageExcessEarnings * capitalizationMultiplier;
    }

    // Core Pipeline Processing Strategy Action
    public double appraise(double baseValue) {
        if (baseValue <= 0) {
            this.appraisedValue = 0.0;
            return 0.0;
        }

        double val = baseValue;

        // Factor 1: Quality Condition Weighting Multipliers
        switch (this.conditionScore) {
            case 5:  val *= 1.05; break; // Excellent premium
            case 4:  val *= 1.00; break; // Good standard
            case 3:  val *= 0.90; break; // Fair deduction
            case 2:  val *= 0.75; break; // Poor deduction
            case 1:  val *= 0.50; break; // Damaged liquidation deduction
            default: val *= 0.80; break;
        }

        // Factor 2: Legal Title Cleanliness Validation
        if (this.titleStatus != null) {
            String ts = this.titleStatus.trim().toLowerCase();
            if (ts.equals("disputed")) {
                val *= 0.60; // Severe 40% clean title risk structural haircut
            } else if (ts.equals("missing")) {
                val *= 0.40; // Critical 60% verification penalty haircut
            }
        }

        // Factor 3: Asset Claim Encumbrance Adjustments
        if (this.hasEncumbrance) {
            val *= (1.0 - DEFAULT_ENCUMBRANCE_HAIRCUT);
        }

        this.appraisedValue = Math.max(0.0, val);
        return this.appraisedValue;
    }

    public String generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("      COLLATERAL APPRAISAL AUDIT REPORT  \n");
        sb.append("=========================================\n");
        sb.append(String.format("Asset Token ID    : %s\n", id));
        sb.append(String.format("Classification     : %s\n", assetType));
        sb.append(String.format("Legal Owner Target : %s\n", ownerName));
        sb.append(String.format("Borrower Account   : %s\n", borrowerType));
        sb.append(String.format("Appraiser Token    : %s\n", appraiserName));
        sb.append(String.format("Evaluation Date    : %s\n", valuationDate));
        sb.append("-----------------------------------------\n");
        sb.append(String.format("Asset Condition    : Tier %d/5\n", conditionScore));
        sb.append(String.format("Title Verification : Status [%s]\n", titleStatus));
        sb.append(String.format("Has Liens/Claims   : %b\n", hasEncumbrance));
        sb.append("=========================================\n");
        sb.append(String.format("FINAL LIQUID VALUE : $%,.2f\n", appraisedValue));
        sb.append("=========================================\n");
        return sb.toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getAssetType() { return assetType; }
    public String getOwnerName() { return ownerName; }
    public String getValuationDate() { return valuationDate; }
    public String getAppraiserName() { return appraiserName; }
    public void setAppraiserName(String name) { this.appraiserName = name; }
    public int getConditionScore() { return conditionScore; }
    public void setConditionScore(int score) { this.conditionScore = score; }
    public boolean isHasEncumbrance() { return hasEncumbrance; }
    public void setHasEncumbrance(boolean flag) { this.hasEncumbrance = flag; }
    public String getTitleStatus() { return titleStatus; }
    public void setTitleStatus(String status) { this.titleStatus = status; }
    public double getAppraisedValue() { return appraisedValue; }
    public void setBorrowerType(String type) { this.borrowerType = type; }
    public String getBorrowerType() { return borrowerType; }

    public static class ExternalAsset {
        private final String id;
        private final String assetType;
        private final String ownerName;
        private final String valuationDate;
        private final String appraiserName;

        public ExternalAsset(String id, String assetType, String ownerName, String valuationDate, String appraiserName) {
            this.id = id;
            this.assetType = assetType;
            this.ownerName = ownerName;
            this.valuationDate = valuationDate;
            this.appraiserName = appraiserName;
        }
        public String getId() { return id; }
        public String getAssetType() { return assetType; }
        public String getOwnerName() { return ownerName; }
        public String getValuationDate() { return valuationDate; }
        public String getAppraiserName() { return appraiserName; }
    }

    public static void main(String[] args) {
        ExternalAsset ext = new ExternalAsset(UUID.randomUUID().toString(), "RealEstate", "Yabu", "2026-06-02", "Appraiser Alpha");
        CollateralAppraisal appraisal = CollateralAppraisal.fromExternalObject(ext);
        appraisal.setConditionScore(4);
        appraisal.setTitleStatus("clear");
        appraisal.setHasEncumbrance(false);
        double base = appraisal.valueRealEstate(120.0, 800.0);
        appraisal.appraise(base);
        System.out.println(appraisal.generateReport());
    }
}