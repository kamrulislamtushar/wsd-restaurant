package com.wsd.restaurant.util;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";
    public static String SPRING_PROFILE_NO_LIQUIBASE = "no-liquibase";
    public static final String NAME_CO_RELATION_ID = "X-Correlation-ID";
    private Constants() {}
}
