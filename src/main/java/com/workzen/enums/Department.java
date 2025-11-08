package com.workzen.enums;

public enum Department {
    HUMAN_RESOURCES("Human Resources"),
    INFORMATION_TECHNOLOGY("Information Technology"),
    FINANCE("Finance"),
    OPERATIONS("Operations"),
    MARKETING("Marketing"),
    SALES("Sales"),
    RESEARCH_DEVELOPMENT("Research & Development"),
    QUALITY_ASSURANCE("Quality Assurance"),
    ADMINISTRATION("Administration"),
    LEGAL("Legal"),
    SECURITY("Security"),
    FACILITIES("Facilities");
    
    private final String displayName;
    
    Department(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
