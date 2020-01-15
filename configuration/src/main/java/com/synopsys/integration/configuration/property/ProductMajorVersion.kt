package com.synopsys.integration.configuration.property

interface ProductMajorVersion {
    fun getIntValue(): Int

    fun getDisplayValue(): String? {
        return "${getIntValue()}.0.0"
    }
}