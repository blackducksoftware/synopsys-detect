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

import com.synopsys.integration.configuration.parse.ListValueParser
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedListProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty
import org.apache.commons.lang3.EnumUtils

class NullableSoftEnumProperty<T : Enum<T>>(key: String, private val enumClass: Class<T>) : NullableProperty<SoftEnumValue<T>>(key, SoftEnumValueParser(enumClass)) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = EnumUtils.getEnumList(enumClass).map { it.toString() }.toList()
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeType(): String? = "Optional ${enumClass.simpleName}"
}

class SoftEnumProperty<T : Enum<T>>(key: String, default: SoftEnumValue<T>, private val enumClass: Class<T>) : ValuedProperty<SoftEnumValue<T>>(key, SoftEnumValueParser(enumClass), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = EnumUtils.getEnumList(enumClass).map { it.toString() }.toList()
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeType(): String? = enumClass.simpleName
    override fun describeDefault(): String? = default.toString()
}


class SoftEnumListProperty<T : Enum<T>>(key: String, default: List<SoftEnumValue<T>>, private val enumClass: Class<T>) : ValuedListProperty<SoftEnumValue<T>>(key, ListValueParser<SoftEnumValue<T>>(SoftEnumValueParser(enumClass)), default) {
    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = EnumUtils.getEnumList(enumClass).map { it.toString() }.toList()
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeType(): String? = "${enumClass.simpleName} List"
    override fun describeDefault(): String? = default.toString()
}