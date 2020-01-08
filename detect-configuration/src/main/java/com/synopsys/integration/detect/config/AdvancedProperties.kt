package com.synopsys.integration.detect.config

import com.synopsys.integration.blackduck.api.enumeration.PolicySeverityType

// FILTERABLE, EXTENDED and SOFT enums.

// An enum that can be the given ENUM or can be STRING
// Useful for properties that might want to be extended by the user such as Black Duck settings where we may know some of the values but don't care if we do not.
sealed class SoftEnumValue<T>
class ActualValue<T>(val value: T) : SoftEnumValue<T>()
class StringValue<T>(val value: String) : SoftEnumValue<T>()

// An enum that can be ALL, NONE or ENUM
// Useful for properties that want to be used with the ExcludeIncludeEnumFilter.
sealed class FilterableEnumValue<T>
class All<T>() : FilterableEnumValue<T>()
class None<T>() : FilterableEnumValue<T>()
class Value<T>(val value: T) : FilterableEnumValue<T>()

fun <T> List<FilterableEnumValue<T>>.containsNone(): Boolean {
    return this.any {
        when (it) {
            is None -> true
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.containsAll(): Boolean {
    return this.any {
        when (it) {
            is None -> true
            else -> false
        }
    }

}

fun <T> List<FilterableEnumValue<T>>.containsValue(value: T): Boolean {
    return this.any {
        when (it) {
            is ActualValue<*> -> it.value == value
            else -> false
        }
    }
}

fun <T> List<FilterableEnumValue<T>>.toValueList(clazz: Class<T>): List<T> {
    return this.flatMap {
        when (it) {
            is ActualValue<*> -> listOf(clazz.cast(it.value))
            else -> emptyList()
        }
    }.toList()
}

fun <T> List<FilterableEnumValue<T>>.populatedValues(allValues: Array<T>, clazz: Class<T>): List<T> {
    if (this.containsNone()) {
        return emptyList()
    } else if (this.containsAll()) {
        return allValues.toList()
    } else {
        return this.toValueList(clazz)
    }
}

// An enum that can be either the E or the B type.
// Useful for enums that extend a base type. For example we want an UNSPECIFIED value on an existing enum that does not have it and does not make sense as an enum value on the existing type.
sealed class ExtendedEnumValue<E, B>
class ExtendedValue<E, B>(val value: E): ExtendedEnumValue<E, B>();
class BaseValue<E, B>(val value: B): ExtendedEnumValue<E, B>();

class FilterableEnumListValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<List<FilterableEnumValue<T>>> () {
    private val parser = ValueOfParser(valueOf)
    override fun parse(value: String) : List<FilterableEnumValue<T>> {
        return value.split(",").map {
            when {
                it.toLowerCase() == "none" -> None<T>()
                it.toLowerCase() == "all" -> All<T>()
                else -> Value(parser.parse(value))
            }
        }
    }
}

class SoftEnumValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<SoftEnumValue<T>> () {
    var parser = ValueOfOrNullParser(valueOf)
    override fun parse(value: String) : SoftEnumValue<T> {
        return when (val enumValue = parser.parse(value)){ //TODO: Catch exception here? What happens when valueOf throws?
            null -> StringValue<T>(value)
            else -> ActualValue<T>(enumValue)
        }
    }
}

class SoftEnumListValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<List<SoftEnumValue<T>>> () {
    var parser = ValueOfOrNullParser(valueOf)
    override fun parse(value: String) : List<SoftEnumValue<T>> {
        return value.split(",").map {
            when (val enumValue = parser.parse(value)){ //TODO: Catch exception here? What happens when valueOf throws?
                null -> StringValue<T>(value)
                else -> ActualValue<T>(enumValue)
            }
        }
    }
}

class ExtendedEnumValueOfParser<E, B>(private val valueOfE: (String) -> E?, private val valueOfB: (String) -> B?) : ValueParser<ExtendedEnumValue<E, B>> () {
    var extendedParser = ValueOfOrNullParser(valueOfE)
    var baseParser = ValueOfOrNullParser(valueOfB)

    override fun parse(value: String) : ExtendedEnumValue<E, B> {
        val eValue = extendedParser.parse(value);
        if (eValue != null) {
            return ExtendedValue(eValue)
        }
        val bValue = baseParser.parse(value);
        if (bValue != null) {
            return BaseValue(bValue)
        }
        throw ValueParseException(value, "either enum", additionalMessage = "Value was not a member of either enum set.")//TODO: Mention enum types?
    }
}

class OptionalFilterableEnumListProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : OptionalProperty<List<FilterableEnumValue<T>>>(key, FilterableEnumListValueOfParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? {
        val base = values.map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }
    override fun isOnlyExampleValues(): Boolean = true
}
class RequiredFilterableEnumListProperty<T>(key: String, default: List<FilterableEnumValue<T>>, valueOf: (String) -> T?, val values: List<T>) : RequiredProperty<List<FilterableEnumValue<T>>>(key, FilterableEnumListValueOfParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.joinToString { "," }
    override fun listExampleValues(): List<String>? {
        val base = values.map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }
    override fun isOnlyExampleValues(): Boolean = true
}

class OptionalEnumProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : OptionalProperty<T>(key, EnumValueOfParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}
class RequiredEnumProperty<T>(key: String, default: T, valueOf: (String) -> T?, val values: List<T>) : RequiredProperty<T>(key, EnumValueOfParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.toString()
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}

class RequiredSoftEnumListProperty<T>(key: String, default: List<SoftEnumValue<T>>, valueOf: (String) -> T?, val values: List<T>) : RequiredProperty<List<SoftEnumValue<T>>>(key, SoftEnumListValueOfParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeDefault(): String? = default.joinToString { "," }
}
class RequiredExtendedEnumProperty<E, B>(key: String, default: ExtendedEnumValue<E, B>, valueOfE: (String) -> E?, valueOfB: (String) -> B?, val valuesExtended: List<E>, val valuesBase: List<B>) : RequiredProperty<ExtendedEnumValue<E, B>>(key, ExtendedEnumValueOfParser(valueOfE, valueOfB), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = valuesExtended.map { it.toString() } + "," + valuesBase.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeDefault(): String? = default.toString() //TODO: No way this is going to work!
}