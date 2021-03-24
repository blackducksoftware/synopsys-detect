/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.util;

public class KeyUtils {
    public static String normalizeKey(String key) {
        return key.toLowerCase().replace("_", ".");
    }
}