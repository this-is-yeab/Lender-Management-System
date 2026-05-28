package com.customerregistration;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Repayment {

    //  Status constants 
    public static final String PENDING   = "Pending";
    public static final String COMPLETED = "Completed";
    public static final String OVERDUE   = "Overdue";
    public static final String PARTIAL   = "Partial";

    //  Private instance fields 
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
    /**
     * Creates a new repayment record.
     * Initial state: status = PENDING, amountPaid = 0.0, paymentDate = null,
     * penaltyAmount = 0.0, totalAmountDue = scheduledAmount.
     */
    public Repayment(String repaymentId, String loanId, String customerName,
                     String scheduledDate, double scheduledAmount, double penaltyRate) {
        if (isBlank(repaymentId) || isBlank(loanId) || isBlank(customerName) || isBlank(scheduledDate)) {
            throw new IllegalArgumentException("Repayment ID, loan ID, customer name and scheduled date are required.");
        }
        if (scheduledAmount <= 0) {
            throw new IllegalArgumentException("Scheduled amount must be greater than 0.");
        }
        if (penaltyRate < 0) {
            throw new IllegalArgumentException("Penalty rate cannot be negative.");
        }

        validateIsoDate(scheduledDate, "Scheduled date");

        this.repaymentId     = repaymentId;
        this.loanId          = loanId;
        this.customerName    = customerName;
        this.scheduledDate   = scheduledDate;
        this.scheduledAmount = scheduledAmount;
        this.penaltyRate     = penaltyRate;

        // Initial state
        this.repaymentStatus = PENDING;
        this.amountPaid      = 0.0;
        this.paymentDate     = null;
        this.penaltyAmount   = 0.0;
        this.totalAmountDue  = scheduledAmount;
    }

    //  Public methods 

    /**
     * Records a payment, recalculates penalty and total due, then updates status.
     * - COMPLETED : amountPaid >= totalAmountDue
     * - OVERDUE   : payment is late AND still insufficient
     * - PARTIAL   : something paid but not enough (and not late)
     *
     * Date comparison works correctly for yyyy-MM-dd format (ISO).
     */
    public void makePayment(double amount, String paymentDate) {
        if (amount < 0) {
            throw new IllegalArgumentException("Payment amount cannot be negative.");
        }
        if (isBlank(paymentDate)) {
            throw new IllegalArgumentException("Payment date is required.");
        }
        validateIsoDate(paymentDate, "Payment date");

        this.amountPaid  = amount;
        this.paymentDate = paymentDate;

        // Recalculate derived figures
        calculatePenalty();
        calculateTotalAmountDue();

        // Determine status
        if (this.amountPaid >= this.totalAmountDue) {
            this.repaymentStatus = COMPLETED;
        } else {
            boolean isLate = isPaymentLate(paymentDate, scheduledDate);
            if (isLate) {
                this.repaymentStatus = OVERDUE;
            } else if (this.amountPaid <= 0) {
                // No payment before due date should remain pending.
                this.repaymentStatus = PENDING;
            } else {
                this.repaymentStatus = PARTIAL;
            }
        }
    }

    //  Private helper methods 

    /**
     * penaltyAmount = scheduledAmount * penaltyRate (applied when late or partial).
     */
    private double calculatePenalty() {
        boolean isLate = isPaymentLate(paymentDate, scheduledDate);
        boolean isPartialOrLate = isLate || (amountPaid > 0 && amountPaid < scheduledAmount);

        if (isPartialOrLate) {
            this.penaltyAmount = scheduledAmount * penaltyRate;
        } else {
            this.penaltyAmount = 0.0;
        }
        return this.penaltyAmount;
    }

    /**
     * totalAmountDue = scheduledAmount + penaltyAmount.
     */
    private double calculateTotalAmountDue() {
        this.totalAmountDue = scheduledAmount + penaltyAmount;
        return this.totalAmountDue;
    }

    /**
     * Previews the penalty for user input before payment is posted.
     */
    public double previewPenalty(double amount, String paymentDate) {
        if (amount < 0 || isBlank(paymentDate)) {
            return 0.0;
        }
        try {
            validateIsoDate(paymentDate, "Payment date");
            boolean isLate = isPaymentLate(paymentDate, scheduledDate);
            boolean isPartialOrLate = isLate || (amount > 0 && amount < scheduledAmount);
            return isPartialOrLate ? scheduledAmount * penaltyRate : 0.0;
        } catch (IllegalArgumentException ex) {
            return 0.0;
        }
    }

    /**
     * Previews the total amount due before payment is posted.
     */
    public double previewTotalDue(double amount, String paymentDate) {
        return scheduledAmount + previewPenalty(amount, paymentDate);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void validateIsoDate(String dateValue, String fieldName) {
        try {
            LocalDate.parse(dateValue);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in yyyy-MM-dd format.");
        }
    }

    private static boolean isPaymentLate(String paymentDate, String scheduledDate) {
        if (paymentDate == null || scheduledDate == null) {
            return false;
        }
        LocalDate payment = LocalDate.parse(paymentDate);
        LocalDate scheduled = LocalDate.parse(scheduledDate);
        return payment.isAfter(scheduled);
    }

    //  Getters 


    public String getRepaymentId()     { return repaymentId; }
    public String getLoanId()          { return loanId; }
    public String getCustomerName()    { return customerName; }
    public String getScheduledDate()   { return scheduledDate; }
    public double getScheduledAmount() { return scheduledAmount; }
    public double getAmountPaid()      { return amountPaid; }
    public String getPaymentDate()     { return paymentDate; }
    public String getRepaymentStatus() { return repaymentStatus; }
    public double getPenaltyAmount()   { return penaltyAmount; }
    public double getTotalAmountDue()  { return totalAmountDue; }

    //  Display methods 

    /**
     * Prints a formatted summary of this repayment record.
     */
    public void displayRepaymentSummary() {
        System.out.println("========================================");
        System.out.println("         REPAYMENT SUMMARY              ");
        System.out.println("========================================");
        System.out.printf("Repayment ID     : %s%n", repaymentId);
        System.out.printf("Customer Name    : %s%n", customerName);
        System.out.printf("Loan ID          : %s%n", loanId);
        System.out.printf("Status           : %s%n", repaymentStatus);
        System.out.printf("Scheduled Date   : %s%n", scheduledDate);
        System.out.printf("Scheduled Amount : %.2f%n", scheduledAmount);
        System.out.printf("Amount Paid      : %.2f%n", amountPaid);
        System.out.printf("Payment Date     : %s%n", (paymentDate != null ? paymentDate : "N/A"));
        System.out.println("========================================");
    }

    /**
     * Prints the complete repayment history including penalty details.
     */
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
