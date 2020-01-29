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
package com.synopsys.integration.configuration.property.types.enumsoft

// An enum that can be the given ENUM or can be STRING
// Useful for properties that might want to be extended by the user such as Black Duck settings where we may know some of the values but don't care if we do not.
sealed class SoftEnumValue<T : Enum<T>>

class ActualValue<T : Enum<T>>(val value: T) : SoftEnumValue<T>() {
    override fun toString(): String = value.toString()
}

class StringValue<T : Enum<T>>(val value: String) : SoftEnumValue<T>() {
    override fun toString(): String = value
}
