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

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.PipenvResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.PythonResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectable;
import com.synopsys.integration.detectable.detectables.pip.PipenvDetectableOptions;
import com.synopsys.integration.detectable.detectables.pip.PipenvExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class PipenvDetectableTest {
    private static final String PIPFILE_FILE_NAME = "Pipfile";
    private static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    @Test
    public void testApplicablePipfile() {
        final PipenvDetectable detectable = constructDetectable(PIPFILE_FILE_NAME);
        assertTrue(detectable.applicable().getPassed());
    }

    @Test
    public void testApplicablePipfileDotLock() {
        final PipenvDetectable detectable = constructDetectable(PIPFILE_DOT_LOCK_FILE_NAME);
        assertTrue(detectable.applicable().getPassed());
    }

    private PipenvDetectable constructDetectable(final String targetFilename) {
        final PipenvDetectableOptions pipenvDetectableOptions = null;
        final PythonResolver pythonResolver = null;
        final PipenvResolver pipenvResolver = null;
        final PipenvExtractor pipenvExtractor = null;

        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final FileFinder fileFinder = MockFileFinder.withFileNamed(targetFilename);

        return new PipenvDetectable(environment, pipenvDetectableOptions, fileFinder, pythonResolver, pipenvResolver, pipenvExtractor);
    }
}
