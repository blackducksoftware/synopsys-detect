package com.synopsys.integration.configuration.config

// IMPORTANT
// A property source is responsible for responding with keys in the normalized form "example.key"
// It must respond to well formed keys in the normalized form "example.key"
// Use KeyUtils if you have keys from unknown sources to ensure consistent key format.
interface PropertySource {
    fun hasKey(key: String): Boolean
    fun getKeys(): Set<String>
    fun getValue(key: String): String?
    fun getOrigin(key: String): String?
    fun getName(): String
}
