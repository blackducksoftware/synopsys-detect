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
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.CacheableExecutableFinder.CacheableExecutableType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;

public class YarnLockBomTool extends BomTool {
    private static final String YARN_LOCK_FILENAME = "yarn.lock";

    private final DetectFileFinder fileFinder;
    private final CacheableExecutableFinder cacheableExecutableFinder;
    private final YarnLockExtractor yarnLockExtractor;

    private File yarnlock;
    private String yarnExe = "";

    public YarnLockBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final CacheableExecutableFinder cacheableExecutableFinder, final YarnLockExtractor yarnLockExtractor) {
        super(environment, "Yarn Lock", BomToolGroupType.YARN, BomToolType.YARN_LOCK);
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
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
        final File yarn = cacheableExecutableFinder.getExecutable(CacheableExecutableType.YARN);
        if (yarn != null) {
            yarnExe = yarn.toString();
        }

        if (StringUtils.isBlank(yarnExe)) {
            return new ExecutableNotFoundBomToolResult("yarn");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return yarnLockExtractor.extract(this.getBomToolType(), environment.getDirectory(), yarnlock, yarnExe);
    }

}
