/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmDependencyTypeFilter;
import com.synopsys.integration.util.Stringable;

class NpmDependencyTypeFilterTest {

    @ParameterizedTest()
    @MethodSource("shouldIncludeSource")
    void shouldIncludeTest(TestParameter testParameter) {
        boolean shouldInclude = testWithParameter(testParameter);
        assertTrue(shouldInclude, "Test Parameters: " + testParameter);
    }

    @ParameterizedTest()
    @MethodSource("shouldNotIncludeSource")
    void shouldNotIncludeTest(TestParameter testParameter) {
        boolean shouldInclude = testWithParameter(testParameter);
        assertFalse(shouldInclude, "Test Parameters: " + testParameter);
    }

    boolean testWithParameter(TestParameter testParameter) {
        Set<String> devDependencies = Collections.singleton("test-dev");
        Set<String> peerDependencies = Collections.singleton("test-peer");

        NpmDependencyTypeFilter npmDependencyTypeFilter = new NpmDependencyTypeFilter(devDependencies, peerDependencies, testParameter.includeDevDependencies, testParameter.includePeerDependencies);
        String dependencyName = "test";
        if (testParameter.isDevDependency) {
            dependencyName = "test-dev";
        } else if (testParameter.isPeerDependency) {
            dependencyName = "test-peer";
        }

        return npmDependencyTypeFilter.shouldInclude(dependencyName, testParameter.isRootDependency);
    }

    static Stream<TestParameter> shouldIncludeSource() {
        return Stream.<TestParameter>builder()
                   // Include Dev Dependencies
                   .add(TestParameter.neither(true, false, true))
                   .add(TestParameter.neither(true, false, false))
                   .add(TestParameter.dev(true, false, true))
                   .add(TestParameter.dev(true, false, false))
                   .add(TestParameter.peer(true, false, false))

                   // Include Peer Dependencies
                   .add(TestParameter.neither(false, true, true))
                   .add(TestParameter.neither(false, true, false))
                   .add(TestParameter.dev(false, true, true))
                   .add(TestParameter.dev(false, true, false))
                   .add(TestParameter.peer(false, true, true))
                   .add(TestParameter.peer(false, true, false))

                   // Exclude both dependency types
                   .add(TestParameter.neither(false, false, false))
                   .add(TestParameter.neither(false, false, true))
                   .add(TestParameter.dev(false, false, false))
                   .add(TestParameter.peer(false, false, false))

                   // Include both dependency types
                   .add(TestParameter.dev(true, true, true))
                   .add(TestParameter.dev(true, true, false))
                   .add(TestParameter.peer(true, true, true))
                   .add(TestParameter.peer(true, true, false))
                   .add(TestParameter.neither(true, true, true))
                   .add(TestParameter.neither(true, true, false))

                   .build();
    }

    static Stream<TestParameter> shouldNotIncludeSource() {
        return Stream.<TestParameter>builder()
                   // Dev Dependencies
                   .add(TestParameter.dev(false, false, true))
                   .add(TestParameter.dev(false, true, true))

                   // Peer Dependencies
                   .add(TestParameter.peer(false, false, true))
                   .add(TestParameter.peer(true, false, true))

                   .build();
    }

    private static class TestParameter extends Stringable {
        public final boolean includeDevDependencies;
        public final boolean includePeerDependencies;
        public final boolean isDevDependency;
        public final boolean isPeerDependency;
        public final boolean isRootDependency;

        public static TestParameter neither(boolean includeDevDependencies, boolean includePeerDependencies, boolean isRootDependency) {
            return new TestParameter(includeDevDependencies, includePeerDependencies, false, false, isRootDependency);
        }

        public static TestParameter dev(boolean includeDevDependencies, boolean includePeerDependencies, boolean isRootDependency) {
            return new TestParameter(includeDevDependencies, includePeerDependencies, true, false, isRootDependency);
        }

        public static TestParameter peer(boolean includeDevDependencies, boolean includePeerDependencies, boolean isRootDependency) {
            return new TestParameter(includeDevDependencies, includePeerDependencies, false, true, isRootDependency);
        }

        private TestParameter(boolean includeDevDependencies, boolean includePeerDependencies, boolean isDevDependency, boolean isPeerDependency, boolean isRootDependency) {
            this.includeDevDependencies = includeDevDependencies;
            this.includePeerDependencies = includePeerDependencies;
            this.isDevDependency = isDevDependency;
            this.isPeerDependency = isPeerDependency;
            this.isRootDependency = isRootDependency;
        }
    }
}
