package com.project.staynest.auth.business.mapper.jwt;

import com.project.staynest.auth.business.model.jwt.JwtUserData;
import com.project.staynest.auth.security.model.JwtIdentity;

public class JwtUserDataMapper {
    private JwtUserDataMapper(){}

    public static JwtUserData from(JwtIdentity jwtIdentity){

        return new JwtUserData(
                jwtIdentity.subject(),
                jwtIdentity.sessionId(),
                jwtIdentity.role(),
                jwtIdentity.status(),
                jwtIdentity.audience(),
                jwtIdentity.deviceId(),
                jwtIdentity.userAgent()
        );
    }
}
