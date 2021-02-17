/*
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
package com.synopsys.integration.configuration.property.types.enumsoft;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.util.EnumPropertyUtils;

public class NullableSoftEnumProperty<E extends Enum<E>> extends NullableProperty<SoftEnumValue<E>> {
    @NotNull
    private final Class<E> enumClass;

    public NullableSoftEnumProperty(@NotNull final String key, @NotNull Class<E> enumClass) {
        super(key, new SoftEnumValueParser<>(enumClass));
        this.enumClass = enumClass;
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNames(enumClass);
    }

    @Override
    public boolean isOnlyExampleValues() {
        return false;
    }

    @Nullable
    @Override
    public String describeType() {
        return "Optional " + enumClass.getSimpleName();
    }
}
