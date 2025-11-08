package com.workzen.enums;

public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    HALF_DAY("Half Day"),
    LATE("Late"),
    ON_LEAVE("On Leave"),
    WORK_FROM_HOME("Work From Home"),
    ON_DUTY("On Duty");
    
    private final String displayName;
    
    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
