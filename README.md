Local Lender Management System (Mini Bank)

Project Architecture and Group Modules

The application is split into three core dashboard layers, allowing different team members to focus on distinct operational modules while sharing a centralized data persistence layer:

Customer Onboarding and Registration Engine (Foundational Backend)

Responsibility: Manages the initial landing pad for new clients entering the lending pipeline.

Core Files:

CustomerRegistration.java: Handles data scrubbing, dynamic RAM array allocation for active files, and flat-file serialization.

CustomerInformation.java: A clean data transfer object (DTO) designed to isolate data properties safely for external search layout modules.

Database Target: Automatically logs and formats user profiles directly into customer_database.txt.

Clerk Dashboard Module (Processing and Verification Interface)

Responsibility: Allows data clerks to access the master ledger, verify pending registration applications, evaluate submitted collateral asset descriptions, and manage client files.

Integration Hook: Pulls the complete profile lists directly from the storage layer to populate operational workstation screens.

Managerial Oversight Panel (Reporting and Analytics Interface)

Responsibility: Provides upper management with high-level lookup utilities, loan status tracking, filter queries, and total registered client summaries.

Integration Hook: Uses partial-text name filtering and unique ID lookup pipelines to inspect individual profiles instantly.

Database Layer Specification

To ensure easy collaboration across our development environments, data is tracked via a structured, predictable flat-file format. Each transaction block is automatically generated, stamped with a unique runtime token, and saved like this:

=========================================
Registration ID: REG-XXXXXXXX
Name:            [Customer Name]
Contact:         [Phone Number]
Email:           [Email Address]
Collateral:      [Asset Description]
Status:          Complete
Integrated Coding Blueprints (How Our Modules Talk to Each Other)

To keep our individual codebases working together perfectly without breaking the file structure, use these standardized backend method hooks to feed data directly into your interface layouts:

For the Clerk Interface: Displaying the Live Master Ledger
To pull the entire customer database into a table or index list view, call getAllRegisteredCustomers():

CustomerRegistration databaseConnection = new CustomerRegistration();
List masterLedger = databaseConnection.getAllRegisteredCustomers();

System.out.println("Total Registered Clients in System: " + masterLedger.size());
for (CustomerRegistration profile : masterLedger) {
System.out.println("Processing Client: " + profile.getCustomerNames()[0] + " | Status: " + profile.getStatus());
}

For the Manager Interface: Real-Time Keyword Search Bar
To implement a search feature that filters user records instantly as a manager types, call searchCustomersByName(String partialName):

CustomerRegistration databaseConnection = new CustomerRegistration();
String searchInput = "John"; // Represents text pulled from a search UI bar
List filteredMatches = databaseConnection.searchCustomersByName(searchInput);

for (CustomerRegistration match : filteredMatches) {
System.out.println("Match Found: " + match.getCustomerNames()[0] + " [" + match.getRegistrationID() + "]");
}

For Logins, Security Checks, or Target Overviews
To look up a single, complete account file using an isolated reference string, call searchCustomerByID(String targetID):

CustomerRegistration databaseConnection = new CustomerRegistration();
CustomerRegistration account = databaseConnection.searchCustomerByID("REG-F3A8B2C1");

if (account != null) {
System.out.println("Profile Loaded: " + account.getCustomerNames()[0]);
System.out.println("Collateral Value Asset: " + account.getCollateralDescription());
} else {
System.out.println("Error: Registration Reference ID not found.");
}
