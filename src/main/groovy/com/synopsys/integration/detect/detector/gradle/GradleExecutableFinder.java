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
package com.synopsys.integration.detect.detector.gradle;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.type.ExecutableType;
import com.synopsys.integration.detect.util.executable.ExecutableFinder;

public class GradleExecutableFinder {
    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;

    private String systemGradle = null;
    private boolean hasLookedForSystemGradle = false;

    public GradleExecutableFinder(final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        this.executableFinder = executableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public String findGradle(final DetectorEnvironment environment) {
        String resolvedGradle = null;
        final String userProvidedGradlePath = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_PATH, PropertyAuthority.None);
        final String gradlePath = executableFinder.getExecutablePathOrOverride(ExecutableType.GRADLEW, false, environment.getDirectory(), userProvidedGradlePath);
        if (StringUtils.isNotBlank(gradlePath)) {
            resolvedGradle = gradlePath;
        } else {
            if (!hasLookedForSystemGradle) {
                systemGradle = executableFinder.getExecutablePathOrOverride(ExecutableType.GRADLE, true, environment.getDirectory(), userProvidedGradlePath);
                hasLookedForSystemGradle = true;
            }
            resolvedGradle = systemGradle;
        }
        return resolvedGradle;
    }
}
