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
    private final Boolean forceBdio;

    public BlackDuckConnectionDetails(
        Boolean offline,
        @Nullable String blackDuckUrl,
        Map<String, String> blackduckProperties,
        Integer parallelProcessors,
        ConnectionDetails connectionDetails,
        Boolean forceBdio
    ) {
        this.offline = offline;
        this.blackDuckUrl = blackDuckUrl;
        this.blackduckProperties = blackduckProperties;
        this.parallelProcessors = parallelProcessors;
        this.connectionDetails = connectionDetails;
        this.forceBdio = forceBdio;
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
    
    @NotNull
    public Boolean getForceBdio() {
        return forceBdio;
    }
}