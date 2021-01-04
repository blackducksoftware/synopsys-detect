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
package com.synopsys.integration.configuration.property.types.enumsoft;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.Assert;

// An enum that can be the given ENUM or can be STRING
// Useful for properties that might want to be extended by the user such as Black Duck settings where we may know some of the values but don't care if we do not.
public class SoftEnumValue<T extends Enum<T>> {
    @Nullable
    private final String softValue;
    @Nullable
    private final T enumValue;

    private SoftEnumValue(@Nullable final String softValue, @Nullable final T enumValue) {
        if (softValue == null) {
            Assert.notNull(enumValue, "You must provide either a enum value or a soft value.");
        } else if (enumValue == null) {
            Assert.notNull(softValue, "You must provide either a enum value or a soft value.");
        }
        Assert.isTrue(enumValue == null || softValue == null, "One value must be null.");

        this.softValue = softValue;
        this.enumValue = enumValue;
    }

    @NotNull
    public static <E extends Enum<E>> SoftEnumValue<E> ofSoftValue(@NotNull final String baseValue) {
        return new SoftEnumValue<>(baseValue, null);
    }

    @NotNull
    public static <E extends Enum<E>> SoftEnumValue<E> ofEnumValue(@NotNull final E enumValue) {
        return new SoftEnumValue<>(null, enumValue);
    }

    public Optional<String> getSoftValue() {
        return Optional.ofNullable(softValue);
    }

    public Optional<T> getEnumValue() {
        return Optional.ofNullable(enumValue);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        final SoftEnumValue<?> that = (SoftEnumValue<?>) o;
        return Objects.equals(softValue, that.softValue) &&
                   Objects.equals(enumValue, that.enumValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(softValue, enumValue);
    }

    @Override
    public String toString() {
        if (enumValue != null) {
            return enumValue.toString();
        } else {
            return softValue;
        }
    }
}


