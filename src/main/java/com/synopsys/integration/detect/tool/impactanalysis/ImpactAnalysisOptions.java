/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.impactanalysis;

import java.nio.file.Path;

import org.jetbrains.annotations.Nullable;

public class ImpactAnalysisOptions {
    private final Boolean enabled;
    @Nullable
    private final String codeLocationPrefix;
    @Nullable
    private final String codeLocationSuffix;
    @Nullable
    private final Path outputDirectory;

    public ImpactAnalysisOptions(Boolean enabled, @Nullable String codeLocationPrefix, @Nullable String codeLocationSuffix, @Nullable Path outputDirectory) {
        this.enabled = enabled;
        this.codeLocationPrefix = codeLocationPrefix;
        this.codeLocationSuffix = codeLocationSuffix;
        this.outputDirectory = outputDirectory;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    @Nullable
    public String getCodeLocationPrefix() {
        return codeLocationPrefix;
    }

    @Nullable
    public String getCodeLocationSuffix() {
        return codeLocationSuffix;
    }

    @Nullable
    public Path getOutputDirectory() {
        return outputDirectory;
    }
}
