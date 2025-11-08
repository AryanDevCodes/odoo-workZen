package com.workzen.enums;

public enum EmployeeStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    ON_LEAVE("On Leave"),
    SUSPENDED("Suspended"),
    TERMINATED("Terminated"),
    RESIGNED("Resigned"),
    PROBATION("On Probation"),
    NOTICE_PERIOD("Notice Period"),
    RETIRED("Retired");
    
    private final String displayName;
    
    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == ACTIVE || this == ON_LEAVE || this == PROBATION;
    }
    
    public boolean canLogin() {
        return this == ACTIVE || this == ON_LEAVE || this == PROBATION || this == NOTICE_PERIOD;
    }
}
