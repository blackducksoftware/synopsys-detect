package com.synopsys.integration.configuration.config

interface DetectPropertySource {
    fun hasKey(key: String): Boolean
    fun getKey(key: String): String?
    fun getName(): String
    fun getKeys(): Set<String>
}

class MapPropertySource(private val givenName: String, private val propertyMap: Map<String, String>) : DetectPropertySource {
    override fun hasKey(key: String): Boolean {
        return propertyMap.containsKey(key);
    }
 
    override fun getKey(key: String): String? {
        return propertyMap.getOrElse(key, { -> null });
    }

    override fun getName(): String {
        return givenName;
    }

    override fun getKeys(): Set<String> {
        return propertyMap.keys
    }
}