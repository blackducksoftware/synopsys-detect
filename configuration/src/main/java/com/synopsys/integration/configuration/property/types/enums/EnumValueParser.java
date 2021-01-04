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
package com.synopsys.integration.configuration.property.types.enums;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class EnumValueParser<T extends Enum<T>> extends ValueParser<T> {
    private final Class<T> enumClass;
    private SafeEnumValueParser<T> parser;

    public EnumValueParser(@NotNull Class<T> enumClass) {
        this.enumClass = enumClass;
        this.parser = new SafeEnumValueParser<T>(enumClass);
    }

    @NotNull
    @Override
    public T parse(@NotNull String value) throws ValueParseException {
        Optional<T> enumValue = this.parser.parse(value);
        if (enumValue.isPresent()) {
            return enumValue.get();
        } else {
            throw new ValueParseException(value, "enum", "Unable to convert '$value' to one of " + String.join(",", EnumPropertyUtils.getEnumNames(enumClass)));
        }
    }
}


