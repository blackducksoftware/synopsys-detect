package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.PassthroughProperty
import com.synopsys.integration.configuration.property.base.TypedProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class DetectConfig(private val orderedPropertySources: List<DetectPropertySource>) {
    private val resolvedCache: MutableMap<String, PropertyValue> = mutableMapOf();

    fun <T> getValueOrNull(property: NullableProperty<T>): T? {
        return try {
            getValue(property)
        } catch (e: InvalidPropertyException) {
            null
        }
    }

    fun <T> getValueOrDefault(property: ValuedProperty<T>): T {
        return try {
            getValue(property)
        } catch (e: InvalidPropertyException) {
            property.default
        }
    }

    @Throws(InvalidPropertyException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: NullableProperty<T>): T? {
        return when (val value = resolveFromCache(property)) {
            is ProvidedValue<*> -> value.value as T
            is ExceptionValue -> throw InvalidPropertyException(property.key, value.source, value.exception)
            is NoValue -> null
        }
    }

    @Throws(InvalidPropertyException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: ValuedProperty<T>): T {
        return when (val value = resolveFromCache(property)) {
            is ProvidedValue<*> -> value.value as T
            is ExceptionValue -> throw InvalidPropertyException(property.key, value.source, value.exception)
            is NoValue -> property.default
        }
    }

    // TODO: Re-implement
    fun getDockerProperties(): Map<String, String> {
        return emptyMap();
    }

    // TODO: Re-implement
    fun getPhoneHomeProperties(): Map<String, String> {
        return emptyMap();
    }

    // TODO: Re-implement
    fun getRaw(): Map<String, String> {
        return emptyMap();
    }

    // TODO: Re-implement
    fun getRaw(keys: Set<String>): Map<String, String> {
        return emptyMap();
    }

    // TODO: Re-implement
    fun getRaw(matches: (String) -> Boolean): Map<String, String> {
        return emptyMap();
    }

    // TODO: Re-implement
    fun getRaw(property: PassthroughProperty): Map<String, String> {
        return emptyMap();
    }

    // TODO: Re-implement
    fun <T> wasPropertyProvided(property: TypedProperty<T>): Boolean {
        return when (resolveFromCache(property)) {
            is ProvidedValue<*> -> true
            is ExceptionValue -> true
            is NoValue -> false
        }
    }

    private fun <T> resolveFromCache(property: TypedProperty<T>): PropertyValue {
        if (!resolvedCache.containsKey(property.key)) {
            resolvedCache[property.key] = resolveFromPropertySource(property)
        }

        return resolvedCache[property.key] ?: throw RuntimeException("Could not resolve a value, something has gone wrong with properties!")
    }

    private fun <T> resolveFromPropertySource(property: TypedProperty<T>): PropertyValue {
        for (source in orderedPropertySources) {
            if (source.hasKey(property.key)) {
                val rawValue = source.getKey(property.key);
                if (rawValue != null) { // If this property source is the first with a value, it is the canonical source of this property key.
                    val propertySourceName = source.getName()
                    return try {
                        val value = property.parser.parse(rawValue)
                        ProvidedValue(value, propertySourceName)
                    } catch (e: ValueParseException) {
                        ExceptionValue(e, rawValue, propertySourceName)
                    }
                }
            }
        }

        return NoValue // No property source could provide the value of this property.
    }
}

class InvalidPropertyException(propertyKey: String, propertySourceName: String, innerException: ValueParseException) : Exception("The key '${propertyKey}' in property source '${propertySourceName}' contained a value that could not be reasonably converted to the properties type. The exception was: ${innerException.localizedMessage
        ?: "Unknown"}", innerException) {}

sealed class PropertyValue {}
data class ProvidedValue<T>(val value: T, val source: String) : PropertyValue() // A property source contained a value and the value could be parsed to the proper type.
data class ExceptionValue(val exception: ValueParseException, val rawValue: String, val source: String) : PropertyValue() // A property source contained a value but the value could NOT be parsed to the proper type.
object NoValue : PropertyValue() // No property source contained a value.
