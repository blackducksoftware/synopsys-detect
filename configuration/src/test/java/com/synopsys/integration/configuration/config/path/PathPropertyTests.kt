package com.synopsys.integration.configuration.config.path

import com.synopsys.integration.configuration.config.InvalidPropertyException
import com.synopsys.integration.configuration.config.PropertyTest
import com.synopsys.integration.configuration.property.types.path.NullablePathProperty
import com.synopsys.integration.configuration.property.types.path.PathProperty
import com.synopsys.integration.configuration.property.types.path.PathValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class PathPropertyTests : PropertyTest() {
    private val exampleKey = "example.key"
    private val propertyWithDefault = PathProperty(exampleKey, PathValue("test/path"))
    private val nullableProperty = NullablePathProperty(exampleKey)

    @Test
    fun emptyDefaultPos1() {
        Assertions.assertEquals("test/path", emptyConfig().getValue(propertyWithDefault).toString())
    }

    @Test
    fun emptyDefaultsNull() {
        Assertions.assertNull(emptyConfig().getValue(nullableProperty))
    }

    @Test
    fun providedOverridesDefault() {
        val config = configOf(exampleKey to "overridden/value")
        val value = config.getValue(propertyWithDefault)
        Assertions.assertNotNull(value, "Config should not have returned null.")
        Assertions.assertEquals(PathValue("overridden/value"), value)
    }

    @Test
    fun providesValue() {
        val config = configOf(exampleKey to "/provided/value")
        val value = config.getValue(nullableProperty)
        Assertions.assertNotNull(value, "Config should not have returned null.")
        Assertions.assertEquals(PathValue("/provided/value"), value)
    }

    @ParameterizedTest()
    @ValueSource(strings = ["", " ", "     "])
    fun unknownValueThrows(value: String) {
        Assertions.assertThrows(InvalidPropertyException::class.java) {
            configOf("example.key" to value).getValue(propertyWithDefault)
        }
    }

    @ParameterizedTest()
    @ValueSource(strings = ["", " ", "     "])
    fun unknownValueGivesDefault(value: String) {
        Assertions.assertEquals(PathValue("test/path"), configOf("example.key" to value).getValueOrDefault(propertyWithDefault))
    }
}