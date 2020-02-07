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

import com.synopsys.integration.configuration.parse.ValueParseException
import com.synopsys.integration.configuration.parse.ValueParser
import com.synopsys.integration.configuration.property.types.enums.SafeEnumValueParser
import org.apache.commons.lang3.EnumUtils

class ExtendedEnumValueParser<E : Enum<E>, B : Enum<B>>(private val enumClassE: Class<E>, private val enumClassB: Class<B>) : ValueParser<ExtendedEnumValue<E, B>>() {
    private var extendedParser = SafeEnumValueParser(enumClassE)
    private var baseParser = SafeEnumValueParser(enumClassB)

    override fun parse(value: String): ExtendedEnumValue<E, B> {
        val eValue = extendedParser.parse(value);
        if (eValue != null) {
            return ExtendedValue(eValue)
        }
        val bValue = baseParser.parse(value);
        if (bValue != null) {
            return BaseValue(bValue)
        }
        val combinedOptions = EnumUtils.getEnumList(enumClassE).joinToString(",") + "," + EnumUtils.getEnumList(enumClassB).joinToString { "," }
        throw ValueParseException(value, "either enum", additionalMessage = "Value was must be one of $combinedOptions")
    }
}

