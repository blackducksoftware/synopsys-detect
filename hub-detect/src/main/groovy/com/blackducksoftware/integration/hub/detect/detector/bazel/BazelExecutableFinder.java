/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.detector.bazel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;

public class BazelExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;

    private String systemBazel = null;
    private boolean hasLookedForSystemBazel = false;

    public BazelExecutableFinder(final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        this.executableFinder = executableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public String findBazel(final DetectorEnvironment environment) {
        String resolvedBazel = null;
        if (!hasLookedForSystemBazel) {
            final String providedBazelPath = detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_PATH, PropertyAuthority.None);
            resolvedBazel = executableFinder.getExecutablePathOrOverride(ExecutableType.BAZEL, true, environment.getDirectory(), providedBazelPath);
                hasLookedForSystemBazel = true;
        }
        return resolvedBazel;
    }
}
