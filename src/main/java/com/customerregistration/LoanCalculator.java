package com.customerregistration;

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
        double monthlyRate = annualRate / 12.0;
        if (monthlyRate == 0) {
            monthlyPayment = principal / termMonths;
        } else {
            monthlyPayment = (principal * monthlyRate) / 
                             (1 - Math.pow(1 + monthlyRate, -termMonths));
        }
        totalPaid = monthlyPayment * termMonths;
        totalInterest = totalPaid - principal;
    }

    public double calculateMonthlyPayment() {
        return monthlyPayment;
    }

    public double getTotalInterestPaid() {
        return totalInterest;
    }

    public double getTotalAmountPaid() {
        return totalPaid;
    }

    public double calculateCompoundInterest() {
        return principal * Math.pow(1 + (annualRate / compoundFreq), compoundFreq * (termMonths / 12.0)) - principal;
    }

    public double getRemainingBalance(int monthsPaid) {
        double monthlyRate = annualRate / 12.0;
        if (monthsPaid >= termMonths) return 0.0;
        return principal * Math.pow(1 + monthlyRate, monthsPaid) - 
               (monthlyPayment * (Math.pow(1 + monthlyRate, monthsPaid) - 1) / monthlyRate);
    }

    public boolean isFullyRepaid() {
        return totalPaid <= 0;
    }

    // Getters 
    public double getPrincipal() { return principal; }
    public double getAnnualRate() { return annualRate; }
    public int getTermMonths() { return termMonths; }
    public int getCompoundFreq() { return compoundFreq; }

    // Main Method (Testing) 
    public static void main(String[] args) {
        LoanCalculator loan = new LoanCalculator(
                0.12,   // annual interest rate = 12%
                60,     // 60 months = 5 years
                COMPOUND_MONTHLY,
                500000  // appraisal value
        );

        System.out.println("===== LOAN DETAILS =====");
        System.out.println("Principal: " + loan.getPrincipal());
        System.out.println("Monthly Payment: " + loan.calculateMonthlyPayment());
        System.out.println("Total Interest Paid: " + loan.getTotalInterestPaid());
        System.out.println("Total Amount Paid: " + loan.getTotalAmountPaid());
        System.out.println("Compound Interest: " + loan.calculateCompoundInterest());
        System.out.println("Remaining Balance after 12 months: " + loan.getRemainingBalance(12));
        System.out.println("Fully Repaid? " + loan.isFullyRepaid());
    }
}