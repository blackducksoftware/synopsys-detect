package com.synopsys.integration.configuration.config

interface PropertySource {
    fun hasKey(key: String): Boolean
    fun getValue(key: String): String?
    fun getName(): String
    fun getKeys(): Set<String>
}
