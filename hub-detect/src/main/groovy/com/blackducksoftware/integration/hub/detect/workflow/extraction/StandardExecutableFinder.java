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
package com.blackducksoftware.integration.hub.detect.workflow.extraction;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;

public class StandardExecutableFinder {
    public enum StandardExecutableType {
        CONDA,
        CPAN,
        CPANM,
        DOCKER,
        BASH,
        GO,
        REBAR3,
        PEAR,
        YARN,
        JAVA
    }

    private final ExecutableManager executableManager;
    private final DetectConfiguration detectConfiguration;

    private final Map<StandardExecutableType, File> alreadyFound = new HashMap<>();

    public StandardExecutableFinder(final ExecutableManager executableManager, final DetectConfiguration detectConfiguration) {
        this.executableManager = executableManager;
        this.detectConfiguration = detectConfiguration;
    }

    public File getExecutable(final StandardExecutableType executableType) throws BomToolException {
        if (alreadyFound.containsKey(executableType)) {
            return alreadyFound.get(executableType);
        }
        final StandardExecutableInfo info = createInfo(executableType);
        if (info == null) {
            throw new BomToolException("Unknown executable type: " + executableType.toString());
        }

        final String exe = executableManager.getExecutablePathOrOverride(info.detectExecutableType, true, new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH)), info.override);
        File exeFile = null;
        if (exe != null) {
            exeFile = new File(exe);
        }
        alreadyFound.put(executableType, exeFile);
        return exeFile;
    }

    public StandardExecutableInfo createInfo(final StandardExecutableType type) {
        switch (type) {
        case CONDA:
            return new StandardExecutableInfo(ExecutableType.CONDA, detectConfiguration.getProperty(DetectProperty.DETECT_CONDA_PATH));
        case CPAN:
            return new StandardExecutableInfo(ExecutableType.CPAN, detectConfiguration.getProperty(DetectProperty.DETECT_CPAN_PATH));
        case CPANM:
            return new StandardExecutableInfo(ExecutableType.CPANM, detectConfiguration.getProperty(DetectProperty.DETECT_CPANM_PATH));
        case DOCKER:
            return new StandardExecutableInfo(ExecutableType.DOCKER, detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_PATH));
        case BASH:
            return new StandardExecutableInfo(ExecutableType.BASH, detectConfiguration.getProperty(DetectProperty.DETECT_BASH_PATH));
        case GO:
            return new StandardExecutableInfo(ExecutableType.GO, null);
        case REBAR3:
            return new StandardExecutableInfo(ExecutableType.REBAR3, detectConfiguration.getProperty(DetectProperty.DETECT_HEX_REBAR3_PATH));
        case PEAR:
            return new StandardExecutableInfo(ExecutableType.PEAR, detectConfiguration.getProperty(DetectProperty.DETECT_PEAR_PATH));
        case YARN:
            return new StandardExecutableInfo(ExecutableType.YARN, detectConfiguration.getProperty(DetectProperty.DETECT_YARN_PATH));
        case JAVA:
            return new StandardExecutableInfo(ExecutableType.JAVA, detectConfiguration.getProperty(DetectProperty.DETECT_JAVA_PATH));
        }
        return null;
    }

    private class StandardExecutableInfo {
        public ExecutableType detectExecutableType;
        public String override;

        public StandardExecutableInfo(final ExecutableType detectExecutableType, final String override) {
            this.detectExecutableType = detectExecutableType;
            this.override = override;
        }
    }
}
