package com.workzen.enums;

public enum Role {
    // Administrative Roles
    SUPER_ADMIN("Super Administrator"),
    ADMIN("Administrator"),
    
    // HR Department Roles
    HR_MANAGER("HR Manager"),
    HR_EXECUTIVE("HR Executive"),
    RECRUITER("Recruiter"),
    
    // Management Roles
    CEO("Chief Executive Officer"),
    CTO("Chief Technology Officer"),
    CFO("Chief Financial Officer"),
    DEPARTMENT_HEAD("Department Head"),
    TEAM_LEAD("Team Lead"),
    PROJECT_MANAGER("Project Manager"),
    
    // Employee Roles
    SENIOR_DEVELOPER("Senior Developer"),
    JUNIOR_DEVELOPER("Junior Developer"),
    BUSINESS_ANALYST("Business Analyst"),
    QA_ENGINEER("QA Engineer"),
    DEVOPS_ENGINEER("DevOps Engineer"),
    UI_UX_DESIGNER("UI/UX Designer"),
    
    // Finance & Accounting
    FINANCE_MANAGER("Finance Manager"),
    ACCOUNTANT("Accountant"),
    PAYROLL_SPECIALIST("Payroll Specialist"),
    
    // Support & Operations
    ADMIN_ASSISTANT("Administrative Assistant"),
    OFFICE_MANAGER("Office Manager"),
    IT_SUPPORT("IT Support"),
    
    // Intern & Trainee
    INTERN("Intern"),
    TRAINEE("Trainee"),
    
    // Contract & Consultant
    CONTRACTOR("Contractor"),
    CONSULTANT("Consultant");
    
    private final String displayName;
    
    Role(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    // Helper methods for role categorization
    public boolean isAdmin() {
        return this == SUPER_ADMIN || this == ADMIN;
    }
    
    public boolean isHR() {
        return this == HR_MANAGER || this == HR_EXECUTIVE || this == RECRUITER;
    }
    
    public boolean isManagement() {
        return this == CEO || this == CTO || this == CFO || 
               this == DEPARTMENT_HEAD || this == TEAM_LEAD || this == PROJECT_MANAGER;
    }
    
    public boolean isFinance() {
        return this == FINANCE_MANAGER || this == ACCOUNTANT || this == PAYROLL_SPECIALIST;
    }
    
    public boolean isDeveloper() {
        return this == SENIOR_DEVELOPER || this == JUNIOR_DEVELOPER;
    }
    
    public boolean isTemporary() {
        return this == INTERN || this == TRAINEE || this == CONTRACTOR || this == CONSULTANT;
    }
}
