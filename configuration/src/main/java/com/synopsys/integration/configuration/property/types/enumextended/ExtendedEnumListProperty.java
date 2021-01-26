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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;
import com.synopsys.integration.configuration.util.PropertyUtils;

public class ExtendedEnumListProperty<E extends Enum<E>, B extends Enum<B>> extends ValuedListProperty<ExtendedEnumValue<E, B>> {
    private final List<String> allOptions;
    private final Class<B> bClass;

    public ExtendedEnumListProperty(@NotNull final String key, @NotNull final List<ExtendedEnumValue<E, B>> defaultValue, @NotNull final Class<E> eClass, @NotNull final Class<B> bClass) {
        super(key, new ListValueParser<>(new ExtendedEnumValueParser<>(eClass, bClass)), defaultValue);

        allOptions = new ArrayList<>();
        allOptions.addAll(EnumPropertyUtils.getEnumNames(eClass));
        allOptions.addAll(EnumPropertyUtils.getEnumNames(bClass));
        this.bClass = bClass;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return allOptions;
    }

    @Override
    public boolean isOnlyExampleValues() {
        return true;
    }

    @Nullable
    @Override
    public String describeType() {
        return bClass.getSimpleName() + " List";
    }
}
