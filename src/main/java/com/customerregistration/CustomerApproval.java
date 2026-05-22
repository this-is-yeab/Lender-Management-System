package com.customerregistration;
public class CustomerApproval {

// ===== Public Static Final Constants (System Flags) =====
public static final String APPROVED = "approved";
public static final String REJECTED = "rejected";
public static final String CONDITIONAL = "conditional approval";

// ===== Private Static Final Constants (Managerial Tiers) =====
private static final String PREMIUM = "Premium";
private static final String GOLD = "Gold";
private static final String PLATINUM = "Platinum";
private static final String NORMAL = "Normal";

// ===== Private Fields =====
private Customer customer;
private String approvalStatus;
private String approvalReason;

// ===== Constructor =====
public CustomerApproval(Customer customer) {
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

    if (checkAccountTier()) {
        approvalStatus = APPROVED;
        approvalReason = "Meets all requirements";
    } else {
        approvalStatus = CONDITIONAL;
        approvalReason = "Partial eligibility";
    }

    return approvalStatus;
}

// ===== Private Validation Methods =====
private boolean checkAgeEligibility() {
    int age = customer.getAge();
    return age >= 18 && age <= 60;
}

private boolean checkCustomerStatus() {
    return customer.isActive();
}

private boolean checkAccountTier() {
    return customer.getBalance() > 10000; // example condition
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

// ===== Social Status Assignment =====
public String evaluateAndAssignSocialStatus(String token, String id) {

    if (!verifyManagerCredentials(token)) {
        return "Access Denied";
    }

    if (!customer.getId().equals(id)) {
        return "Invalid Customer";
    }

    double assets = customer.getBalance();
    String status;

    if (assets > 100000) {
        status = PLATINUM;
    } else if (assets > 50000) {
        status = PREMIUM;
    } else if (assets > 20000) {
        status = GOLD;
    } else {
        status = NORMAL;
    }

    writeAuditLogRecord(id, "Assigned Tier: " + status);
    return status;
}

// ===== Audit Log =====
private void writeAuditLogRecord(String mgrId, String action) {
    System.out.println("[AUDIT] Manager: " + mgrId + " | Action: " + action);
}

}