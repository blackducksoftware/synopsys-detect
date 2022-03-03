package com.synopsys.integration.detectable.detectable.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EnumListFilterTest {

    private enum TestValue {
        A,
        B
    }

    private interface TestClass {
        void handleObject(Object object);

        void handleRunnable();
    }

    @Test
    void shouldInclude() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        assertFalse(filter.shouldInclude(TestValue.A));
        assertTrue(filter.shouldInclude(TestValue.B));
    }

    @Test
    void shouldExclude() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        assertTrue(filter.shouldExclude(TestValue.A));
        assertFalse(filter.shouldExclude(TestValue.B));
    }

    @Test
    void shouldIncludeNullable() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        assertFalse(filter.shouldInclude(TestValue.A, "some-value"), "Object is not null, but value is excluded. Should be excluded.");
        assertFalse(filter.shouldInclude(TestValue.A, null), "Object is null and value is excluded. Should be excluded.");
        assertTrue(filter.shouldInclude(TestValue.B, "some-value"), "Object is not null and value not excluded. Should be included.");
        assertFalse(filter.shouldInclude(TestValue.B, null), "Object is null even though value is included. Should be excluded.");
    }

    @Test
    void shouldExcludeNullable() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        assertTrue(filter.shouldExclude(TestValue.A, "some-value"), "Object is not null, but value should be excluded. Should be excluded.");
        assertTrue(filter.shouldExclude(TestValue.A, null), "Object is null, but value should be excluded. Should be excluded.");
        assertFalse(filter.shouldExclude(TestValue.B, "some-value"), "Object is not null and value is not excluded. Should be included.");
        assertTrue(filter.shouldExclude(TestValue.B, null), "Object is null even though value is not excluded. Should be excluded.");
    }

    @Test
    void ifShouldIncludeNullable() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        String dependency = null;
        filter.ifShouldInclude(TestValue.B, dependency, object -> fail("Although the value should be included, it was null and should be filtered."));

        // Now test with non-null value
        dependency = "dependency";
        TestClass testClass = Mockito.mock(TestClass.class);
        filter.ifShouldInclude(TestValue.B, dependency, testClass::handleObject);
        Mockito.verify(testClass).handleObject(dependency);
    }

    @Test
    void ifShouldExcludeNullable() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        String dependency = null;
        filter.ifShouldExclude(TestValue.A, dependency, object -> fail("Although the value should be excluded, it was null and should be filtered."));

        // Now test with non-null value
        dependency = "dependency";
        TestClass testClass = Mockito.mock(TestClass.class);
        filter.ifShouldExclude(TestValue.A, dependency, testClass::handleObject);
        Mockito.verify(testClass).handleObject(dependency);
    }

    @Test
    void ifShouldIncludeOptional() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        Optional<String> dependency = Optional.empty();
        filter.ifShouldInclude(
            TestValue.B,
            dependency,
            (Consumer<String>) object -> fail("Although the value should be included, it was an empty Optional and should be filtered.")
        );

        // Now test with non-null value
        dependency = Optional.of("dependency");
        TestClass testClass = Mockito.mock(TestClass.class);
        filter.ifShouldInclude(TestValue.B, dependency, (Consumer<String>) testClass::handleObject);
        Mockito.verify(testClass).handleObject(dependency.get());
    }

    @Test
    void ifShouldExcludeOptional() {
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        Optional<String> dependency = Optional.empty();
        filter.ifShouldExclude(
            TestValue.A,
            dependency,
            (Consumer<String>) object -> fail("Although the value should be excluded, it was an empty Optional and should be filtered.")
        );

        // Now test with non-null value
        dependency = Optional.of("dependency");
        TestClass testClass = Mockito.mock(TestClass.class);
        filter.ifShouldExclude(TestValue.A, dependency, (Consumer<String>) testClass::handleObject);
        Mockito.verify(testClass).handleObject(dependency.get());
    }

    @Test
    void shouldIncludeRunnable() {
        TestClass testClass = Mockito.mock(TestClass.class);
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        filter.ifShouldInclude(TestValue.B, testClass::handleRunnable);
        Mockito.verify(testClass).handleRunnable();

        filter.ifShouldInclude(TestValue.A, testClass::handleRunnable);
        Mockito.verifyNoMoreInteractions(testClass);
    }

    @Test
    void shouldExcludeRunnable() {
        TestClass testClass = Mockito.mock(TestClass.class);
        EnumListFilter<TestValue> filter = EnumListFilter.fromExcluded(TestValue.A);
        filter.ifShouldExclude(TestValue.A, testClass::handleRunnable);
        Mockito.verify(testClass).handleRunnable();

        filter.ifShouldExclude(TestValue.B, testClass::handleRunnable);
        Mockito.verifyNoMoreInteractions(testClass);
    }
}