package com.synopsys.integration.configuration.property

/**
 * This is the most basic property.
 * It has no type information and a value cannot be retrieved for it (without a subclass).
 **/
abstract class Property(val key: String) {
    var name: String? = null
    var fromVersion: String? = null
    var propertyHelpInfo: PropertyHelpInfo? = null
    var propertyGroupInfo: PropertyGroupInfo? = null
    var category: Category? = null
    var propertyDeprecationInfo: PropertyDeprecationInfo? = null

    fun info(name: String, fromVersion: String): Property {
        this.name = name
        this.fromVersion = fromVersion
        return this
    }

    fun help(short: String, long: String? = null): Property {
        this.propertyHelpInfo = PropertyHelpInfo(short, long)
        return this
    }

    fun groups(primaryGroup: Group, vararg additionalGroups: Group): Property {
        this.propertyGroupInfo = PropertyGroupInfo(primaryGroup, additionalGroups.toList())
        return this
    }

    fun category(category: Category): Property {
        this.category = category
        return this
    }

    fun deprecated(description: String, failInVersion: ProductMajorVersion, removeInVersion: ProductMajorVersion): Property {
        this.propertyDeprecationInfo = PropertyDeprecationInfo(description, failInVersion, removeInVersion)
        return this
    }

    open fun isCaseSensitive(): Boolean = false
    open fun isOnlyExampleValues(): Boolean = false
    open fun listExampleValues(): List<String>? = emptyList()
    open fun describeDefault(): String? = null
}

data class PropertyHelpInfo(val short: String, val long: String?)
data class PropertyGroupInfo(val primaryGroup: Group, val additionalGroups: List<Group>)
data class PropertyDeprecationInfo(val description: String, val failInVersion: ProductMajorVersion, val removeInVersion: ProductMajorVersion)

