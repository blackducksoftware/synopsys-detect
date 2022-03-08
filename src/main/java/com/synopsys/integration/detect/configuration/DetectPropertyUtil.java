package com.synopsys.integration.detect.configuration;

import java.util.function.Predicate;

public class DetectPropertyUtil {
    private DetectPropertyUtil() {
        // Hiding the implicit public constructor
    }

    private static final Predicate<String> PASSWORDS_AND_TOKENS_PREDICATE = propertyKey ->
        propertyKey.toLowerCase().contains("password")
            || propertyKey.toLowerCase().contains("api.token")
            || propertyKey.toLowerCase().contains("access.token");

    public static Predicate<String> getPasswordsAndTokensPredicate() {
        return PASSWORDS_AND_TOKENS_PREDICATE;
    }
}
