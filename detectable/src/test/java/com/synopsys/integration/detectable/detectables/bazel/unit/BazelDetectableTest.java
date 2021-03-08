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
package com.synopsys.integration.detectable.detectables.bazel.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BazelResolver;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectable;
import com.synopsys.integration.detectable.detectables.bazel.BazelDetectableOptions;
import com.synopsys.integration.detectable.detectables.bazel.BazelExtractor;
import com.synopsys.integration.detectable.util.MockDetectableEnvironment;

public class BazelDetectableTest {

    @Test
    public void testApplicable() {
        DetectableEnvironment environment = MockDetectableEnvironment.empty();
        FileFinder fileFinder = Mockito.mock(FileFinder.class);
        Mockito.when(fileFinder.findFile(new File("."), "WORKSPACE")).thenReturn(new File("src/test/resources/functional/bazel/WORKSPACE"));
        BazelExtractor bazelExtractor = null;
        BazelResolver bazelResolver = null;
        BazelDetectableOptions bazelDetectableOptions = new BazelDetectableOptions("target", null, null);
        BazelDetectable detectable = new BazelDetectable(environment, fileFinder, bazelExtractor, bazelResolver, bazelDetectableOptions);

        assertTrue(detectable.applicable().getPassed());
    }
}
