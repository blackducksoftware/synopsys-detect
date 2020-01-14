package com.synopsys.integration.configuration.config

class MapPropertySource(private val givenName: String, private val propertyMap: Map<String, String>) : PropertySource {
    override fun hasKey(key: String): Boolean {
        return propertyMap.containsKey(key);
    }

    override fun getValue(key: String): String? {
        return propertyMap.getOrElse(key, { -> null });
    }

    override fun getName(): String {
        return givenName;
    }

    override fun getKeys(): Set<String> {
        return propertyMap.keys
    }
}