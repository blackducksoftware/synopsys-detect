package com.synopsys.integration.configuration.parse

class ValueParseException(rawValue: String, typeName: String, additionalMessage: String = "", innerException: Exception? = null) : Exception("Unable to parse raw value '${rawValue}' and coerce it into type '${typeName}'. $additionalMessage", innerException)
