/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.configuration.config

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.property.Property
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.PassthroughProperty
import com.synopsys.integration.configuration.property.base.TypedProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class PropertyConfiguration(private val orderedPropertySources: List<PropertySource>) {
    private val resolutionCache: MutableMap<String, PropertyResolution> = mutableMapOf()
    private val valueCache: MutableMap<String, PropertyValue> = mutableMapOf()

    //#region Recommended Usage
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
        return when (val value = valueFromCache(property)) {
            is TypedValue<*> -> value.value as T
            is ExceptionValue -> throw InvalidPropertyException(property.key, value.resolution.source, value.exception)
            is NoValue -> null
        }
    }

    @Throws(InvalidPropertyException::class)
    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(property: ValuedProperty<T>): T {
        return when (val value = valueFromCache(property)) {
            is TypedValue<*> -> value.value as T
            is ExceptionValue -> throw InvalidPropertyException(property.key, value.resolution.source, value.exception)
            is NoValue -> property.default
        }
    }

    fun wasKeyProvided(key: String): Boolean {
        return when (resolveFromCache(key)) {
            is SourceResolution -> true
            is NoResolution -> false
        }
    }

    fun <T> wasPropertyProvided(property: TypedProperty<T>): Boolean {
        return wasKeyProvided(property.key)
    }

    fun getPropertySource(property: Property): String? {
        return when (val value = resolveFromCache(property.key)) {
            is SourceResolution -> value.source
            is NoResolution -> null
        }
    }

    fun getPropertyOrigin(property: Property): String? {
        return when (val value = resolveFromCache(property.key)) {
            is SourceResolution -> value.origin
            is NoResolution -> null
        }
    }

    fun getKeys(): Set<String> {
        return orderedPropertySources.flatMap { it.getKeys() }.toSet()
    }

    fun <T> getPropertyException(property: TypedProperty<T>): ExceptionValue? {
        return when (val value = valueFromCache(property)) {
            is ExceptionValue -> value
            else -> null
        }
    }

    //#endregion Recommended Usage

    //region Advanced Usage
    fun getRaw(property: Property): String? {
        return when (val value = resolveFromCache(property.key)) {
            is NoResolution -> null
            is SourceResolution -> value.raw
        }
    }

    fun getRaw(): Map<String, String> {
        return getRaw { true }
    }

    fun getRaw(keys: Set<String>): Map<String, String> {
        return getRaw { key -> keys.contains(key) }
    }

    fun getRaw(matches: (String) -> Boolean): Map<String, String> {
        return getKeys()
                .filter(matches::invoke)
                .mapNotNull { key ->
                    when (val resolution = resolveFromCache(key)) {
                        is SourceResolution -> Pair(key, resolution.raw)
                        is NoResolution -> null
                    }
                }.toMap()
    }

    // Takes in a 'passthrough.key' and returns key map (whose keys have that value removed)
    // So value 'passthrough.key.example' is returned as 'example'
    fun getRaw(property: PassthroughProperty): Map<String, String> {
        return getRaw { key -> key.startsWith(property.key) }
                .map { pair -> pair.key.substring(property.key.length + 1) to pair.value }
                .toMap()
    }
    //endregion Advanced Usage

    //region Implementation Details
    private fun resolveFromCache(key: String): PropertyResolution {
        if (!resolutionCache.containsKey(key)) {
            resolutionCache[key] = resolveFromPropertySources(key)
        }

        return resolutionCache[key] ?: throw RuntimeException("Could not resolve a value, something has gone wrong with properties!")
    }

    private fun resolveFromPropertySources(key: String): PropertyResolution {
        for (source in orderedPropertySources) {
            if (source.hasKey(key)) {
                val rawValue = source.getValue(key)
                if (rawValue != null) { // If this property source is the first with a value, it is the canonical source of this property key.
                    val propertySourceName = source.getName()
                    val propertyOrigin = source.getOrigin(key) ?: "unknown"
                    return SourceResolution(propertySourceName, propertyOrigin, rawValue)
                }
            }
        }
        return NoResolution
    }

    private fun <T> valueFromCache(property: TypedProperty<T>): PropertyValue {
        if (!valueCache.containsKey(property.key)) {
            valueCache[property.key] = valueFromResolution(property)
        }

        return valueCache[property.key] ?: throw RuntimeException("Could not resolve a value, something has gone wrong with properties!")
    }

    private fun <T> valueFromResolution(property: TypedProperty<T>): PropertyValue {
        return when (val resolution = resolveFromCache(property.key)) {
            is NoResolution -> NoValue
            is SourceResolution -> coerceValue(property, resolution)
        }
    }

    private fun <T> coerceValue(property: TypedProperty<T>, resolution: SourceResolution): PropertyValue {
        return try {
            val value = property.parser.parse(resolution.raw)
            TypedValue(value, resolution)
        } catch (e: ValueParseException) {
            ExceptionValue(e, resolution)
        }
    }
    //endregion Implementation Details
}

class InvalidPropertyException(propertyKey: String, propertySourceName: String, innerException: ValueParseException) : Exception(
        "The key '${propertyKey}' in property source '${propertySourceName}' contained a value that could not be reasonably converted to the properties type. The exception was: ${innerException.localizedMessage ?: "Unknown"}",
        innerException
) {}

sealed class PropertyValue
data class TypedValue<T>(val value: T, val resolution: SourceResolution) : PropertyValue() // A property source contained a value and the value could be parsed to the proper type.
data class ExceptionValue(val exception: ValueParseException, val resolution: SourceResolution) : PropertyValue() // A property source contained a value but the value could NOT be parsed to the proper type.
object NoValue : PropertyValue()

sealed class PropertyResolution
data class SourceResolution(val source: String, val origin: String, val raw: String) : PropertyResolution()
object NoResolution : PropertyResolution() // No property source contained a value.