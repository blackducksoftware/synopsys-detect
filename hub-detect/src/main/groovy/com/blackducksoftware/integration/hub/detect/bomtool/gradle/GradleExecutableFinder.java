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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class GradleExecutableFinder {
    private final DetectFileManager detectFileManager;
    private final ExecutableManager executableManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfigWrapper detectConfigWrapper;

    private String systemGradle = null;
    private boolean hasLookedForSystemGradle = false;

    @Autowired
    public GradleExecutableFinder(final DetectFileManager detectFileManager, final ExecutableManager executableManager, final ExecutableRunner executableRunner, final DetectConfigWrapper detectConfigWrapper) {
        this.detectFileManager = detectFileManager;
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public String findGradle(final BomToolEnvironment environment) {
        String resolvedGradle = null;
        String userProvidedGradlePath = detectConfigWrapper.getProperty(DetectProperty.DETECT_GRADLE_PATH);
        final String gradlePath = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLEW, false, environment.getDirectory(), userProvidedGradlePath);
        if (StringUtils.isNotBlank(gradlePath)) {
            resolvedGradle = gradlePath;
        } else {
            if (!hasLookedForSystemGradle) {
                systemGradle = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLE, true, environment.getDirectory(), userProvidedGradlePath);
                hasLookedForSystemGradle = true;
            }
            resolvedGradle = systemGradle;
        }
        return resolvedGradle;
    }
}
