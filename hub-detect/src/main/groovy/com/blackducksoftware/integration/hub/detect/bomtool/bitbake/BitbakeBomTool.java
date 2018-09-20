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
package com.blackducksoftware.integration.hub.detect.bomtool.bitbake;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.PropertyInsufficientBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class BitbakeBomTool extends BomTool {
    public static Forge YOCTO_FORGE = new Forge("/", "/", "yocto");

    private final DetectFileFinder detectFileFinder;
    private final DetectConfigWrapper detectConfigWrapper;
    private final BitbakeExtractor bitbakeExtractor;

    private File foundBuildEnvScript;

    public BitbakeBomTool(final BomToolEnvironment bomToolEnvironment, final DetectFileFinder detectFileFinder, final DetectConfigWrapper detectConfigWrapper, final BitbakeExtractor bitbakeExtractor) {
        super(bomToolEnvironment, "Bitbake", BomToolGroupType.BITBAKE, BomToolType.BITBAKE_CLI);
        this.detectFileFinder = detectFileFinder;
        this.detectConfigWrapper = detectConfigWrapper;
        this.bitbakeExtractor = bitbakeExtractor;
    }

    @Override
    public BomToolResult applicable() {
        foundBuildEnvScript = detectFileFinder.findFile(environment.getDirectory(), detectConfigWrapper.getProperty(DetectProperty.DETECT_INIT_BUILD_ENV_NAME));
        if (foundBuildEnvScript == null) {
            return new FileNotFoundBomToolResult(DetectProperty.DETECT_INIT_BUILD_ENV_NAME.getDefaultValue());
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() {
        final String packageName = detectConfigWrapper.getProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAME);
        if (StringUtils.isBlank(packageName)) {
            return new PropertyInsufficientBomToolResult();
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        try {
            return bitbakeExtractor.extract(extractionId, foundBuildEnvScript.getCanonicalPath(), environment.getDirectory().getCanonicalPath());
        } catch (final IOException | ExecutableRunnerException e) {
            return new Extraction.Builder().failure(String.format("Failed to extract dependencies from bitbake: %s", e.getMessage())).build();
        }
    }
}
