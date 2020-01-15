package com.synopsys.integration.configuration.config

interface PropertySource {
    fun hasKey(key: String): Boolean
    fun getKeys(): Set<String>
    fun getValue(key: String): String?
    fun getOrigin(key: String): String?
    fun getName(): String
}
