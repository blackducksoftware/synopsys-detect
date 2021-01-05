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

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

import com.synopsys.integration.common.util.Bdo;

public class ExtendedEnumValue<E extends Enum<E>, B extends Enum<B>> {
    @Nullable
    private final E extendedValue;
    @Nullable
    private final B baseValue;

    private ExtendedEnumValue(@Nullable final E extendedValue, @Nullable final B baseValue) {
        if (baseValue == null) {
            Assert.notNull(extendedValue, "You must provide either a base value or an extended value.");
        } else if (extendedValue == null) {
            Assert.notNull(baseValue, "You must provide either a base value or an extended value.");
        }
        Assert.isTrue(baseValue == null || extendedValue == null, "One value must be null.");

        this.baseValue = baseValue;
        this.extendedValue = extendedValue;
    }

    @NotNull
    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofBaseValue(@NotNull final B baseValue) {
        return new ExtendedEnumValue<>(null, baseValue);
    }

    @NotNull
    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofExtendedValue(@NotNull final E extendedValue) {
        return new ExtendedEnumValue<>(extendedValue, null);
    }

    @NotNull
    public Optional<E> getExtendedValue() {
        return Optional.ofNullable(extendedValue);
    }

    @NotNull
    public Optional<B> getBaseValue() {
        return Optional.ofNullable(baseValue);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final ExtendedEnumValue<?, ?> that = (ExtendedEnumValue<?, ?>) o;

        if (getExtendedValue().isPresent() ? !getExtendedValue().equals(that.getExtendedValue()) : that.getExtendedValue().isPresent())
            return false;
        return getBaseValue().isPresent() ? getBaseValue().equals(that.getBaseValue()) : !that.getBaseValue().isPresent();
    }

    @Override
    public int hashCode() {
        int result = getExtendedValue().isPresent() ? getExtendedValue().hashCode() : 0;
        result = 31 * result + (getBaseValue().isPresent() ? getBaseValue().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return Bdo.of(getBaseValue().map(Enum::name))
                   .or(getExtendedValue().map(Enum::name))
                   .orElseThrow(() -> new IllegalStateException("Extended enum values should be created with a default value."));
    }
}
