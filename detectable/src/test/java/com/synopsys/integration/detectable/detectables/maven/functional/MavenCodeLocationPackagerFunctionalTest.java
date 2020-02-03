/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.maven.functional;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenCodeLocationPackager;
import com.synopsys.integration.detectable.detectables.maven.cli.MavenParseResult;
import com.synopsys.integration.detectable.detectables.maven.cli.ScopedDependency;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class MavenCodeLocationPackagerFunctionalTest {
    @Test
    public void extractCodeLocationsTestWithNumbersRemovedOutput() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutputWithStrangePrefixesFoundFromCustomer.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityCodeLocation.json", 5, "", "");
    }

    @Test
    public void extractCodeLocationsTest() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/sonarStashOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/sonarStashCodeLocation.json");
    }

    @Test
    public void extractCodeLocationsTestTeamCity() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityCodeLocation.json", 5, "", "");
    }

    @Test
    public void extractCodeLocationsTestTeamCityWithUnpackDependencies() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutputWithDependencyUnpack.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityCodeLocation.json", 5, "", "");
    }

    @Test
    public void extractCodeLocationsTestTeamCityIncludedModules() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityIncludedCodeLocation.json", 1, "", "hub-teamcity-agent");
    }

    @Test
    public void extractCodeLocationsTestTeamCityExcludedModules() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/hubTeamcityOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/hubTeamCityExcludedCodeLocation.json", 1, "hub-teamcity-common,hub-teamcity-agent,hub-teamcity-assembly,hub-teamcity", "");
    }

    @Test
    public void extractCodeLocationsCorruptTest() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/sonarStashCorruptOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/sonarStashCorruptCodeLocation.json");
    }

    @Test
    public void extractCodeLocationsTestWebgoat() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/webgoat-container-pom-dependency-tree-output.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/webgoatCodeLocation.json", 1, "", "");
    }

    @Test
    public void extractCodeLocationsTestNoScope() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/compileScopeUnderTestScope.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/compileScopeUnderTestScopeNoScope.json", 3, "", "", 2, null, null);
    }

    @Test
    public void extractCodeLocationsTestCompileScope() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/compileScopeUnderTestScope.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/compileScopeUnderTestScopeCompileScope.json", 3, "", "", 2, null, "compile");
    }

    @Test
    public void extractCodeLocationsTestCompileScope2() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/compileScopeUnderTestScope.txt");
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());
        final List<MavenParseResult> result = mavenCodeLocationPackager.extractCodeLocations("/test/path", mavenOutputText, "test", null, null, null);
        assertEquals(3, result.size());

        for (final MavenParseResult mavenParseResult : result) {
            final DependencyGraph dependencyGraph = mavenParseResult.getCodeLocation().getDependencyGraph();
            for (final Dependency rootDependency : dependencyGraph.getRootDependencies()) {
                if (rootDependency instanceof ScopedDependency) {
                    walkGraphExcludingScope(dependencyGraph, (ScopedDependency) rootDependency, "test");
                } else {
                    System.out.println(String.format("Dependency is not a scoped dependency. Validation cannot occur. %s", rootDependency.getExternalId().createExternalId()));
                }
            }
        }
    }

    private void walkGraphExcludingScope(final DependencyGraph dependencyGraph, final ScopedDependency scopedDependency, final String scope) {
        Assertions.assertNotEquals(scope, scopedDependency.scope);
        for (final Dependency dependency : dependencyGraph.getChildrenForParent(scopedDependency)) {
            walkGraphExcludingScope(dependencyGraph, (ScopedDependency) dependency, scope);
        }
    }

    @Test
    public void extractCodeLocationsTestComplexTree() {
        final String mavenOutputText = FunctionalTestFiles.asString("/maven/mavenComplexOutput.txt");
        createNewCodeLocationTest(mavenOutputText, "/maven/mavenComplexOutputResult.json", 85, "", "", 84, null, "compile");
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, 1, "", "");
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath, final int numberOfCodeLocations, final String excludedModules, final String includedModules) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, numberOfCodeLocations, excludedModules, includedModules, 0);
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath, final int numberOfCodeLocations, final String excludedModules, final String includedModules, final int codeLocationIndex) {
        createNewCodeLocationTest(mavenOutputText, expectedResourcePath, numberOfCodeLocations, excludedModules, includedModules, codeLocationIndex, null, null);
    }

    private void createNewCodeLocationTest(final String mavenOutputText, final String expectedResourcePath, final int numberOfCodeLocations, final String excludedModules, final String includedModules, final int codeLocationIndex,
        final String excludedScopes, final String includedScopes) {
        final MavenCodeLocationPackager mavenCodeLocationPackager = new MavenCodeLocationPackager(new ExternalIdFactory());
        final List<MavenParseResult> result = mavenCodeLocationPackager.extractCodeLocations("/test/path", mavenOutputText, excludedScopes, includedScopes, excludedModules, includedModules);
        assertEquals(numberOfCodeLocations, result.size());
        final CodeLocation codeLocation = result.get(codeLocationIndex).getCodeLocation();

        GraphCompare.assertEqualsResource(expectedResourcePath, codeLocation.getDependencyGraph());
    }
}
