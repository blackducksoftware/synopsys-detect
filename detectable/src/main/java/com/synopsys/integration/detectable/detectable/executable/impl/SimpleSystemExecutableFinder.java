/**
 * detectable
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
package com.synopsys.integration.detectable.detectable.executable.impl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSystemExecutableFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SimpleExecutableFinder executableFinder;

    public SimpleSystemExecutableFinder(SimpleExecutableFinder executableFinder) {
        this.executableFinder = executableFinder;
    }

    public File findExecutable(final String executable) {
        final String systemPath = System.getenv("PATH");
        List<File> systemPathLocations = Arrays.stream(systemPath.split(File.pathSeparator))
                                             .map(File::new)
                                             .collect(Collectors.toList());

        File found = executableFinder.findExecutable(executable, systemPathLocations);
        if (found == null) {
            logger.debug(String.format("Could not find the executable: %s while searching through: %s", executable, systemPath));
        }
        return found;
    }
}
