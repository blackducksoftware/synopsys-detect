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

import com.synopsys.integration.configuration.property.base.ValuedProperty

const val TYPE_DESCRIPTION = "Soft Enum"

class SoftEnumProperty<T>(key: String, default: SoftEnumValue<T>, valueOf: (String) -> T?, val value: T) : ValuedProperty<SoftEnumValue<T>>(key, SoftEnumValueParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = listOf(value.toString())
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeType(): String? = TYPE_DESCRIPTION
    override fun isCommaSeparated(): Boolean = true
}


class SoftEnumListProperty<T>(key: String, default: List<SoftEnumValue<T>>, valueOf: (String) -> T?, val values: List<T>) : ValuedProperty<List<SoftEnumValue<T>>>(key, SoftEnumListValueParser(valueOf), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = values.map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeDefault(): String? = default.joinToString { "," }
    override fun describeType(): String? = "$TYPE_DESCRIPTION List"
    override fun isCommaSeparated(): Boolean = true
}