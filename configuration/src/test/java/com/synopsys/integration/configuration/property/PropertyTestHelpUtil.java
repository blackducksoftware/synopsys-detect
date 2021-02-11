/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.configuration.property;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.TypedProperty;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;

/**
 * Ensures the Property classes are providing necessary property descriptions for help doc.
 */
public class PropertyTestHelpUtil {

    private PropertyTestHelpUtil() {
        throw new IllegalStateException("Utility class");
    }

    //#region Recommended Usage

    public static <T> void assertAllHelpValid(final NullableProperty<T> property) {
        assertAllHelpValid(property, null);
    }

    public static <T> void assertAllHelpValid(final NullableProperty<T> property, @Nullable final List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    public static <T> void assertAllHelpValid(final ValuedProperty<T> property) {
        assertAllHelpValid(property, null);
    }

    public static <T> void assertAllHelpValid(final ValuedProperty<T> property, @Nullable final List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasDefaultDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    public static <T> void assertAllListHelpValid(final ValuedListProperty<T> property) {
        assertAllListHelpValid(property, null);
    }

    public static <T> void assertAllListHelpValid(final ValuedListProperty<T> property, @Nullable final List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasDefaultDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    //#endregion Recommended Usage

    //#region Advanced Usage

    private static final String NULLABLE_TYPE_DESCRIPTION_PREFIX = "Optional ";
    private static final String VALUED_TYPE_DESCRIPTION_POSTFIX = " List";

    public static <T> void assertValidTypeDescription(final NullableProperty<T> property) {
        assertHasTypeDescription(property);
        Assertions.assertTrue(property.describeType().startsWith(NULLABLE_TYPE_DESCRIPTION_PREFIX),
            String.format("%s is a %s so its type description should start with '%s'.", property.getClass().getSimpleName(), NullableProperty.class.getSimpleName(), NULLABLE_TYPE_DESCRIPTION_PREFIX));
    }

    public static <T> void assertValidTypeDescription(final ValuedProperty<T> property) {
        assertHasTypeDescription(property);
        Assertions.assertFalse(property.describeType().startsWith(NULLABLE_TYPE_DESCRIPTION_PREFIX),
            String.format("%s is a %s so its type description should not start with '%s'.", property.getClass().getSimpleName(), ValuedProperty.class.getSimpleName(), NULLABLE_TYPE_DESCRIPTION_PREFIX));
        Assertions.assertFalse(property.describeType().endsWith(VALUED_TYPE_DESCRIPTION_POSTFIX),
            String.format("%s is a %s so its type description should not end with '%s'.", property.getClass().getSimpleName(), ValuedProperty.class.getSimpleName(), VALUED_TYPE_DESCRIPTION_POSTFIX));
    }

    public static <T> void assertValidTypeDescription(final ValuedListProperty<T> property) {
        assertHasTypeDescription(property);
        Assertions.assertTrue(property.describeType().endsWith(VALUED_TYPE_DESCRIPTION_POSTFIX),
            String.format("%s is a %s so its type description should end with '%s'.", property.getClass().getSimpleName(), ValuedListProperty.class.getSimpleName(), VALUED_TYPE_DESCRIPTION_POSTFIX));
    }

    private static <T> void assertHasTypeDescription(final TypedProperty<T> property) {
        Assertions.assertNotNull(property.describeType(), String.format("%s is a typed property so it should be able to describe its type.", property.getClass().getSimpleName()));
    }

    public static <T> void assertHasExampleValues(final TypedProperty<T> property, @Nullable final List<String> expectedExampleValues) {
        if (expectedExampleValues != null) {
            Assertions.assertNotNull(property.listExampleValues(), String.format("A %s property has a limited number of values that should be described.", property.describeType()));
            Assertions.assertTrue(CollectionUtils.containsAll(expectedExampleValues, property.listExampleValues()), String.format("The %s provided unexpected example values.", property.getClass().getSimpleName()));
        }
    }

    private static <T> void assertHasExampleValues(final ValuedListProperty<T> property, @Nullable final List<String> expectedExampleValues) {
        if (expectedExampleValues != null) {
            Assertions.assertNotNull(property.listExampleValues(), String.format("A %s property has a limited number of values that should be described.", property.describeType()));
            Assertions.assertTrue(CollectionUtils.containsAll(expectedExampleValues, property.listExampleValues()), String.format("The %s provided unexpected example values.", property.getClass().getSimpleName()));
        }
    }

    public static <T> void assertHasDefaultDescription(final ValuedProperty<T> property) {
        Assertions.assertNotNull(property.describeDefault(), String.format("%s is a non-null typed property and should describe its default value.", property.getClass().getSimpleName()));
    }

    //#endregion Advanced Usage
}