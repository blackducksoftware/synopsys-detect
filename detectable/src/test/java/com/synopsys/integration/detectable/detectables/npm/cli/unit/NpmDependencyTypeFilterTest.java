/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.cli.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmDependencyTypeFilter;
import com.synopsys.integration.util.Stringable;

class NpmDependencyTypeFilterTest {

    @ParameterizedTest()
    @MethodSource("generateTest")
    void includeDependencies(TestParameter testParameter) {
        Set<String> devDependencies = Collections.singleton("test-dev");
        Set<String> peerDependencies = Collections.singleton("test-peer");

        NpmDependencyTypeFilter npmDependencyTypeFilter = new NpmDependencyTypeFilter(devDependencies, peerDependencies, testParameter.includeDevDependencies, testParameter.includePeerDependencies);
        String dependencyName = "test";
        if (testParameter.isDevDependency) {
            dependencyName = "test-dev";
        } else if (testParameter.isPeerDependency) {
            dependencyName = "test-peer";
        }
        boolean actual = npmDependencyTypeFilter.shouldInclude(dependencyName, testParameter.isRootDependency);
        assertEquals(testParameter.shouldInclude, actual, "TestParameters: " + testParameter);
    }

    static Stream<TestParameter> generateTest() {
        return Stream.<TestParameter>builder()
                   // Dev Dependencies
                   // isDevDependency = true
                   .add(new TestParameter(true, false, true, false, true, true))
                   .add(new TestParameter(true, false, true, false, false, true))
                   .add(new TestParameter(false, false, true, false, false, true))
                   .add(new TestParameter(false, false, true, false, true, false))
                   // isDevDependency = false
                   .add(new TestParameter(true, false, false, false, true, true))
                   .add(new TestParameter(true, false, false, false, false, true))
                   .add(new TestParameter(false, false, false, false, false, true))
                   .add(new TestParameter(false, false, false, false, true, true))

                   // Peer Dependencies
                   // isPeerDependency = true
                   .add(new TestParameter(false, true, false, true, true, true))
                   .add(new TestParameter(false, true, false, true, false, true))
                   .add(new TestParameter(false, false, false, true, true, false))
                   .add(new TestParameter(false, false, false, true, false, true))
                   // isPeerDependency = false
                   .add(new TestParameter(false, true, false, false, true, true))
                   .add(new TestParameter(false, true, false, false, false, true))
                   .add(new TestParameter(false, false, false, false, true, true))
                   .add(new TestParameter(false, false, false, false, false, true))

                   // Together
                   // Include Both Types
                   .add(new TestParameter(true, true, true, false, true, true))
                   .add(new TestParameter(true, true, true, false, false, true))
                   .add(new TestParameter(true, true, false, true, true, true))
                   .add(new TestParameter(true, true, false, true, false, true))
                   // Exclude Both types
                   .add(new TestParameter(false, false, true, false, true, false))
                   .add(new TestParameter(false, false, true, false, false, true))
                   .add(new TestParameter(false, false, false, true, true, false))
                   .add(new TestParameter(false, false, false, true, false, true))

                   .build();
    }

    private static class TestParameter extends Stringable {
        public final boolean includeDevDependencies;
        public final boolean includePeerDependencies;
        public final boolean isDevDependency;
        public final boolean isPeerDependency;
        public final boolean isRootDependency;
        public final boolean shouldInclude;

        public TestParameter(boolean includeDevDependencies, boolean includePeerDependencies, boolean isDevDependency, boolean isPeerDependency, boolean isRootDependency, boolean shouldInclude) {
            this.includeDevDependencies = includeDevDependencies;
            this.includePeerDependencies = includePeerDependencies;
            this.isDevDependency = isDevDependency;
            this.isPeerDependency = isPeerDependency;
            this.isRootDependency = isRootDependency;
            this.shouldInclude = shouldInclude;
        }
    }
}
