package com.synopsys.integration.detect.config

import com.synopsys.integration.detect.DetectTool
import java.lang.RuntimeException



class DetectConfig (val orderedPropertySources: List<DetectPropertySource>) {
    val resolved : MutableMap<String, PropertyValue> = mutableMapOf();

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: OptionalProperty<T>): T? {
        return when (val value = resolve(property)) {
            is ProvidedValue -> value.value as T
            is NoValue -> null
        }
   }

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: RequiredProperty<T>): T {
        return when (val value = resolve(property)) {
            is ProvidedValue -> value.value as T
            is NoValue -> property.default
        }
    }

    fun <T> resolve(property: TypedProperty<T>) : PropertyValue {
        if (!resolved.containsKey(property.key)) {
            val propertySourceValue = resolveFromPropertySource(property)
            resolved[property.key] = propertySourceValue ?: NoValue
        }

        return resolved[property.key] ?: throw RuntimeException("Could not resolve a value, something has gone wrong with properties!")
    }

    private fun <T> resolveFromPropertySource(property: TypedProperty<T>) : ProvidedValue? {
        for (source in orderedPropertySources){
            if (source.hasKey(property.key)) {
                val provided = source.getKey(property.key);
                val value = property.parser.parse(provided);
                if (value != null) {
                    return ProvidedValue(value, source.getName());
                }
            }
        }
        return null
    }
}


sealed class PropertyValue {}
data class ProvidedValue(val value: Any, val source: String) : PropertyValue()
object NoValue : PropertyValue()

interface DetectPropertySource {
    fun hasKey(key: String) : Boolean
    fun getKey(key: String) : String?
    fun getName() : String
}

open abstract class Property(val key: String) {
    //this is the most basic property
    //it has no type information and a value cannot be retrieved for it (without a subclass)
    //it is recommended things that only appear in the help but do not actually appear as 'keys' use this type - phone home passthrough for example.
    var name: String? = null;
    var from: String? = null;
    var helpShort: String? = null;
    var helpLong:String? = null;
    var primaryGroup: Group? = null;
    var additionalGroups: List<Group>? = null;
    var category: Category = Category.Simple;

    fun info(name:String, from: String): Property {
        this.name = name;
        this.from = from;
        return this
    }
    fun help(short:String, long: String? = null): Property  {
        this.helpShort = short;
        this.helpLong = long;
        return this
    }
    fun groups(primaryGroup: Group, vararg additionalGroups: Group): Property  {
        this.primaryGroup = primaryGroup;
        this.additionalGroups = additionalGroups?.toList()
        return this
    }
    fun category(category: Category): Property  {
        this.category = category;
        return this
    }

    open fun isCaseSensitive(): Boolean = false
    open fun isOnlyExampleValues(): Boolean = false
    open fun listExampleValues(): List<String>? = emptyList()
    open fun describeDefault(): String? = null
}

open abstract class TypedProperty<T>(key: String, val parser: ValueParser<T>) : Property(key){

}

open abstract class OptionalProperty<T>(key: String, parser: ValueParser<T>) : TypedProperty<T>(key, parser) {
    //this is a property with a key and a value, but the value is optional.
}

open abstract class RequiredProperty<T>(key: String, parser: ValueParser<T>, val default: T) : TypedProperty<T>(key, parser) {
    //this is a property with a key and with a default value, it will always have a value.
}

abstract class ValueParser<T> {
    abstract fun parse(value: String?) : T?;
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

class FilterableEnumListValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<List<FilterableEnumValue<T>>> () {
    override fun parse(value: String?) : List<FilterableEnumValue<T>>? = when {
        value != null && value.isNotBlank() -> value.split(",").map {it ->
            when {
                it.toLowerCase() == "none" -> None<T>()
                it.toLowerCase() == "all" -> All<T>()
                else -> Value<T>(valueOf(it)!!)
            }
        }.toList()
        else -> null
    }
}


class SoftEnumValueOfParser<T>(val valueOf: (String) -> T?) : ValueParser<SoftEnumValue<T>> () {
    override fun parse(value: String?) : SoftEnumValue<T>? = when {
        value != null && value.isNotBlank() -> {
            val enumValue = valueOf(value)
            if (enumValue != null){
                ActualValue(enumValue)
            }else {
                StringValue(value)
            }
        }
        else -> null
    }
}

class DetectToolValueParser : ValueParser<DetectTool> () {
    override fun parse(value: String?) : DetectTool? = when {
        value != null && value.isNotBlank() -> DetectTool.valueOf(value)
        else -> null
    }
}

class DefaultStringValue (val provided: String?, val default: String) {
    fun get() : String {
        return provided ?: default
    }
}

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

class SoftEnumProperty<T>(key: String, valueOf: (String) -> T?, val values: List<T>) : OptionalProperty<T>(key, EnumValueOfParser(valueOf)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = false
}

sealed class SoftEnumValue<T>
class ActualValue<T>(val value: T) : SoftEnumValue<T>()
class StringValue<T>(val value: String) : SoftEnumValue<T>()

sealed class FilterableEnumValue<T>
class All<T>() : FilterableEnumValue<T>()
class None<T>() : FilterableEnumValue<T>()
class Value<T>(val value: T) : FilterableEnumValue<T>()
