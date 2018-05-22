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
package com.blackducksoftware.integration.hub.detect.util.executable;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.type.OperatingSystemType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class ExecutableManager {
    private final Logger logger = LoggerFactory.getLogger(ExecutableManager.class);

    @Autowired
    private DetectFileFinder detectFileFinder;

    @Autowired
    private DetectInfo detectInfo;

    private final Map<String, File> cachedSystemExecutables = new HashMap<>();

    public String getExecutableName(final ExecutableType executableType) {
        return executableType.getExecutable();
    }

    public String getExecutablePath(final ExecutableType executableType, final boolean searchSystemPath, final String path) {
        final File executable = getExecutable(executableType, searchSystemPath, path);
        if (executable != null) {
            return executable.getAbsolutePath();
        } else {
            return null;
        }
    }

    public String getExecutablePathOrOverride(final ExecutableType executableType, final boolean searchSystemPath, final File path, final String override) {
        return getExecutablePathOrOverride(executableType, searchSystemPath, path.toString(), override);
    }

    public String getExecutablePathOrOverride(final ExecutableType executableType, final boolean searchSystemPath, final String path, final String override) {
        if (StringUtils.isNotBlank(override)) {
            return override;
        } else {
            return getExecutablePath(executableType, searchSystemPath, path);
        }
    }

    public File getExecutable(final ExecutableType executableType, final boolean searchSystemPath, final String path) {
        final String executable = getExecutableName(executableType);
        final String searchPath = path.trim();
        File executableFile = findExecutableFileFromPath(searchPath, executable);
        if (searchSystemPath && (executableFile == null || !executableFile.exists())) {
            executableFile = findExecutableFileFromSystemPath(executable);
        }

        return executableFile;
    }

    private File findExecutableFileFromSystemPath(final String executable) {
        final String systemPath = System.getenv("PATH");
        if (!cachedSystemExecutables.containsKey(executable)) {
            cachedSystemExecutables.put(executable, findExecutableFileFromPath(systemPath, executable));
        }
        return cachedSystemExecutables.get(executable);

    }

    private File findExecutableFileFromPath(final String path, final String executableName) {
        final List<String> executables;
        final OperatingSystemType currentOs = detectInfo.getCurrentOs();
        if (currentOs == OperatingSystemType.WINDOWS) {
            executables = Arrays.asList(executableName + ".cmd", executableName + ".bat", executableName + ".exe");
        } else {
            executables = Arrays.asList(executableName);
        }

        for (final String pathPiece : path.split(File.pathSeparator)) {
            for (final String possibleExecutable : executables) {
                final File foundFile = detectFileFinder.findFile(pathPiece, possibleExecutable);
                if (foundFile != null && foundFile.exists() && foundFile.canExecute()) {
                    return foundFile;
                }
            }
        }
        logger.debug(String.format("Could not find the executable: %s while searching through: %s", executableName, path));
        return null;
    }
}
