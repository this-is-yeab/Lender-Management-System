package com.customerregistration;

public class CustomerApproval {

    // System-defined approval status constants
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String CONDITIONAL = "conditional approval";

    // Customer domain model instance
    private CustomerInformation customer;

    // Internal tracking variables for evaluation results
    private String approvalStatus;
    private String approvalReason;

    // Constructor: initializes evaluation pipeline
    public CustomerApproval(CustomerInformation customer) {
        this.customer = customer;
    }

    // Core processing logic for approval evaluation
    public String evaluateApproval() {
        if (!checkCustomerStatus()) {
            approvalStatus = REJECTED;
            approvalReason = "Inactive customer account";
            return approvalStatus;
        }

        if (!checkAgeEligibility()) {
            approvalStatus = REJECTED;
            approvalReason = "Age not eligible";
            return approvalStatus;
        }

        if (checkAccountTier()) {
            approvalStatus = APPROVED;
            approvalReason = "High tier customer";
        } else {
            approvalStatus = CONDITIONAL;
            approvalReason = "Requires further review";
        }

        return approvalStatus;
    }

    // Validation boundary: checks age eligibility matching CustomerInformation constants
    private boolean checkAgeEligibility() {
        if (customer == null) return false;
        String ageGroup = customer.getAgeGroup();
        return !CustomerInformation.UNDER_18.equals(ageGroup);
    }

    // Business rule implementation: evaluates eligibility via account tier constants
    private boolean checkAccountTier() {
        if (customer == null) return false;
        String tier = customer.getAccountTier();
        return CustomerInformation.PLATINUM.equals(tier) || CustomerInformation.PREMIUM.equals(tier);
    }

    // Lifecycle validation state inspector
    private boolean checkCustomerStatus() {
        if (customer == null) return false;
        return customer.isActive(); 
    }

    // Encapsulated getter for approval status
    public String getApprovalStatus() {
        return approvalStatus;
    }

    // Encapsulated getter for approval reason
    public String getApprovalReason() {
        return approvalReason;
    }

    // Presentation method for displaying results
    public void displayApprovalResult() {
        System.out.println("Approval Status: " + approvalStatus);
        System.out.println("Reason: " + approvalReason);
    }

    // Administrative credential verification
    private boolean verifyManagerCredentials(String token) {
        return token != null && token.equals("ADMIN123");
    }

    // Secure data modification controller
    public boolean modifyCustomerDataRecord(String token, String id, String field, String val) {
        if (!verifyManagerCredentials(token)) {
            System.out.println("Access Denied");
            return false;
        }

        System.out.println("Record updated: " + field + " -> " + val);
        writeAuditLogRecord(id, "Modified " + field);
        return true;
    }

    // Status assignment engine based on account classification
    public String evaluateAndAssignSocialStatus(String token, String id) {
        if (!verifyManagerCredentials(token)) {
            return "Unauthorized";
        }

        String tier = customer.getAccountTier();
        writeAuditLogRecord(id, "Checked social status");
        return tier;
    }

    // Compliance logging mechanism
    private void writeAuditLogRecord(String mgrId, String action) {
        System.out.println("LOG: Manager " + mgrId + " performed -> " + action);
    }
}