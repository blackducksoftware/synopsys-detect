/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.misc.Nullable;

import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.util.OperatingSystemType;

public class SimpleExecutableFinder {
    private final List<String> extensions;
    private final FileFinder fileFinder;

    public static SimpleExecutableFinder forCurrentOperatingSystem(final FileFinder fileFinder) {
        return SimpleExecutableFinder.forOperatingSystem(OperatingSystemType.determineFromSystem(), fileFinder);
    }

    public static SimpleExecutableFinder forOperatingSystem(final OperatingSystemType operatingSystemType, final FileFinder fileFinder) {
        if (operatingSystemType == OperatingSystemType.WINDOWS) {
            return new SimpleExecutableFinder(Arrays.asList(".cmd", ".bat", ".exe"), fileFinder);
        } else {
            return new SimpleExecutableFinder(Collections.emptyList(), fileFinder);
        }
    }

    public SimpleExecutableFinder(final List<String> extensions, final FileFinder fileFinder) {
        this.extensions = extensions;
        this.fileFinder = fileFinder;
    }

    private List<String> executablesFromName(final String name) {
        if (extensions.isEmpty()) {
            return Collections.singletonList(name);
        } else {
            return extensions.stream().map(ext -> name + ext).collect(Collectors.toList());
        }
    }

    @Nullable
    public File findExecutable(final String executable, final File location) {
        return findExecutable(executable, Collections.singletonList(location));
    }

    @Nullable
    public File findExecutable(final String executable, final List<File> locations) {
        final List<String> executables = executablesFromName(executable);

        for (final File location : locations) {
            for (final String possibleExecutable : executables) {
                final File foundFile = fileFinder.findFile(location, possibleExecutable);
                if (foundFile != null && foundFile.exists() && foundFile.canExecute()) {
                    return foundFile;
                }
            }
        }

        return null;
    }
}