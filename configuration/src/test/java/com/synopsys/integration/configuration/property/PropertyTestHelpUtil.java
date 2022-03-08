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

    public static <T, V> void assertAllHelpValid(NullableProperty<T, V> property) {
        assertAllHelpValid(property, null);
    }

    public static <T, V> void assertAllHelpValid(NullableProperty<T, V> property, @Nullable List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    public static <T, V> void assertAllHelpValid(ValuedProperty<T, V> property) {
        assertAllHelpValid(property, null);
    }

    public static <T, V> void assertAllHelpValid(ValuedProperty<T, V> property, @Nullable List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasDefaultDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    public static <T, V> void assertAllListHelpValid(ValuedListProperty<T, V> property) {
        assertAllListHelpValid(property, null);
    }

    public static <T, V> void assertAllListHelpValid(ValuedListProperty<T, V> property, @Nullable List<String> expectedExampleValues) {
        assertValidTypeDescription(property);
        assertHasDefaultDescription(property);
        assertHasExampleValues(property, expectedExampleValues);
    }

    //#endregion Recommended Usage

    //#region Advanced Usage

    private static final String NULLABLE_TYPE_DESCRIPTION_PREFIX = "Optional ";
    private static final String VALUED_TYPE_DESCRIPTION_POSTFIX = " List";

    public static <T, V> void assertValidTypeDescription(NullableProperty<T, V> property) {
        assertHasTypeDescription(property);
        Assertions.assertTrue(
            property.describeType().startsWith(NULLABLE_TYPE_DESCRIPTION_PREFIX),
            String.format(
                "%s is a %s so its type description should start with '%s'.",
                property.getClass().getSimpleName(),
                NullableProperty.class.getSimpleName(),
                NULLABLE_TYPE_DESCRIPTION_PREFIX
            )
        );
    }

    public static <T, V> void assertValidTypeDescription(ValuedProperty<T, V> property) {
        assertHasTypeDescription(property);
        Assertions.assertFalse(
            property.describeType().startsWith(NULLABLE_TYPE_DESCRIPTION_PREFIX),
            String.format(
                "%s is a %s so its type description should not start with '%s'.",
                property.getClass().getSimpleName(),
                ValuedProperty.class.getSimpleName(),
                NULLABLE_TYPE_DESCRIPTION_PREFIX
            )
        );
    }

    public static <T, V> void assertValidTypeDescription(ValuedListProperty<T, V> property) {
        assertHasTypeDescription(property);
        Assertions.assertTrue(
            property.describeType().endsWith(VALUED_TYPE_DESCRIPTION_POSTFIX),
            String.format(
                "%s is a %s so its type description should end with '%s'.",
                property.getClass().getSimpleName(),
                ValuedListProperty.class.getSimpleName(),
                VALUED_TYPE_DESCRIPTION_POSTFIX
            )
        );
    }

    private static <T, V> void assertHasTypeDescription(TypedProperty<T, V> property) {
        Assertions.assertNotNull(property.describeType(), String.format("%s is a typed property so it should be able to describe its type.", property.getClass().getSimpleName()));
    }

    public static <T, V> void assertHasExampleValues(TypedProperty<T, V> property, @Nullable List<String> expectedExampleValues) {
        if (expectedExampleValues != null) {
            Assertions.assertNotNull(
                property.listExampleValues(),
                String.format("A %s property has a limited number of values that should be described.", property.describeType())
            );
            //Ideally would use a CollectionUtils.areEqual but this gets us close enough.
            Assertions.assertEquals(
                expectedExampleValues.size(),
                property.listExampleValues().size(),
                String.format("The %s provided unexpected example values.", property.getClass().getSimpleName())
            );
            Assertions.assertTrue(
                CollectionUtils.containsAll(expectedExampleValues, property.listExampleValues()),
                String.format("The %s provided unexpected example values.", property.getClass().getSimpleName())
            );
            Assertions.assertTrue(
                CollectionUtils.containsAll(property.listExampleValues(), expectedExampleValues),
                String.format("The %s provided unexpected example values.", property.getClass().getSimpleName())
            );
        }
    }

    private static <T, V> void assertHasExampleValues(ValuedListProperty<T, V> property, @Nullable List<String> expectedExampleValues) {
        if (expectedExampleValues != null) {
            Assertions.assertNotNull(
                property.listExampleValues(),
                String.format("A %s property has a limited number of values that should be described.", property.describeType())
            );
            Assertions.assertTrue(
                CollectionUtils.containsAll(expectedExampleValues, property.listExampleValues()),
                String.format("The %s provided unexpected example values.", property.getClass().getSimpleName())
            );
        }
    }

    public static <T, V> void assertHasDefaultDescription(ValuedProperty<T, V> property) {
        Assertions.assertNotNull(
            property.describeDefault(),
            String.format("%s is a non-null typed property and should describe its default value.", property.getClass().getSimpleName())
        );
    }

    //#endregion Advanced Usage
}