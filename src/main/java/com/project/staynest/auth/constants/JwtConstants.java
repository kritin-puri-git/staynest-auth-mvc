package com.project.staynest.auth.constants;

public class JwtConstants {

    public static final String JWT_ACCESS_TOKEN_TYPE = "ACCESS";
    public static final String JWT_REFRESH_TOKEN_TYPE = "REFRESH";

    public static final int JWT_ACCESS_TOKEN_EXPIRY_IN_SECONDS = 900;
    public static final int JWT_REFRESH_TOKEN_EXPIRY_IN_SECONDS = 259200;

    private JwtConstants(){}
}
