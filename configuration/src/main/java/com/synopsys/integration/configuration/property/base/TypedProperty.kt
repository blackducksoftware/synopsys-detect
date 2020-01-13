package com.synopsys.integration.configuration.property.base

import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.Property

abstract class TypedProperty<T>(key: String, val parser: ValueParser<T>) : Property(key)
