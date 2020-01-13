package com.synopsys.integration.configuration.property.types.stringlist

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

class NullableStringListProperty(key: String) : NullableProperty<List<String>>(key, StringListValueParser()) {}

// Using @JvmSuppressWildcards to prevent the Kotlin compiler from generating wildcard types: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics
class StringListProperty(key: String, default: List<String>) : ValuedProperty<@JvmSuppressWildcards List<String>>(key, StringListValueParser(), default) {
    override fun describeDefault(): String? = default.joinToString { "," }
}