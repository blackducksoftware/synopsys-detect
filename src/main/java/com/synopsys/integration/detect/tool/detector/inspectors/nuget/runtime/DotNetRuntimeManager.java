/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class DotNetRuntimeManager {
    private final DotNetRuntimeFinder runtimeFinder;
    private final DotNetRuntimeParser runtimeParser;

    public DotNetRuntimeManager(final DotNetRuntimeFinder runtimeFinder, final DotNetRuntimeParser runtimeParser) {
        this.runtimeFinder = runtimeFinder;
        this.runtimeParser = runtimeParser;
    }

    public boolean isRuntimeAvailable(final Integer... versionTokens) throws DetectableException {
        final String versionSearchString = StringUtils.join(versionTokens, ".");
        return isRuntimeAvailable(versionSearchString);
    }

    public boolean isRuntimeAvailable(final String version) throws DetectableException {
        final List<String> availableRuntimes = runtimeFinder.listAvailableRuntimes();
        return runtimeParser.doRuntimesContainVersionStartingWith(availableRuntimes, version);
    }
}
