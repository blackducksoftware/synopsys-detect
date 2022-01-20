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
    void ifShouldInclude() {
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
        assertFalse(filter.shouldInclude(TestValue.A));
        assertTrue(filter.shouldInclude(TestValue.B));
    }

    @Test
    void ifShouldExclude() {
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
        assertTrue(filter.shouldExclude(TestValue.A));
        assertFalse(filter.shouldExclude(TestValue.B));
    }

    @Test
    void ifShouldIncludeNullable() {
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
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
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
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
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
        Optional<String> dependency = Optional.empty();
        filter.ifShouldInclude(TestValue.B, dependency, (Consumer<String>) object -> fail("Although the value should be included, it was an empty Optional and should be filtered."));

        // Now test with non-null value
        dependency = Optional.of("dependency");
        TestClass testClass = Mockito.mock(TestClass.class);
        filter.ifShouldInclude(TestValue.B, dependency, (Consumer<String>) testClass::handleObject);
        Mockito.verify(testClass).handleObject(dependency.get());
    }

    @Test
    void ifShouldExcludeOptional() {
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
        Optional<String> dependency = Optional.empty();
        filter.ifShouldExclude(TestValue.A, dependency, (Consumer<String>) object -> fail("Although the value should be excluded, it was an empty Optional and should be filtered."));

        // Now test with non-null value
        dependency = Optional.of("dependency");
        TestClass testClass = Mockito.mock(TestClass.class);
        filter.ifShouldExclude(TestValue.A, dependency, (Consumer<String>) testClass::handleObject);
        Mockito.verify(testClass).handleObject(dependency.get());
    }

    @Test
    void shouldIncludeRunnable() {
        TestClass testClass = Mockito.mock(TestClass.class);
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
        filter.ifShouldInclude(TestValue.B, testClass::handleRunnable);
        Mockito.verify(testClass).handleRunnable();

        filter.ifShouldInclude(TestValue.A, testClass::handleRunnable);
        Mockito.verifyNoMoreInteractions(testClass);
    }

    @Test
    void shouldExcludeRunnable() {
        TestClass testClass = Mockito.mock(TestClass.class);
        EnumListFilter<TestValue> filter = new EnumListFilter<>(TestValue.A);
        filter.ifShouldExclude(TestValue.A, testClass::handleRunnable);
        Mockito.verify(testClass).handleRunnable();

        filter.ifShouldExclude(TestValue.B, testClass::handleRunnable);
        Mockito.verifyNoMoreInteractions(testClass);
    }
}