package com.project.staynest.auth.enums;

public enum UserStatus {
    ACTIVE
    ;

    public static UserStatus from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("User status cannot be null");
        }

        try {
            return UserStatus.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unknown user status: " + value,
                    exception
            );
        }
    }
}
