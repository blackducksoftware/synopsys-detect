package com.synopsys.integration.configuration.config

class MapPropertySource(private val givenName: String, private val underlyingMap: Map<String, String>) : PropertySource {
    private val normalizedPropertyMap: Map<String, String> = underlyingMap.map { KeyUtils.normalizeKey(it.key) to it.value }.toMap()

    override fun getOrigin(key: String): String? {
        return givenName;
    }

    override fun hasKey(key: String): Boolean {
        return normalizedPropertyMap.containsKey(key);
    }

    override fun getValue(key: String): String? {
        return normalizedPropertyMap.getOrElse(key, { -> null });
    }

    override fun getName(): String {
        return givenName;
    }

    override fun getKeys(): Set<String> {
        return normalizedPropertyMap.keys
    }
}