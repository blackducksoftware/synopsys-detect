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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.NotNull;

public class FilterableEnumUtils {

    public static <T extends Enum<T>> List<FilterableEnumValue<T>> noneList() {
        List<FilterableEnumValue<T>> list = new ArrayList<FilterableEnumValue<T>>();
        list.add(FilterableEnumValue.noneValue());
        return list;
    }

    public static <T extends Enum<T>> boolean containsNone(@NotNull List<FilterableEnumValue<T>> filterableList) {
        return filterableList.stream()
                   .anyMatch(FilterableEnumValue::isNone);
    }

    public static <T extends Enum<T>> boolean containsAll(@NotNull List<FilterableEnumValue<T>> filterableList) {
        return filterableList.stream()
                   .anyMatch(FilterableEnumValue::isAll);
    }

    public static <T extends Enum<T>> boolean containsValue(@NotNull List<FilterableEnumValue<T>> filterableList, T value) {
        return filterableList.stream()
                   .map(FilterableEnumValue::getValue)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .anyMatch(value::equals);
    }

    public static <T extends Enum<T>> List<T> toPresentValues(@NotNull List<FilterableEnumValue<T>> filterableList) {
        return filterableList.stream()
                   .map(FilterableEnumValue::getValue)
                   .filter(Optional::isPresent)
                   .map(Optional::get)
                   .collect(Collectors.toList());
    }

    public static <T extends Enum<T>> List<T> populatedValues(@NotNull List<FilterableEnumValue<T>> filterableList, Class<T> enumClass) {
        if (FilterableEnumUtils.containsNone(filterableList)) {
            return new ArrayList<>();
        } else if (FilterableEnumUtils.containsAll(filterableList)) {
            return EnumUtils.getEnumList(enumClass);
        } else {
            return FilterableEnumUtils.toPresentValues(filterableList);
        }
    }

}