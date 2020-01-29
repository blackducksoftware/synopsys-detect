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
package com.synopsys.integration.configuration.property.types.enumextended

import com.synopsys.integration.configuration.property.base.ValuedProperty
import org.apache.commons.lang3.EnumUtils

class ExtendedEnumProperty<E : Enum<E>, B : Enum<B>>(key: String, default: ExtendedEnumValue<E, B>, enumClassE: Class<E>, val enumClassB: Class<B>) : ValuedProperty<ExtendedEnumValue<E, B>>(key, ExtendedEnumValueOfParser(enumClassE, enumClassB), default) {
    val options = mutableListOf<String>()

    init {
        options.addAll(EnumUtils.getEnumList(enumClassE).map { it.toString() })
        options.addAll(EnumUtils.getEnumList(enumClassB).map { it.toString() })
    }

    override fun isCaseSensitive(): Boolean = false
    override fun listExampleValues(): List<String>? = options
    override fun isOnlyExampleValues(): Boolean = false
    override fun describeDefault(): String? = default.toString()
    override fun describeType(): String? = enumClassB.simpleName
}