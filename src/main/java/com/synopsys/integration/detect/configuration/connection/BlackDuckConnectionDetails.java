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
package com.synopsys.integration.detect.configuration.connection;

import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlackDuckConnectionDetails {
    private final Boolean offline;
    @Nullable
    private final String blackDuckUrl;
    private final Map<String, String> blackduckProperties;
    private final Integer parallelProcessors;
    private final ConnectionDetails connectionDetails;

    public BlackDuckConnectionDetails(final Boolean offline, @Nullable final String blackDuckUrl, final Map<String, String> blackduckProperties, final Integer parallelProcessors,
        final ConnectionDetails connectionDetails) {
        this.offline = offline;
        this.blackDuckUrl = blackDuckUrl;
        this.blackduckProperties = blackduckProperties;
        this.parallelProcessors = parallelProcessors;
        this.connectionDetails = connectionDetails;
    }

    @NotNull
    public Boolean getOffline() {
        return offline;
    }

    @NotNull
    public Optional<String> getBlackDuckUrl() {
        return Optional.ofNullable(blackDuckUrl);
    }

    @NotNull
    public Map<String, String> getBlackduckProperties() {
        return blackduckProperties;
    }

    @NotNull
    public Integer getParallelProcessors() {
        return parallelProcessors;
    }

    @NotNull
    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }
}