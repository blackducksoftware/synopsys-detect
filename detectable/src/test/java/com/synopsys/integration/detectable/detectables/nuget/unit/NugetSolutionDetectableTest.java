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
package com.synopsys.integration.detectable.detectables.nuget.unit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.nuget.NugetInspectorExtractor;
import com.synopsys.integration.detectable.detectables.nuget.NugetSolutionDetectable;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class NugetSolutionDetectableTest {
    @Test
    public void testApplicableForSolution() {
        final NugetInspectorResolver nugetInspectorManager = null;
        final NugetInspectorExtractor nugetInspectorExtractor = null;
        final NugetInspectorOptions nugetInspectorOptions = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("test.sln");

        final NugetSolutionDetectable detectable = new NugetSolutionDetectable(environment, fileFinder, nugetInspectorManager, nugetInspectorExtractor, nugetInspectorOptions);

        assertTrue(detectable.applicable().getPassed());
    }

    @Test
    public void notApplicableForPodfile() {
        final NugetInspectorResolver nugetInspectorManager = null;
        final NugetInspectorExtractor nugetInspectorExtractor = null;
        final NugetInspectorOptions nugetInspectorOptions = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("podfile.lock");

        final NugetSolutionDetectable detectable = new NugetSolutionDetectable(environment, fileFinder, nugetInspectorManager, nugetInspectorExtractor, nugetInspectorOptions);

        assertFalse(detectable.applicable().getPassed());
    }
}

