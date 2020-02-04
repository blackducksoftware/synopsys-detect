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
package com.synopsys.integration.detectable.detectables.bitbake.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;
import com.synopsys.integration.detectable.util.MockFileFinder;

public class BitbakeDetectorTest {
    private static final String BUILD_ENV_NAME = "testBuildEnv";

    @Test
    public void testApplicable() {
        final DetectableEnvironment environment = MockDetectableEnvironment.empty();
        final BitbakeExtractor bitbakeExtractor = null;
        final BashResolver bashResolver = null;

        final BitbakeDetectableOptions bitbakeDetectableOptions = new BitbakeDetectableOptions(BUILD_ENV_NAME, new ArrayList<>(), Collections.singletonList("testPkgName"), 1);
        final FileFinder fileFinder = MockFileFinder.withFileNamed(BUILD_ENV_NAME);

        final BitbakeDetectable detectable = new BitbakeDetectable(environment, fileFinder, bitbakeDetectableOptions, bitbakeExtractor, bashResolver);

        assertTrue(detectable.applicable().getPassed());
    }
}
