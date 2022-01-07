package com.synopsys.integration.detectable.detectable.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.times;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

class DependencyTypeFilterTest {

    @ParameterizedTest
    @EnumSource(MockType.class)
    void shouldReportAllTypes(MockType mockType) {
        DependencyTypeFilter<MockType> filter = new DependencyTypeFilter<>(MockType.values());
        assertTrue(filter.shouldReportDependencyType(mockType), "All types should be included.");
    }

    @Test
    void shouldNotReportBuild() {
        DependencyTypeFilter<MockType> filter = new DependencyTypeFilter<>(MockType.APP, MockType.OPTIONAL);
        assertFalse(filter.shouldReportDependencyType(MockType.BUILD), String.format("%s should not be included.", MockType.BUILD));
        assertTrue(filter.shouldReportDependencyType(MockType.APP));
        assertTrue(filter.shouldReportDependencyType(MockType.OPTIONAL));

        List<String> buildDependencies = Collections.singletonList("build-dep");
        filter.ifReportingType(MockType.BUILD, buildDependencies, deps -> fail(String.format("%s dependencies should not be reported on.", MockType.BUILD)));
    }

    @Test
    void nullDependencies() {
        DependencyTypeFilter<MockType> filter = new DependencyTypeFilter<>(MockType.APP, MockType.OPTIONAL);
        filter.ifReportingType(MockType.APP, null, deps -> fail("If dependencies are null, they should not be reported."));
    }

    @Test
    void ifReportingType() {
        List<String> appDependencies = Collections.singletonList("app-dep");
        class TestReporter {
            void report(List<String> dependencies) {}
        }
        TestReporter testReporter = Mockito.mock(TestReporter.class);
        DependencyTypeFilter<MockType> filter = new DependencyTypeFilter<>(MockType.APP);

        filter.ifReportingType(MockType.APP, appDependencies, testReporter::report);
        Mockito.verify(testReporter, times(1)).report(appDependencies);
    }

    private enum MockType {
        APP,
        BUILD,
        OPTIONAL
    }
}