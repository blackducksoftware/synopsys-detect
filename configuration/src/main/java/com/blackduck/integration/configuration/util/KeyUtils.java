package com.blackduck.integration.configuration.util;

public class KeyUtils {
    public static String normalizeKey(String key) {
        return key.toLowerCase().replace("_", ".");
    }
}