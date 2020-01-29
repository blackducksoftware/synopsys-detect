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
package com.synopsys.integration.configuration.property.types.enums

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedListProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty
import org.apache.commons.lang3.EnumUtils

class NullableEnumProperty<T : Enum<T>>(key: String, private val enumClass: Class<T>) : NullableProperty<T>(key, EnumValueParser(enumClass)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? = EnumUtils.getEnumList(enumClass).map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
    override fun describeType(): String? = enumClass.simpleName
}

class EnumProperty<T : Enum<T>>(key: String, default: T, private val enumClass: Class<T>) : ValuedProperty<T>(key, EnumValueParser(enumClass), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.toString()
    override fun listExampleValues(): List<String>? = EnumUtils.getEnumList(enumClass).map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
    override fun describeType(): String? = enumClass.simpleName
}

class EnumListProperty<T : Enum<T>>(key: String, default: List<T>, private val enumClass: Class<T>) : ValuedListProperty<T>(key, EnumListValueParser(enumClass), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = default.joinToString { "," }
    override fun listExampleValues(): List<String>? = EnumUtils.getEnumList(enumClass).map { it.toString() }
    override fun isOnlyExampleValues(): Boolean = true
    override fun describeType(): String? = "${enumClass.simpleName} List"
    override fun isCommaSeparated(): Boolean = true
}