package com.synopsys.integration.configuration.property

/**
 * This is the most basic property.
 * It has no type information and a value cannot be retrieved for it (without a subclass).
 **/
abstract class Property(val key: String) {
    var name: String? = null
    var from: String? = null
    var helpShort: String? = null
    var helpLong: String? = null
    var primaryGroup: Group? = null
    var additionalGroups: List<Group>? = null
    var category: Category? = null

    fun info(name: String, from: String): Property {
        this.name = name
        this.from = from
        return this
    }

    fun help(short: String, long: String? = null): Property {
        this.helpShort = short
        this.helpLong = long
        return this
    }

    fun groups(primaryGroup: Group, vararg additionalGroups: Group): Property {
        this.primaryGroup = primaryGroup
        this.additionalGroups = additionalGroups.toList()
        return this
    }

    fun category(category: Category): Property {
        this.category = category
        return this
    }

    open fun isCaseSensitive(): Boolean = false
    open fun isOnlyExampleValues(): Boolean = false
    open fun listExampleValues(): List<String>? = emptyList()
    open fun describeDefault(): String? = null
}
