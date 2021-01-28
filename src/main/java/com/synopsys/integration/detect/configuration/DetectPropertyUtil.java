package com.synopsys.integration.detect.configuration;

import java.util.function.Predicate;

public class DetectPropertyUtil {
    public static Predicate<String> PASSWORDS_AND_TOKENS_PREDICATE = propertyKey -> propertyKey.toLowerCase().contains("password") || propertyKey.toLowerCase().contains("api.token") || propertyKey.toLowerCase().contains("access.token");
}
