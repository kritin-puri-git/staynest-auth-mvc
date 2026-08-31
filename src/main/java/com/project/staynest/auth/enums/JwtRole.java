package com.project.staynest.auth.enums;

import com.project.staynest.auth.validation.Validation;

import java.util.ArrayList;
import java.util.List;

public enum JwtRole {
    USER(List.of(JwtAudience.STAYNEST_PROFILE)),
    ;

    private final List<String> audience;

    JwtRole(List<JwtAudience> jwtAudienceList) {
        Validation.validate(jwtAudienceList, "jwtAudienceList", this.getClass().getSimpleName());

        List<String> audience = new ArrayList<>(jwtAudienceList.size());

        for(JwtAudience jwtAudience : jwtAudienceList){
            audience.add(jwtAudience.name());
        }

        this.audience = List.copyOf(audience);
    }

    public static JwtRole from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Jwt role cannot be null");
        }

        try {
            return JwtRole.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Unknown Jwt Role: " + value,
                    exception
            );
        }
    }

    public List<String> getAudience(){
        return this.audience;
    }
}
