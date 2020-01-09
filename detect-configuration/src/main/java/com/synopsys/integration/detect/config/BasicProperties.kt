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
// Using @JvmSuppressWildcards to prevent the Kotlin compiler from generating wildcard types: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics
class RequiredStringArrayProperty(key: String, default: List<String>) : RequiredProperty<@JvmSuppressWildcards List<String>>(key, StringListValueParser(), default) {
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
    override fun parse(value: String) : String = value
}

class BooleanValueParser : ValueParser<Boolean> () {
    override fun parse(value: String) : Boolean {
        return when (value.toLowerCase().trim()) {
            "" -> true //support spring notion of just adding a property to true (--bool)
            "t" -> true
            "true" -> true
            "f" -> false
            "false" -> false
            else -> throw ValueParseException(value, "boolean", "Supported formats are double quotes, 't', 'f', 'true' and 'false' and is not case sensitive.")
        }
    }
}

class IntegerValueParser : ValueParser<Int> () {
    override fun parse(value: String) : Int {
        return try {
            value.toInt()
        } catch (e: NumberFormatException) {
            throw ValueParseException(value, "integer", innerException = e)
        }
    }
}

class LongValueParser : ValueParser<Long> () {
    override fun parse(value: String) : Long {
        return try {
            value.toLong()
        } catch (e: NumberFormatException) {
            throw ValueParseException(value, "integer", innerException = e)
        }
    }
}

class StringListValueParser : ValueParser<List<String>> () {
    override fun parse(value: String) : List<String> {
        return value.split(",").toList()
    }
}

class EnumValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<T> () {
    private val parser = ValueOfParser(valueOf);
    override fun parse(value: String) : T {
        return parser.parse(value)
    }
}

class EnumListValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<List<T>> () {
    private val parser = ValueOfParser(valueOf);
    override fun parse(value: String) : List<T> {
        return value.split(",").map { parser.parse(it) }
    }
}

class ValueOfParser<T>(val valueOf: (String) -> T?) {
    @Throws(ValueParseException::class)
    fun parse(value: String) : T {
        try {
            return valueOf(value) ?: throw ValueParseException(value, "enum", additionalMessage = "Enum value was null.")
        } catch (e:Exception){
            throw ValueParseException(value, "enum", innerException = e)
        }
    }
}

class ValueOfOrNullParser<T>(val valueOf: (String) -> T?) {
    fun parse(value: String) : T? {
        return try {
            valueOf(value)
        } catch (e:Exception){
            null
        }
    }
}
