package com.synopsys.integration.configuration.property.base

import com.synopsys.integration.configuration.property.Property

abstract class PassthroughProperty(key: String) : Property(key) {
    //This is a property with no value, but with potentially many values all prefixed by the same key.
}