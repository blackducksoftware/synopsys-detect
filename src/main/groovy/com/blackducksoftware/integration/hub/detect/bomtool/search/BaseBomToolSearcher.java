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
package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

public abstract class BaseBomToolSearcher<T extends BomToolSearchResult> implements BomToolSearcher<T> {
    @Autowired
    private ExecutableManager executableManager;

    @Autowired
    private ExecutableRunner executableRunner;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Override
    public final T getBomToolSearchResult(final String directoryPathToSearch) throws BomToolException {
        if (StringUtils.isBlank(directoryPathToSearch)) {
            throw new BomToolException(String.format("The provided path %s is empty.", directoryPathToSearch));
        }

        final File directoryToSearch = new File(directoryPathToSearch);
        return getBomToolSearchResult(directoryToSearch);
    }

    @Override
    public final T getBomToolSearchResult(final File directoryToSearch) throws BomToolException {
        if (!directoryToSearch.isDirectory()) {
            throw new BomToolException(String.format("The provided file %s is not a directory.", directoryToSearch.getAbsolutePath()));
        }

        return getSearchResult(directoryToSearch);
    }

    public abstract T getSearchResult(File directoryToSearch);

    public String findExecutablePath(final ExecutableType executable, final boolean searchSystemPath, final File directoryToSearch, final String optionalPathOverride) {
        if (StringUtils.isNotBlank(optionalPathOverride)) {
            return optionalPathOverride;
        }

        return executableManager.getExecutablePath(executable, searchSystemPath, directoryToSearch.getAbsolutePath());
    }

    public ExecutableManager getExecutableManager() {
        return executableManager;
    }

    public ExecutableRunner getExecutableRunner() {
        return executableRunner;
    }

    public DetectFileManager getDetectFileManager() {
        return detectFileManager;
    }

    public DetectConfiguration getDetectConfiguration() {
        return detectConfiguration;
    }
}
