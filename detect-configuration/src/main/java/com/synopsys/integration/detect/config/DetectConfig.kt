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
        return when (resolve(property)) {
            is ProvidedValue -> true
            is NoValue -> false
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

