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
package com.synopsys.integration.detectable.detectables.pip;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.executable.LocalOrSytemExecutableFinder;

public class PythonExecutableFinder {
    private final LocalOrSytemExecutableFinder localOrSytemExecutableFinder;
    private final DetectConfiguration detectConfiguration;

    public PythonExecutableFinder(final LocalOrSytemExecutableFinder localOrSytemExecutableFinder, final DetectConfiguration detectConfiguration) {
        this.localOrSytemExecutableFinder = localOrSytemExecutableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public String findPip(final DetectableEnvironment environment) throws DetectableException {
        return findExecutable(environment, ExecutableType.PIP, ExecutableType.PIP3);
    }

    public String findPipenv(final DetectableEnvironment environment, final String pipEnvPath) throws DetectableException {
        return findExecutable(environment, ExecutableType.PIPENV, pipEnvPath);
    }

    public String findPython(final DetectableEnvironment environment) throws DetectableException {
        return findExecutable(environment, ExecutableType.PYTHON, ExecutableType.PYTHON3, detectConfiguration.getProperty(DetectProperty.DETECT_PYTHON_PATH, PropertyAuthority.None));
    }

    private String findExecutable(final DetectableEnvironment environment, final ExecutableType pythonAnyVersionExecutableType, final String overridePath) throws DetectableException {
        return findExecutable(environment, pythonAnyVersionExecutableType, pythonAnyVersionExecutableType, overridePath);
    }

    private String findExecutable(final DetectableEnvironment environment, final ExecutableType python2ExecutableType, final ExecutableType python3ExecutableType) throws DetectableException {
        return findExecutable(environment, python2ExecutableType, python3ExecutableType, null);
    }

    private String findExecutable(final DetectableEnvironment environment, final ExecutableType python2ExecutableType, final ExecutableType python3ExecutableType, final String overridePath) throws DetectableException {
        try {
            final ExecutableType executableType;
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PYTHON_PYTHON3, PropertyAuthority.None)) {
                executableType = python3ExecutableType;
            } else {
                executableType = python2ExecutableType;
            }

            return localOrSytemExecutableFinder.getExecutablePathOrOverride(executableType, true, environment.getDirectory(), overridePath);
        } catch (final Exception e) {
            throw new DetectableException(e);
        }
    }

}
