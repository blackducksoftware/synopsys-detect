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
package com.synopsys.integration.configuration.config.value;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

import com.synopsys.integration.configuration.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.configuration.parse.ValueParseException;

public class ValuedPropertyValue<T> extends ResolvedPropertyValue<T> {
    @NotNull
    private final T value;

    public ValuedPropertyValue(@NotNull final T value, @NotNull final PropertyResolutionInfo propertyResolutionInfo) {
        super(propertyResolutionInfo);
        Assert.notNull(value, "Value cannot be null.");
        Assert.notNull(propertyResolutionInfo, "PropertyResolutionInfo cannot be null.");
        this.value = value;
    }

    @Override
    public Optional<T> getValue() {
        return Optional.of(value);
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.empty();
    }
}
