package com.customerregistration;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Repayment {

    // Status constants 
    public static final String PENDING   = "Pending";
    public static final String COMPLETED = "Completed";
    public static final String OVERDUE   = "Overdue";
    public static final String PARTIAL   = "Partial";

    // Private instance fields 
    private String repaymentId;
    private String loanId;
    private String customerName;
    private String scheduledDate;
    private double scheduledAmount;
    private double amountPaid;
    private String paymentDate;
    private String repaymentStatus;
    private double penaltyRate;
    private double penaltyAmount;
    private double totalAmountDue;

    // Constructor 
    public Repayment(String repaymentId, String loanId, String customerName,
                     String scheduledDate, double scheduledAmount, double penaltyRate) {
        if (isBlank(repaymentId) || isBlank(loanId) || isBlank(customerName) || isBlank(scheduledDate)) {
            throw new IllegalArgumentException("Repayment ID, loan ID, customer name and scheduled date are required.");
        }
        if (scheduledAmount <= 0) {
            throw new IllegalArgumentException("Scheduled amount must be greater than zero.");
        }
        if (penaltyRate < 0) {
            throw new IllegalArgumentException("Penalty rate cannot be negative.");
        }

        this.repaymentId = repaymentId;
        this.loanId = loanId;
        this.customerName = customerName;
        this.scheduledDate = scheduledDate;
        this.scheduledAmount = scheduledAmount;
        this.penaltyRate = penaltyRate;

        // Default setup state parameters
        this.amountPaid = 0.0;
        this.paymentDate = null;
        this.penaltyAmount = 0.0;
        this.totalAmountDue = scheduledAmount;
        this.repaymentStatus = PENDING;
    }

    // Business Logic Methods
    public void processPayment(double paymentAmount, String actualPaymentDate) {
        if (paymentAmount <= 0) {
            throw new IllegalArgumentException("Payment execution input must be greater than zero.");
        }
        if (isBlank(actualPaymentDate)) {
            throw new IllegalArgumentException("Processing execution require target date validation context.");
        }

        this.paymentDate = actualPaymentDate;
        this.amountPaid += paymentAmount;

        calculatePenalties(actualPaymentDate);

        if (this.amountPaid >= this.totalAmountDue) {
            this.repaymentStatus = COMPLETED;
        } else if (this.amountPaid > 0) {
            this.repaymentStatus = PARTIAL;
            this.totalAmountDue -= paymentAmount; 
        }
    }

    public void calculatePenalties(String referenceDateStr) {
        try {
            LocalDate scheduled = LocalDate.parse(this.scheduledDate);
            LocalDate reference = LocalDate.parse(referenceDateStr);

            if (reference.isAfter(scheduled) && !repaymentStatus.equals(COMPLETED)) {
                this.repaymentStatus = OVERDUE;
                this.penaltyAmount = this.scheduledAmount * this.penaltyRate;
                this.totalAmountDue = this.scheduledAmount + this.penaltyAmount;
            }
        } catch (DateTimeParseException ex) {
            System.out.println("Algorithmic warning: Invalid date parsing detected during penalty updates.");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Getters
    public String getRepaymentId() { return repaymentId; }
    public String getLoanId() { return loanId; }
    public String getCustomerName() { return customerName; }
    public String getScheduledDate() { return scheduledDate; }
    public double getScheduledAmount() { return scheduledAmount; }
    public double getAmountPaid() { return amountPaid; }
    public String getPaymentDate() { return paymentDate; }
    public String getRepaymentStatus() { return repaymentStatus; }
    public double getPenaltyRate() { return penaltyRate; }
    public double getPenaltyAmount() { return penaltyAmount; }
    public double getTotalAmountDue() { return totalAmountDue; }

    public void displayRepaymentSummary() {
        System.out.println("========================================");
        System.out.println("         REPAYMENT RECORD               ");
        System.out.println("========================================");
        System.out.printf("Repayment ID     : %s%n", repaymentId);
        System.out.printf("Customer Name    : %s%n", customerName);
        System.out.printf("Loan ID          : %s%n", loanId);
        System.out.printf("Status           : %s%n", repaymentStatus);
        System.out.printf("Amount Paid      : %.2f%n", amountPaid);
        System.out.printf("Payment Date     : %s%n", (paymentDate != null ? paymentDate : "N/A"));
        System.out.println("========================================");
    }

    public void displayRepaymentHistory() {
        System.out.println("========================================");
        System.out.println("         REPAYMENT HISTORY              ");
        System.out.println("========================================");
        System.out.printf("Repayment ID     : %s%n", repaymentId);
        System.out.printf("Customer Name    : %s%n", customerName);
        System.out.printf("Loan ID          : %s%n", loanId);
        System.out.printf("Scheduled Date   : %s%n", scheduledDate);
        System.out.printf("Scheduled Amount : %.2f%n", scheduledAmount);
        System.out.printf("Amount Paid      : %.2f%n", amountPaid);
        System.out.printf("Payment Date     : %s%n", (paymentDate != null ? paymentDate : "N/A"));
        System.out.println("----------------------------------------");
        System.out.printf("Penalty Rate     : %.2f%%%n", penaltyRate * 100);
        System.out.printf("Penalty Amount   : %.2f%n", penaltyAmount);
        System.out.printf("Total Amount Due : %.2f%n", totalAmountDue);
        System.out.printf("Final Status     : %s%n", repaymentStatus);
        System.out.println("========================================");
    }
}