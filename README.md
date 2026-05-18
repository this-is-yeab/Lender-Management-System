# Lender Management System - Customer Registration Module

This repository contains the core Customer Registration and Data Persistence engine for our group's Lender Management System. It manages live data entry processing in RAM via dynamic array scaling and acts as the central data feed provider for the Manager and Clerk dashboards.

## 🛠️ Project Architecture

The registration module is split into two primary backend structural files to maintain strict separation of concerns:

1. **`CustomerInformation.java`** A clean data encapsulation transfer object blueprint. It holds individual, validated customer data fields safely so external modules (like Search) can display layouts without risking data corruption in the primary engine.

2. **`CustomerRegistration.java`** The primary functional engine. It handles initial registration intake, orchestrates an autonomous dynamic array buffer system within RAM, and executes text serialization to permanently log transactions on the host machine.

---

## 💾 Database Layer: Flat-File Storage

The backend completely bypasses heavy SQL setups by using a flat-file database tracking mechanism. All committed registrations are written permanently to:
📁 `customer_database.txt`

Records are structured in a standardized text block layout, complete with verification spacers:
```text
=========================================
Registration ID: REG-XXXXXXXX
Name:            [Customer Name]
Contact:         [Phone Number]
Email:           [Email Address]
Collateral:      [Asset Description]
Status:          Complete
=========================================
