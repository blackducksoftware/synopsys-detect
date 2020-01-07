package com.synopsys.integration.detect.config


class OptionalStringProperty(key: String) : OptionalProperty<String>(key, StringValueParser()) {}
class RequiredStringProperty(key: String, default: String) : RequiredProperty<String>(key, StringValueParser(), default) {
    override fun describeDefault(): String? = default
}

class OptionalBooleanProperty(key: String) : OptionalProperty<Boolean>(key, BooleanValueParser()) {
    override fun listExampleValues(): List<String>? = listOf("true", "false")
}
class RequiredBooleanProperty(key: String, default: Boolean) : RequiredProperty<Boolean>(key, BooleanValueParser(), default) {
    override fun listExampleValues(): List<String>? = listOf("true", "false")
    override fun describeDefault(): String? = default.toString()
}

class OptionalIntegerProperty(key: String) : OptionalProperty<Int>(key, IntegerValueParser()) {}
class RequiredIntegerProperty(key: String, default: Int) : RequiredProperty<Int>(key, IntegerValueParser(), default) {
    override fun describeDefault(): String? = default.toString()
}

class OptionalLongProperty(key: String) : OptionalProperty<Long>(key, LongValueParser()) {}
class RequiredLongProperty(key: String, default: Long) : RequiredProperty<Long>(key, LongValueParser(), default) {
    override fun describeDefault(): String? = default.toString()
}

class OptionalStringArrayProperty(key: String) : OptionalProperty<List<String>>(key, StringListValueParser()) {}
class RequiredStringArrayProperty(key: String, default: List<String>) : RequiredProperty<List<String>>(key, StringListValueParser(), default) {
    override fun describeDefault(): String? = default.joinToString { "," }
}

class OptionalEnumListProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : OptionalProperty<List<T>>(key, EnumListValueOfParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}

class RequiredEnumListProperty<T>(key: String, default: List<T>, valueOf: (String) -> T?, val values: List<T>) : RequiredProperty<List<T>>(key, EnumListValueOfParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.joinToString { "," }
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
}

class StringValueParser : ValueParser<String> () {
    override fun parse(value: String?) : String? = when {
        value != null && value.isNotBlank() -> value
        else -> null
    }
}

class BooleanValueParser : ValueParser<Boolean> () {
    override fun parse(value: String?) : Boolean? = when {
        value != null && value.isNotBlank() -> toBoolean(value)
        else -> null
    }

    fun toBoolean(value: String): Boolean {
        return value.toLowerCase().startsWith("t");
    }
}

class IntegerValueParser : ValueParser<Int> () {
    override fun parse(value: String?) : Int? = when {
        value != null && value.isNotBlank() -> toInteger(value)
        else -> null
    }

    fun toInteger(value: String): Int {
        return Integer.parseInt(value)
    }
}

class LongValueParser : ValueParser<Long> () {
    override fun parse(value: String?) : Long? = when {
        value != null && value.isNotBlank() -> toLong(value)
        else -> null
    }

    fun toLong(value: String): Long {
        return value.toLong()
    }
}

class StringListValueParser : ValueParser<List<String>> () {
    override fun parse(value: String?) : List<String>? = when {
        value != null && value.isNotBlank() -> toStringList(value)
        else -> null
    }

    fun toStringList(value: String): List<String> {
        return value.split(",").toList()
    }
}

class EnumValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<T> () {
    override fun parse(value: String?) : T? = when {
        value != null && value.isNotBlank() -> valueOf(value)
        else -> null
    }
}

class EnumListValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<List<T>> () {
    override fun parse(value: String?) : List<T>? = when {
        value != null && value.isNotBlank() -> value.split(",").map {it -> valueOf(it)!! }.toList()
        else -> null
    }
}

