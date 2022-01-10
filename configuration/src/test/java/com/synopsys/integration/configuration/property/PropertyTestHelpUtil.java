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

    public static <T> void assertAllHelpValid(NullableProperty<T> property) {
        assertAllHelpValid(property, null);
    }

    public static <T> void assertAllHelpValid(NullableProperty<T> property, @Nullable List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    public static <T> void assertAllHelpValid(ValuedProperty<T> property) {
        assertAllHelpValid(property, null);
    }

    public static <T> void assertAllHelpValid(ValuedProperty<T> property, @Nullable List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasDefaultDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    public static <T> void assertAllListHelpValid(ValuedListProperty<T> property) {
        assertAllListHelpValid(property, null);
    }

    public static <T> void assertAllListHelpValid(ValuedListProperty<T> property, @Nullable List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasDefaultDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    //#endregion Recommended Usage

    //#region Advanced Usage

    private static final String NULLABLE_TYPE_DESCRIPTION_PREFIX = "Optional ";
    private static final String VALUED_TYPE_DESCRIPTION_POSTFIX = " List";

    public static <T> void assertValidTypeDescription(NullableProperty<T> property) {
        assertHasTypeDescription(property);
        Assertions.assertTrue(property.describeType().startsWith(NULLABLE_TYPE_DESCRIPTION_PREFIX),
            String.format("%s is a %s so its type description should start with '%s'.", property.getClass().getSimpleName(), NullableProperty.class.getSimpleName(), NULLABLE_TYPE_DESCRIPTION_PREFIX));
    }

    public static <T> void assertValidTypeDescription(ValuedProperty<T> property) {
        assertHasTypeDescription(property);
        Assertions.assertFalse(property.describeType().startsWith(NULLABLE_TYPE_DESCRIPTION_PREFIX),
            String.format("%s is a %s so its type description should not start with '%s'.", property.getClass().getSimpleName(), ValuedProperty.class.getSimpleName(), NULLABLE_TYPE_DESCRIPTION_PREFIX));
    }

    public static <T> void assertValidTypeDescription(ValuedListProperty<T> property) {
        assertHasTypeDescription(property);
        Assertions.assertTrue(property.describeType().endsWith(VALUED_TYPE_DESCRIPTION_POSTFIX),
            String.format("%s is a %s so its type description should end with '%s'.", property.getClass().getSimpleName(), ValuedListProperty.class.getSimpleName(), VALUED_TYPE_DESCRIPTION_POSTFIX));
    }

    private static <T> void assertHasTypeDescription(TypedProperty<T> property) {
        Assertions.assertNotNull(property.describeType(), String.format("%s is a typed property so it should be able to describe its type.", property.getClass().getSimpleName()));
    }

    public static <T> void assertHasExampleValues(TypedProperty<T> property, @Nullable List<String> expectedExampleValues) {
        if (expectedExampleValues != null) {
            Assertions.assertNotNull(property.listExampleValues(), String.format("A %s property has a limited number of values that should be described.", property.describeType()));
            //Ideally would use a CollectionUtils.areEqual but this gets us close enough.
            Assertions.assertEquals(expectedExampleValues.size(), property.listExampleValues().size(), String.format("The %s provided unexpected example values.", property.getClass().getSimpleName()));
            Assertions.assertTrue(CollectionUtils.containsAll(expectedExampleValues, property.listExampleValues()), String.format("The %s provided unexpected example values.", property.getClass().getSimpleName()));
            Assertions.assertTrue(CollectionUtils.containsAll(property.listExampleValues(), expectedExampleValues), String.format("The %s provided unexpected example values.", property.getClass().getSimpleName()));
        }
    }

    private static <T> void assertHasExampleValues(ValuedListProperty<T> property, @Nullable List<String> expectedExampleValues) {
        if (expectedExampleValues != null) {
            Assertions.assertNotNull(property.listExampleValues(), String.format("A %s property has a limited number of values that should be described.", property.describeType()));
            Assertions.assertTrue(CollectionUtils.containsAll(expectedExampleValues, property.listExampleValues()), String.format("The %s provided unexpected example values.", property.getClass().getSimpleName()));
        }
    }

    public static <T> void assertHasDefaultDescription(ValuedProperty<T> property) {
        Assertions.assertNotNull(property.describeDefault(), String.format("%s is a non-null typed property and should describe its default value.", property.getClass().getSimpleName()));
    }

    //#endregion Advanced Usage
}