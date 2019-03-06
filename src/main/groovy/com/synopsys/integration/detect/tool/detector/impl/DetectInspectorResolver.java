/**
 * synopsys-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detect.tool.detector.impl;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorInfo;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;

// TODO: Implement this class
public class DetectInspectorResolver implements DockerInspectorResolver, NugetInspectorResolver, PipInspectorResolver, GradleInspectorResolver {
    @Override
    public File resolvePipInspector() {
        return null;
    }

    @Override
    public NugetInspector resolveNugetInspector() {
        return null;
    }

    @Override
    public File resolveGradleInspector() {
        return null;
    }

    @Override
    public DockerInspectorInfo resolveDockerInspector() throws DetectableException {
        return null;
    }
}
