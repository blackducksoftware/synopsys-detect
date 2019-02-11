/**
 * hub-detect
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
package com.synopsys.integration.detectable.detectables.maven;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableFinder;

public class MavenExecutableFinder {
    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;

    private String systemMaven = null;
    private boolean hasLookedForSystemMaven = false;

    public MavenExecutableFinder(final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        this.executableFinder = executableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public String findMaven(final DetectorEnvironment environment) {
        String resolvedMaven = null;
        final String providedMavenPath = detectConfiguration.getProperty(DetectProperty.DETECT_MAVEN_PATH, PropertyAuthority.None);
        final String mavenPath = executableFinder.getExecutablePathOrOverride(ExecutableType.MVNW, false, environment.getDirectory(), providedMavenPath);
        if (StringUtils.isNotBlank(mavenPath)) {
            resolvedMaven = mavenPath;
        } else {
            if (!hasLookedForSystemMaven) {
                systemMaven = executableFinder.getExecutablePathOrOverride(ExecutableType.MVN, true, environment.getDirectory(), providedMavenPath);
                hasLookedForSystemMaven = true;
            }
            resolvedMaven = systemMaven;
        }
        return resolvedMaven;
    }
}
