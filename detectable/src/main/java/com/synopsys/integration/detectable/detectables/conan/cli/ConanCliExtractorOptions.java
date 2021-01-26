/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.Optional;

public class ConanCliExtractorOptions {
    private final String lockfilePath;
    private final String additionalArguments;
    private final boolean includeDevDependencies;
    private final boolean preferLongFormExternalIds;

    public ConanCliExtractorOptions(String lockfilePath, String additionalArguments, boolean includeDevDependencies,
        boolean preferLongFormExternalIds) {
        this.lockfilePath = lockfilePath;
        this.additionalArguments = additionalArguments;
        this.includeDevDependencies = includeDevDependencies;
        this.preferLongFormExternalIds = preferLongFormExternalIds;
    }

    public Optional<String> getLockfilePath() {
        return Optional.ofNullable(lockfilePath);
    }

    public Optional<String> getAdditionalArguments() {
        return Optional.ofNullable(additionalArguments);
    }

    public boolean shouldIncludeDevDependencies() {
        return includeDevDependencies;
    }

    public boolean preferLongFormExternalIds() {
        return preferLongFormExternalIds;
    }
}
