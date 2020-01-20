package com.synopsys.integration.configuration.config

class KeyUtils {
    companion object {
        fun normalizeKey(key: String): String {
            return key.toLowerCase().replace("_", ".");
        }
    }
}