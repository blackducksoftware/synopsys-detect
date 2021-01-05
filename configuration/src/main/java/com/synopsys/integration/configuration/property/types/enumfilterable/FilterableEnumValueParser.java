/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.configuration.property.types.enumfilterable;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.types.enums.EnumValueParser;

public class FilterableEnumValueParser<T extends Enum<T>> extends ValueParser<FilterableEnumValue<T>> {
    private EnumValueParser<T> enumValueParser;

    public FilterableEnumValueParser(@NotNull Class<T> enumClass) {
        this.enumValueParser = new EnumValueParser<>(enumClass);
    }

    @NotNull
    @Override
    public FilterableEnumValue<T> parse(@NotNull final String value) throws ValueParseException {
        String trimmedValue = value.toLowerCase().trim();
        if (trimmedValue.equals("none")) {
            return FilterableEnumValue.noneValue();
        } else if (trimmedValue.equals("all")) {
            return FilterableEnumValue.allValue();
        } else {
            return FilterableEnumValue.value(enumValueParser.parse(value));
        }
    }
}
