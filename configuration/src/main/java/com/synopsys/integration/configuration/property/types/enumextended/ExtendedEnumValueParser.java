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
package com.synopsys.integration.configuration.property.types.enumextended;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.types.enums.SafeEnumValueParser;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

class ExtendedEnumValueParser<E extends Enum<E>, B extends Enum<B>> extends ValueParser<ExtendedEnumValue<E, B>> {
    private final Class<E> enumClassE;
    private final Class<B> enumClassB;
    private final SafeEnumValueParser<E> extendedParser;
    private final SafeEnumValueParser<B> baseParser;

    public ExtendedEnumValueParser(@NotNull final Class<E> enumClassE, @NotNull final Class<B> enumClassB) {
        this.enumClassE = enumClassE;
        this.enumClassB = enumClassB;
        this.extendedParser = new SafeEnumValueParser<>(enumClassE);
        this.baseParser = new SafeEnumValueParser<>(enumClassB);
    }

    @Override
    @NotNull
    public ExtendedEnumValue<E, B> parse(@NotNull final String value) throws ValueParseException {
        final Optional<E> eValue = extendedParser.parse(value);
        if (eValue.isPresent()) {
            return ExtendedEnumValue.ofExtendedValue(eValue.get());
        }
        final Optional<B> bValue = baseParser.parse(value);
        if (bValue.isPresent()) {
            return ExtendedEnumValue.ofBaseValue(bValue.get());
        }
        final List<String> combinedOptions = new ArrayList<>();
        combinedOptions.addAll(EnumPropertyUtils.getEnumNames(enumClassE));
        combinedOptions.addAll(EnumPropertyUtils.getEnumNames(enumClassB));
        final String optionText = String.join(",", combinedOptions);
        throw new ValueParseException(value, "either enum", "Value was must be one of " + optionText);
    }
}

