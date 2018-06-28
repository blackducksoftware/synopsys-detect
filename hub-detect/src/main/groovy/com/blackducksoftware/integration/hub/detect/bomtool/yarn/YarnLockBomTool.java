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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.manager.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.manager.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class YarnLockBomTool extends BomTool {
    public static final String YARN_LOCK_FILENAME = "yarn.lock";

    private final DetectFileFinder fileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final YarnLockExtractor yarnLockExtractor;
    private final boolean productionDependenciesOnly;

    File yarnlock;
    String yarnExe;

    public YarnLockBomTool(final BomToolEnvironment environment, final boolean productionDependenciesOnly, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder,
            final YarnLockExtractor yarnLockExtractor) {
        super(environment, "Yarn Lock", BomToolGroupType.YARN, BomToolType.YARN_LOCK);
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
        this.standardExecutableFinder = standardExecutableFinder;
        this.productionDependenciesOnly = productionDependenciesOnly;
    }

    @Override
    public BomToolResult applicable() {
        yarnlock = fileFinder.findFile(environment.getDirectory(), YARN_LOCK_FILENAME);
        if (yarnlock == null) {
            return new FileNotFoundBomToolResult(YARN_LOCK_FILENAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        final File yarn = standardExecutableFinder.getExecutable(StandardExecutableType.YARN);
        if (yarn != null) {
            yarnExe = yarn.toString();
        }

        if (productionDependenciesOnly && StringUtils.isBlank(yarnExe)) {
            return new ExecutableNotFoundBomToolResult("Could not find the Yarn executable, can not get the production only dependencies.");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return yarnLockExtractor.extract(this.getBomToolType(), environment.getDirectory(), yarnlock, yarnExe);
    }

}
