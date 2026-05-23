package com.customerregistration;
 public class CustomerApproval {

    // ===== Public Static Final Constants (System Flags) =====
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String CONDITIONAL = "conditional approval";

    // ===== Private Fields =====
    private CustomerInformation customer;
    private String approvalStatus;
    private String approvalReason;

    // ===== Constructor =====
    public CustomerApproval(CustomerInformation customer) {
        this.customer = customer;
        this.approvalStatus = "";
        this.approvalReason = "";
    }

    // ===== Core Evaluation =====
    public String evaluateApproval() {

        if (!checkCustomerStatus()) {
            approvalStatus = REJECTED;
            approvalReason = "Inactive customer";
            return approvalStatus;
        }

        if (!checkAgeEligibility()) {
            approvalStatus = REJECTED;
            approvalReason = "Age not eligible";
            return approvalStatus;
        }

        String tier = customer.getAccountTier();
        if (tier.equals(CustomerInformation.PLATINUM) || tier.equals(CustomerInformation.PREMIUM)) {
            approvalStatus = APPROVED;
            approvalReason = "Meets all requirements";
        } else if (tier.equals(CustomerInformation.GOLD)) {
            approvalStatus = APPROVED;
            approvalReason = "Standard approval";
        } else {
            approvalStatus = CONDITIONAL;
            approvalReason = "Normal tier - conditional approval";
        }

        return approvalStatus;
    }

    // ===== Private Validation Methods =====
    private boolean checkAgeEligibility() {
        String ageGroup = customer.getAgeGroup();
        return !ageGroup.equals(CustomerInformation.UNDER_18) && 
               !ageGroup.equals(CustomerInformation.AGE_60_PLUS);
    }

    private boolean checkCustomerStatus() {
        return customer.isActive();
    }

    // ===== Getters =====
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    // ===== Display Method =====
    public void displayApprovalResult() {
        System.out.println("Status: " + approvalStatus);
        System.out.println("Reason: " + approvalReason);
    }

    // ===== Manager Verification =====
    private boolean verifyManagerCredentials(String token) {
        return token != null && token.equals("ADMIN123");
    }

    // ===== Modify Customer Record =====
    public boolean modifyCustomerDataRecord(String token, String id, String field, String val) {

        if (!verifyManagerCredentials(token)) {
            return false;
        }

        if (!customer.getId().equals(id)) {
            return false;
        }

        switch (field.toLowerCase()) {
            case "name":
                customer.setName(val);
                break;
            case "balance":
                customer.setBalance(Double.parseDouble(val));
                break;
            case "age":
                customer.setAge(Integer.parseInt(val));
                break;
            default:
                return false;
        }

        writeAuditLogRecord(id, "Modified " + field);
        return true;
    }

    // ===== Audit Log =====
    private void writeAuditLogRecord(String mgrId, String action) {
        System.out.println("[AUDIT] Manager: " + mgrId + " | Action: " + action);
    }
}public class CustomerApproval {

    // ===== Public Static Final Constants (System Flags) =====
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String CONDITIONAL = "conditional approval";

    // ===== Private Fields =====
    private CustomerInformation customer;
    private String approvalStatus;
    private String approvalReason;

    // ===== Constructor =====
    public CustomerApproval(CustomerInformation customer) {
        this.customer = customer;
        this.approvalStatus =   
         "";
        this.approvalReason = "";
    }

    // ===== Core Evaluation =====
    public String evaluateApproval() {

        if (!checkCustomerStatus()) {
            approvalStatus = REJECTED;
            approvalReason = "Inactive customer";
            return approvalStatus;
        }

        if (!checkAgeEligibility()) {
            approvalStatus = REJECTED;
            approvalReason = "Age not eligible";
            return approvalStatus;
        }

        String tier = customer.getAccountTier();
        if (tier.equals(CustomerInformation.PLATINUM) || tier.equals(CustomerInformation.PREMIUM)) {
            approvalStatus = APPROVED;
            approvalReason = "Meets all requirements";
        } else if (tier.equals(CustomerInformation.GOLD)) {
            approvalStatus = APPROVED;
            approvalReason = "Standard approval";
        } else {
            approvalStatus = CONDITIONAL;
            approvalReason = "Normal tier - conditional approval";
        }

        return approvalStatus;
    }

    // ===== Private Validation Methods =====
    private boolean checkAgeEligibility() {
        String ageGroup = customer.getAgeGroup();
        return !ageGroup.equals(CustomerInformation.UNDER_18) && 
               !ageGroup.equals(CustomerInformation.AGE_60_PLUS);
    }

    private boolean checkCustomerStatus() {
        return customer.isActive();
    }

    // ===== Getters =====
    public String getApprovalStatus() {
        return approvalStatus;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    // ===== Display Method =====
    public void displayApprovalResult() {
        System.out.println("Status: " + approvalStatus);
        System.out.println("Reason: " + approvalReason);
    }

    // ===== Manager Verification =====
    private boolean verifyManagerCredentials(String token) {
        return token != null && token.equals("ADMIN123");
    }

    // ===== Modify Customer Record =====
    public boolean modifyCustomerDataRecord(String token, String id, String field, String val) {

        if (!verifyManagerCredentials(token)) {
            return false;
        }

        if (!customer.getId().equals(id)) {
            return false;
        }

        switch (field.toLowerCase()) {
            case "name":
                customer.setName(val);
                break;
            case "balance":
                customer.setBalance(Double.parseDouble(val));
                break;
            case "age":
                customer.setAge(Integer.parseInt(val));
                break;
            default:
                return false;
        }

        writeAuditLogRecord(id, "Modified " + field);
        return true;
    }

    // ===== Audit Log =====
    private void writeAuditLogRecord(String mgrId, String action) {
        System.out.println("[AUDIT] Manager: " + mgrId + " | Action: " + action);
    }
}   
