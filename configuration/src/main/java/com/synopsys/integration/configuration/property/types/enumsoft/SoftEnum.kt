package com.synopsys.integration.configuration.property.types.enumsoft

// An enum that can be the given ENUM or can be STRING
// Useful for properties that might want to be extended by the user such as Black Duck settings where we may know some of the values but don't care if we do not.
sealed class SoftEnumValue<T>
class ActualValue<T>(val value: T) : SoftEnumValue<T>()
class StringValue<T>(val value: String) : SoftEnumValue<T>()
