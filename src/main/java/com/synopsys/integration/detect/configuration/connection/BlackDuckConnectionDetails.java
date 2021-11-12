/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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

    public BlackDuckConnectionDetails(Boolean offline, @Nullable String blackDuckUrl, Map<String, String> blackduckProperties, Integer parallelProcessors,
        ConnectionDetails connectionDetails) {
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