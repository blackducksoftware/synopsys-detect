package com.synopsys.integration.detect.config

import com.synopsys.integration.detect.DetectTool
import java.lang.Exception
import java.lang.RuntimeException

class DetectConfig (val orderedPropertySources: List<DetectPropertySource>) {
    val resolvedCache : MutableMap<String, PropertyValue> = mutableMapOf();

    fun <T> getValueOrNull(property: OptionalProperty<T>): T? {
        return try {
            getValue(property)
        } catch (e: InvalidPropertyException) {
            null
        }
    }

    fun <T> getValueOrDefault(property: RequiredProperty<T>): T {
        return try {
            getValue(property)
        } catch (e: InvalidPropertyException) {
            property.default
        }
    }

    @Throws(InvalidPropertyException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: OptionalProperty<T>): T? {
        return when (val value = resolveFromCache(property)) {
            is ProvidedValue -> value.value as T
            is ExceptionValue -> throw InvalidPropertyException(property.key, value.source, value.exception)
            is NoValue -> null
        }
   }

    @Throws(InvalidPropertyException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: RequiredProperty<T>): T {
        return when (val value = resolveFromCache(property)) {
            is ProvidedValue -> value.value as T
            is ExceptionValue -> throw InvalidPropertyException(property.key, value.source, value.exception)
            is NoValue -> property.default
        }
    }

    //TODO: Re-implement
    fun getDockerProperties(): Map<String, String> {
        return emptyMap();
    }

    //TODO: Re-implement
    fun getPhoneHomeProperties(): Map<String, String> {
        return emptyMap();
    }

    //TODO: Re-implement
    fun getRaw(): Map<String, String> {
        return emptyMap();
    }

    //TODO: Re-implement
    fun getRaw(keys: Set<String>): Map<String, String> {
        return emptyMap();
    }

    //TODO: Re-implement
    fun <T> wasPropertyProvided(property: TypedProperty<T>): Boolean {
        return when (resolveFromCache(property)) {
            is ProvidedValue -> true
            is ExceptionValue -> true
            is NoValue -> false
        }
    }

    private fun <T> resolveFromCache(property: TypedProperty<T>) : PropertyValue {
        if (!resolvedCache.containsKey(property.key)) {
            resolvedCache[property.key] = resolveFromPropertySource(property)
        }

        return resolvedCache[property.key] ?: throw RuntimeException("Could not resolve a value, something has gone wrong with properties!")    }

    private fun <T> resolveFromPropertySource(property: TypedProperty<T>) : PropertyValue {
        for (source in orderedPropertySources){
            if (source.hasKey(property.key)) {
                val rawValue = source.getKey(property.key);
                if (rawValue != null) {//if this property source is the first with a value, it is the canonical source of this property key.
                    val propertySourcName = source.getName()
                    try {
                        val value = property.parser.parse(rawValue)
                        return ProvidedValue(value as Any, propertySourcName); //TODO: Not sure why this requires me to cast it to 'Any'
                    } catch (e: ValueParseException) {
                        return ExceptionValue(e, rawValue, propertySourcName)
                    }
                }
            }
        }
        return NoValue //No property source could provide the value of this property.
    }
}

class InvalidPropertyException (val propertyKey:String, val propertySourceName:String, val innerException: ValueParseException) : Exception("The key '${propertyKey}' in property source '${propertySourceName}' contained a value that could not be reasonably converted to the properties type. The exception was: ${innerException.localizedMessage ?: "Unknown"}", innerException) {}

sealed class PropertyValue {}
data class ProvidedValue(val value: Any, val source: String) : PropertyValue()//A property source contained a value and the value could be parsed to the proper type.
data class ExceptionValue(val exception: ValueParseException, val rawValue: String, val source: String): PropertyValue()//A property source contained a value but the value could NOT be parsed to the proper type.
object NoValue : PropertyValue()//No property source contained a value.
