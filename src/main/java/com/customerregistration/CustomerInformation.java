package com.customerregistration;

public class CustomerInformation {
    private String customerName = "";

    public void setCustomerName(String customerName) {
        if (customerName != null) {
            this.customerName = customerName;
        }
    }

    public String getCustomerName() {
        return this.customerName;
    }
}