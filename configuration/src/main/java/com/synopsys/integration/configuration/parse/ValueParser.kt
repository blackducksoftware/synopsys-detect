package com.synopsys.integration.configuration.parse

abstract class ValueParser<T> {
    @Throws(ValueParseException::class)
    abstract fun parse(value: String): T
}