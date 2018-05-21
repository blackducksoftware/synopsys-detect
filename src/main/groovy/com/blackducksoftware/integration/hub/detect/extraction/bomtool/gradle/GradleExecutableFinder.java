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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class GradleExecutableFinder {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String systemGradle = null;
    private boolean hasLookedForSystemGradle = false;

    public String findGradle(final StrategyEnvironment environment) {
        String resolvedGradle = null;
        final String gradlePath = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLEW, false, environment.getDirectory(), detectConfiguration.getGradlePath());
        if (StringUtils.isNotBlank(gradlePath)) {
            resolvedGradle = gradlePath;
        }else {
            if (!hasLookedForSystemGradle) {
                systemGradle = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLE, true, environment.getDirectory(), detectConfiguration.getGradlePath());
                hasLookedForSystemGradle = true;
            }
            resolvedGradle = systemGradle;
        }
        return resolvedGradle;
    }
}
