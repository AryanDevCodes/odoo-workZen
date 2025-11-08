package com.workzen.enums;

public enum LeaveType {
    CASUAL_LEAVE("Casual Leave", 12),
    SICK_LEAVE("Sick Leave", 12),
    EARNED_LEAVE("Earned Leave", 21),
    MATERNITY_LEAVE("Maternity Leave", 90),
    PATERNITY_LEAVE("Paternity Leave", 15),
    EMERGENCY_LEAVE("Emergency Leave", 5),
    BEREAVEMENT_LEAVE("Bereavement Leave", 7),
    STUDY_LEAVE("Study Leave", 30),
    SABBATICAL_LEAVE("Sabbatical Leave", 365),
    UNPAID_LEAVE("Unpaid Leave", 0);
    
    private final String displayName;
    private final int defaultDaysAllowed;
    
    LeaveType(String displayName, int defaultDaysAllowed) {
        this.displayName = displayName;
        this.defaultDaysAllowed = defaultDaysAllowed;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getDefaultDaysAllowed() {
        return defaultDaysAllowed;
    }
}
