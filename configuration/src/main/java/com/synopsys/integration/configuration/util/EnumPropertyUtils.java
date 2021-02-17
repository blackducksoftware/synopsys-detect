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
package com.synopsys.integration.configuration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;

public class EnumPropertyUtils {

    public static <T extends Enum<T>> List<String> getEnumNamesAnd(Class<T> enumClass, String... additional) {
        List<String> exampleValues = new ArrayList<>();
        exampleValues.addAll(Arrays.asList(additional));
        exampleValues.addAll(EnumPropertyUtils.getEnumNames(enumClass));
        return exampleValues;
    }

    public static <T extends Enum<T>> List<String> getEnumNames(Class<T> enumClass) {
        final List<T> values = new ArrayList<>(EnumUtils.getEnumList(enumClass));
        return values.stream()
                   .map(Objects::toString)
                   .collect(Collectors.toList());
    }
}
