/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.detector.pip;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.type.ExecutableType;
import com.synopsys.integration.detect.util.executable.ExecutableFinder;

public class PythonExecutableFinder {
    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;

    public PythonExecutableFinder(final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        this.executableFinder = executableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public String findPip(final DetectorEnvironment environment) throws DetectorException {
        return findExecutable(environment, ExecutableType.PIP, ExecutableType.PIP3);
    }

    public String findPipenv(final DetectorEnvironment environment) throws DetectorException {
        return findExecutable(environment, ExecutableType.PIPENV, detectConfiguration.getProperty(DetectProperty.DETECT_PIPENV_PATH, PropertyAuthority.None));
    }

    public String findPython(final DetectorEnvironment environment) throws DetectorException {
        return findExecutable(environment, ExecutableType.PYTHON, ExecutableType.PYTHON3, detectConfiguration.getProperty(DetectProperty.DETECT_PYTHON_PATH, PropertyAuthority.None));
    }

    private String findExecutable(final DetectorEnvironment environment, final ExecutableType pythonAnyVersionExecutableType, final String overridePath) throws DetectorException {
        return findExecutable(environment, pythonAnyVersionExecutableType, pythonAnyVersionExecutableType, overridePath);
    }

    private String findExecutable(final DetectorEnvironment environment, final ExecutableType python2ExecutableType, final ExecutableType python3ExecutableType) throws DetectorException {
        return findExecutable(environment, python2ExecutableType, python3ExecutableType, null);
    }

    private String findExecutable(final DetectorEnvironment environment, final ExecutableType python2ExecutableType, final ExecutableType python3ExecutableType, final String overridePath) throws DetectorException {
        try {
            final ExecutableType executableType;
            if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PYTHON_PYTHON3, PropertyAuthority.None)) {
                executableType = python3ExecutableType;
            } else {
                executableType = python2ExecutableType;
            }

            return executableFinder.getExecutablePathOrOverride(executableType, true, environment.getDirectory(), overridePath);
        } catch (final Exception e) {
            throw new DetectorException(e);
        }
    }

}
