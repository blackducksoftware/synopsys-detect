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
package com.synopsys.integration.detectable.detectables.pip.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipInspectorExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class PipInspectorDetectableTest {
    @Test
    public void testApplicableSetUpToolsFile() {

        final PythonResolver pythonResolver = null;
        final PipResolver pipResolver = null;
        final PipInspectorResolver pipInspectorResolver = null;
        final PipInspectorExtractor pipInspectorExtractor = null;
        final PipInspectorDetectableOptions pipInspectorDetectableOptions = Mockito.mock(PipInspectorDetectableOptions.class);

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed("setup.py");

        final PipInspectorDetectable detectable = new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor, pipInspectorDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }

    @Test
    public void testApplicableRequirementsFilePath() {

        final PythonResolver pythonResolver = null;
        final PipResolver pipResolver = null;
        final PipInspectorResolver pipInspectorResolver = null;
        final PipInspectorExtractor pipInspectorExtractor = null;
        final PipInspectorDetectableOptions pipInspectorDetectableOptions = Mockito.mock(PipInspectorDetectableOptions.class);

        final DetectableEnvironment environment = Mockito.mock(DetectableEnvironment.class);
        final FileFinder fileFinder = Mockito.mock(FileFinder.class);

        final File dir = new File(".");
        Mockito.when(environment.getDirectory()).thenReturn(dir);
        Mockito.when(pipInspectorDetectableOptions.getRequirementsFilePaths()).thenReturn(Collections.singletonList(Paths.get("testReqtsPath")));

        final PipInspectorDetectable detectable = new PipInspectorDetectable(environment, fileFinder, pythonResolver, pipResolver, pipInspectorResolver, pipInspectorExtractor, pipInspectorDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
