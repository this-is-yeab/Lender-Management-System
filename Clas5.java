// Jvdroid-main: 
import java.util.ArrayList;

public class CustomerInformation {

    // =============================
    // CONSTANTS
    // =============================
    
    public static final String PREMIUM = "Premium";
    public static final String GOLD = "Gold";
    public static final String NORMAL = "Normal";
    
    public static final String INDIVIDUAL = "Individual";
    public static final String GROUP = "Group";
    public static final String COMPANY = "Company";
    
    public static final String UNDER_18 = "Under 18";
    public static final String AGE_18_35 = "18-35";
    public static final String AGE_35_60 = "35-60";
    public static final String AGE_60_PLUS = "60+";
    
    public static final String ACTIVE = "Active";
    public static final String INACTIVE = "Inactive";
    public static final String SUSPENDED = "Suspended";
    
    // =============================
    // FIELDS
    // =============================
    
    private String customerName;
    private String contactNumber;
    private String email;
    private String collateralDescription;
    private String registrationID;
    private String accountNumber;
    private int age;
    private double accountBalance;
    private double monthlyTransactionVolume;
    private boolean isGroup;
    private boolean isCompany;
    private String accountTier;
    private String institutionalStatus;
    private String ageGroup;
    private String customerStatus;
    private ArrayList<String> transactions;
    
    // =============================
    // CONSTRUCTOR
    // =============================
    
    public CustomerInformation(String customerName, String contactNumber, String email,
                               String collateralDescription, String registrationID,
                               String accountNumber, int age, double accountBalance,
                               double monthlyTransactionVolume, boolean isGroup, boolean isCompany) {
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.collateralDescription = collateralDescription;
        this.registrationID = registrationID;
        this.accountNumber = accountNumber;
        this.age = age;
        this.accountBalance = accountBalance;
        this.monthlyTransactionVolume = monthlyTransactionVolume;
        this.isGroup = isGroup;
        this.isCompany = isCompany;
        
        this.accountTier = classifyAccountTier();
        this.institutionalStatus = classifyInstitutionalStatus();
        this.ageGroup = classifyAgeGroup();
        this.customerStatus = ACTIVE;
        this.transactions = new ArrayList<>();
    }
    
    // =============================
    // CLASSIFICATION
    // =============================
    
    private String classifyAccountTier() {
        if (accountBalance >= 50000 || monthlyTransactionVolume >= 10000) {
            return PREMIUM;
        } else if (accountBalance >= 20000 || monthlyTransactionVolume >= 5000) {
            return GOLD;
        } else {
            return NORMAL;
        }
    }
    
    private String classifyInstitutionalStatus() {
        if (isCompany) {
            return COMPANY;
        } else if (isGroup) {
            return GROUP;
        } else {
            return INDIVIDUAL;
        }
    }
    
    private String classifyAgeGroup() {
        if (age < 18) {
            return UNDER_18;
        } else if (age <= 35) {
            return AGE_18_35;
        } else if (age <= 60) {
            return AGE_35_60;
        } else {
            return AGE_60_PLUS;
        }
    }
    
    // =============================
    // GETTERS
    // =============================
    
    public String getCustomerName() { return customerName; }
    public String getContactNumber() { return contactNumber; }
    public String getEmail() { return email; }
    public String getCollateralDescription() { return collateralDescription; }
    public String getRegistrationID() { return registrationID; }
    public String getAccountNumber() { return accountNumber; }
    public String getAccountTier() { return accountTier; }
    public String getInstitutionalStatus() { return institutionalStatus; }
    public String getAgeGroup() { return ageGroup; }
    public String getCustomerStatus() { return customerStatus; }
    public ArrayList<String> getTransactions() { return transactions; }
    
    public String getAllClassifications() {
        return "Name: " + customerName +
               "\nContact: " + contactNumber +
               "\nEmail: " + email +
               "\nCollateral: " + collateralDescription +
               "\nRegistration ID: " + registrationID +
               "\nAccount: " + accountNumber +
               "\nTier: " + accountTier +
               "\nInstitutional Status: " + institutionalStatus +
               "\nAge Group: " + ageGroup +
               "\nCustomer Status: " + customerStatus;
    }
    
    // =============================
    // SEARCH
    // =============================
    
    public String searchByName(String name) {
        if (this.customerName.equalsIgnoreCase(name)) {
            return getAllClassifications();
        }
        return "Customer not found.";
    }
    
    public String searchByAccount(String accountNumber) {
        if (this.accountNumber.equals(accountNumber)) {
            return getAllClassifications();
        }
        return "Customer not found.";
    }
    
    public String searchByRegistrationID(String regID) {
        if (this.registrationID.equalsIgnoreCase(regID)) {
            return getAllClassifications();
        }
        return "Customer not found.";
    }
    
    // =============================
    // DISPLAY
    // =============================
    
    public void displayCustomerStatus() {
        System.out.println("=== Customer Status ===");
        System.out.println("Name: " + customerName);
        System.out.println("Registration ID: " + registrationID);
        System.out.println("Account: " + accountNumber);
        System.out.println("Contact: " + contactNumber);
        System.out.println("Email: " + email);
        System.out.println("Collateral: " + collateralDescription);
        System.out.println("Status: " + customerStatus);
        System.out.println("Tier: " + accountTier);
        System.out.println("Institutional Status: " + institutionalStatus);
        System.out.println("Age Group: " + ageGroup);
    }
    
    public void displayTransactions() {
        System.out.println("=== Transactions for " + customerName + " ===");
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (int i = 0; i < transactions.size(); i++) {
                System.out.println((i + 1) + ". " + transactions.get(i));
            }
        }
    }
    
    // =============================
    // MANAGE
    // =============================
    
    public void addTransaction(String description) {
        transactions.add(description);
    }
    
    public void updateStatus(String newStatus) {
        if (newStatus.equals(ACTIVE) || newStatus.equals(INACTIVE) || newStatus.equals(SUSPENDED)) {
            this.customerStatus = newStatus;
        }
    }
}