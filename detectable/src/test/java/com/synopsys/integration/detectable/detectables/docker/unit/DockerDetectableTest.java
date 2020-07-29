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
package com.synopsys.integration.detectable.detectables.docker.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.executable.resolver.BashResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.DockerResolver;
import com.synopsys.integration.detectable.detectable.executable.resolver.JavaResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.WrongOperatingSystemResult;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectable;
import com.synopsys.integration.detectable.detectables.docker.DockerDetectableOptions;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;

public class DockerDetectableTest {

    @Test
    public void testApplicable() {
        final DetectableEnvironment environment = null;
        final DockerInspectorResolver dockerInspectorResolver = null;
        final JavaResolver javaResolver = null;
        final BashResolver bashResolver = null;
        final DockerResolver dockerResolver = null;
        final DockerExtractor dockerExtractor = null;
        final DockerDetectableOptions dockerDetectableOptions = Mockito.mock(DockerDetectableOptions.class);

        Mockito.when(dockerDetectableOptions.hasDockerImageOrTar()).thenReturn(Boolean.TRUE);

        final DockerDetectable detectable = new DockerDetectable(environment, dockerInspectorResolver, javaResolver, bashResolver, dockerResolver,
            dockerExtractor, dockerDetectableOptions);

        final DetectableResult result = detectable.applicable();

        assertTrue(result.getPassed() || result instanceof WrongOperatingSystemResult);
    }
}
