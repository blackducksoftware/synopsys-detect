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
package com.synopsys.integration.detect.util.executable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.type.ExecutableType;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class CacheableExecutableFinder {
    /*
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private DirectoryManager directoryManager;

    public enum CacheableExecutableType {
        CONDA,
        CPAN,
        CPANM,
        DOCKER,
        BASH,
        GO,
        REBAR3,
        PEAR,
        YARN,
        JAVA,
        BAZEL
    }

    private final ExecutableFinder executableFinder;
    private final DetectConfiguration detectConfiguration;

    private final Map<CacheableExecutableType, File> alreadyFound = new HashMap<>();

    public CacheableExecutableFinder(final DirectoryManager directoryManager, final ExecutableFinder executableFinder, final DetectConfiguration detectConfiguration) {
        this.directoryManager = directoryManager;
        this.executableFinder = executableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public File getExecutable(final CacheableExecutableType executableType) throws Exception {
        if (alreadyFound.containsKey(executableType)) {
            logger.debug("Already found executable, resolving with cached value.");
            return alreadyFound.get(executableType);
        }
        final StandardExecutableInfo info = createInfo(executableType);
        if (info == null) {
            throw new Exception("Unknown executable type: " + executableType.toString());
        }

        final String exe = executableFinder.getExecutablePathOrOverride(info.detectExecutableType, true, directoryManager.getSourceDirectory(), info.override);
        File exeFile = null;
        if (exe != null) {
            exeFile = new File(exe);
        }
        logger.debug("Cached executable " + executableType.toString() + " to: " + exeFile.getAbsolutePath());
        alreadyFound.put(executableType, exeFile);
        return exeFile;
    }

    public StandardExecutableInfo createInfo(final CacheableExecutableType type) {
        switch (type) {
            case CONDA:
                return new StandardExecutableInfo(ExecutableType.CONDA, detectConfiguration.getProperty(DetectProperty.DETECT_CONDA_PATH, PropertyAuthority.None));
            case CPAN:
                return new StandardExecutableInfo(ExecutableType.CPAN, detectConfiguration.getProperty(DetectProperty.DETECT_CPAN_PATH, PropertyAuthority.None));
            case CPANM:
                return new StandardExecutableInfo(ExecutableType.CPANM, detectConfiguration.getProperty(DetectProperty.DETECT_CPANM_PATH, PropertyAuthority.None));
            case DOCKER:
                return new StandardExecutableInfo(ExecutableType.DOCKER, detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_PATH, PropertyAuthority.None));
            case BASH:
                return new StandardExecutableInfo(ExecutableType.BASH, detectConfiguration.getProperty(DetectProperty.DETECT_BASH_PATH, PropertyAuthority.None));
            case GO:
                return new StandardExecutableInfo(ExecutableType.GO, null);
            case REBAR3:
                return new StandardExecutableInfo(ExecutableType.REBAR3, detectConfiguration.getProperty(DetectProperty.DETECT_HEX_REBAR3_PATH, PropertyAuthority.None));
            case PEAR:
                return new StandardExecutableInfo(ExecutableType.PEAR, detectConfiguration.getProperty(DetectProperty.DETECT_PEAR_PATH, PropertyAuthority.None));
            case YARN:
                return new StandardExecutableInfo(ExecutableType.YARN, detectConfiguration.getProperty(DetectProperty.DETECT_YARN_PATH, PropertyAuthority.None));
            case JAVA:
                return new StandardExecutableInfo(ExecutableType.JAVA, detectConfiguration.getProperty(DetectProperty.DETECT_JAVA_PATH, PropertyAuthority.None));
            case BAZEL:
                return new StandardExecutableInfo(ExecutableType.BAZEL, detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_PATH, PropertyAuthority.None));
        }
        return null;
    }

    protected boolean isAlreadyFound(final CacheableExecutableType executableType) {
        return alreadyFound.containsKey(executableType);
    }

    private class StandardExecutableInfo {
        public ExecutableType detectExecutableType;
        public String override;

        public StandardExecutableInfo(final ExecutableType detectExecutableType, final String override) {
            this.detectExecutableType = detectExecutableType;
            this.override = override;
        }
    } */
}
