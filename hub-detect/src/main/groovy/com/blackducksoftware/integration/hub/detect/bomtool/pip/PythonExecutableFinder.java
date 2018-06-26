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
package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;

@Component
public class PythonExecutableFinder {
    private final ExecutableManager executableManager;
    private final DetectConfigWrapper detectConfigWrapper;

    @Autowired
    public PythonExecutableFinder(final ExecutableManager executableManager, final DetectConfigWrapper detectConfigWrapper) {
        this.executableManager = executableManager;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public String findPip(final BomToolEnvironment environment) throws BomToolException {
        return findExecutable(environment, ExecutableType.PIP, ExecutableType.PIP3);
    }

    public String findPipenv(final BomToolEnvironment environment) throws BomToolException {
        return findExecutable(environment, ExecutableType.PIPENV, detectConfigWrapper.getProperty(DetectProperty.DETECT_PIPENV_PATH));
    }

    public String findPython(final BomToolEnvironment environment) throws BomToolException {
        return findExecutable(environment, ExecutableType.PYTHON, ExecutableType.PYTHON3, detectConfigWrapper.getProperty(DetectProperty.DETECT_PYTHON_PATH));
    }

    private String findExecutable(final BomToolEnvironment environment, final ExecutableType pythonAnyVersionExecutableType, final String overridePath) throws BomToolException {
        return findExecutable(environment, pythonAnyVersionExecutableType, pythonAnyVersionExecutableType, overridePath);
    }

    private String findExecutable(final BomToolEnvironment environment, final ExecutableType python2ExecutableType, final ExecutableType python3ExecutableType) throws BomToolException {
        return findExecutable(environment, python2ExecutableType, python3ExecutableType, null);
    }

    private String findExecutable(final BomToolEnvironment environment, final ExecutableType python2ExecutableType, final ExecutableType python3ExecutableType, final String overridePath) throws BomToolException {
        try {
            final ExecutableType executableType;
            if (detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_PYTHON_PYTHON3)) {
                executableType = python3ExecutableType;
            } else {
                executableType = python2ExecutableType;
            }

            return executableManager.getExecutablePathOrOverride(executableType, true, environment.getDirectory(), overridePath);
        } catch (final Exception e) {
            throw new BomToolException(e);
        }
    }

}
