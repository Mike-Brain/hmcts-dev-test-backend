package uk.gov.hmcts.reform.dev.enumerations;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TaskStatus {
    NOT_STARTED("Not started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    @JsonValue
    public String getDisplayName() {
        return displayName;
    }
}


