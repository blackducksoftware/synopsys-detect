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
package com.synopsys.integration.configuration.property.base;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.util.PropertyUtils;

/**
 * This is a property with a key and with a default value, it will always have a value.
 */
// Using @JvmSuppressWildcards to prevent the Kotlin compiler from generating wildcard types: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics
public abstract class ValuedListProperty<T> extends ValuedProperty<List<T>> {
    public ValuedListProperty(@NotNull final String key, @NotNull final ValueParser<List<T>> valueParser, final List<T> defaultValue) {
        super(key, valueParser, defaultValue);
    }

    @Override
    public boolean isCommaSeparated() {
        return true;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }
}