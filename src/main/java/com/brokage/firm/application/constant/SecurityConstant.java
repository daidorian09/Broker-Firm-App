package com.brokage.firm.application.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityConstant {
    public static final String AUTHENTICATION_SCHEME = "Basic ";
    public static final String AUTHORIZATION_HEADER = "authorization";
    public static final String BASIC_AUTH_USERNAME = "admin";
    public static final String BASIC_AUTH_PASSWORD = "1234";
    public static final String JWT_BEARER_AUTHENTICATION_SCHEME = "Bearer ";
    public static final String AUTHORIZATION_ROLE = "ROLE_";
}