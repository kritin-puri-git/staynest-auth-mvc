package com.project.staynest.auth.errorhandling.exceptions.internal;


import java.util.HashMap;
import java.util.Map;

public class InvalidGenderEnumException extends RuntimeException {

    private final String gender;
    public InvalidGenderEnumException(String gender) {
        super("Invalid gender: "+ gender);
        this.gender = gender;
    }

    public Map<String, String> getErrorDetails(){
        return new HashMap<>(Map.of("gender", "Invalid gender: " + this.gender));
    }


}
