/**
 * detect-application
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
package com.synopsys.integration.detect.tool.bazel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectTool;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.tool.SimpleToolDetector;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PropertyInsufficientDetectorResult;

public class BazelDetector extends SimpleToolDetector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectorEnvironment environment;
    private final BazelExtractor bazelExtractor;
    private final BazelExecutableFinder bazelExecutableFinder;
    private String bazelExe;
    private final DetectConfiguration detectConfiguration;

    public BazelDetector(final DetectorEnvironment environment, final BazelExtractor bazelExtractor,
        BazelExecutableFinder bazelExecutableFinder, final DetectConfiguration detectConfiguration) {
        super(DetectTool.BAZEL);
        this.environment = environment;
        this.bazelExtractor = bazelExtractor;
        this.bazelExecutableFinder = bazelExecutableFinder;
        this.detectConfiguration = detectConfiguration;
    }

    @Override
    public DetectorResult applicable() {
        final String bazelTarget = detectConfiguration.getProperty(DetectProperty.DETECT_BAZEL_TARGET, PropertyAuthority.None);
        if (StringUtils.isBlank(bazelTarget)) {
            return new PropertyInsufficientDetectorResult();
        }
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        bazelExe = bazelExecutableFinder.findBazel(environment);
        if (bazelExe == null) {
            logger.debug("Bazel command not found");
            return new ExecutableNotFoundDetectorResult("bazel");
        }
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract() {
        Extraction extractResult = bazelExtractor.extract(bazelExe, environment.getDirectory());
        return extractResult;
    }
}
