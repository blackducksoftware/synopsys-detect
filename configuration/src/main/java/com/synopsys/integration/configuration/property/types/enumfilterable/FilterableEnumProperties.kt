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
package com.synopsys.integration.configuration.property.types.enumfilterable

import com.synopsys.integration.configuration.parse.ListValueParser
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedListProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty
import org.apache.commons.lang3.EnumUtils

class NullableFilterableEnumProperty<T : Enum<T>>(key: String, private val enumClass: Class<T>) : NullableProperty<FilterableEnumValue<T>>(key, FilterableEnumValueParser(enumClass)) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? {
        val base = EnumUtils.getEnumList(enumClass).map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }

    override fun describeType(): String? = "Optional ${enumClass.simpleName}"
    override fun isOnlyExampleValues(): Boolean = true
}

class FilterableEnumProperty<T : Enum<T>>(key: String, default: FilterableEnumValue<T>, private val enumClass: Class<T>) : ValuedProperty<FilterableEnumValue<T>>(key, FilterableEnumValueParser(enumClass), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun describeDefault(): String? = defaultValue.toString()
    override fun listExampleValues(): List<String>? {
        val base = EnumUtils.getEnumList(enumClass).map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }

    override fun describeType(): String? = enumClass.simpleName
    override fun isOnlyExampleValues(): Boolean = true
}

class FilterableEnumListProperty<T : Enum<T>>(key: String, default: List<FilterableEnumValue<T>>, private val enumClass: Class<T>) : ValuedListProperty<FilterableEnumValue<T>>(key, ListValueParser<FilterableEnumValue<T>>(FilterableEnumValueParser(enumClass)), default) {
    override fun isCaseSensitive(): Boolean = true
    override fun listExampleValues(): List<String>? {
        val base = EnumUtils.getEnumList(enumClass).map { it.toString() }.toMutableList()
        base.add("ALL")
        base.add("NONE")
        return base
    }

    override fun describeType(): String? = "${enumClass.simpleName} List"
    override fun isOnlyExampleValues(): Boolean = true
}