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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import java.io.File;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class MavenPomWrapperStrategy extends Strategy {
    public static final String POM_FILENAME = "pom.xml";

    private final DetectFileFinder fileFinder;
    private final MavenExecutableFinder mavenExecutableFinder;
    private final MavenCliExtractor mavenCliExtractor;

    private String mavenExe;

    public MavenPomWrapperStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final MavenExecutableFinder mavenExecutableFinder, final MavenCliExtractor mavenCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.mavenExecutableFinder = mavenExecutableFinder;
        this.mavenCliExtractor = mavenCliExtractor;
    }

    @Override
    public StrategyResult applicable() {
        final File pom = fileFinder.findFile(environment.getDirectory(), POM_FILENAME);
        if (pom == null) {
            return new FileNotFoundStrategyResult(POM_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable(){
        mavenExe = mavenExecutableFinder.findMaven(environment);

        if (mavenExe == null) {
            return new ExecutableNotFoundStrategyResult("mvn");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return mavenCliExtractor.extract(environment.getDirectory(), mavenExe);
    }

    @Override
    public String getName() {
        return "Pom wrapper file";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.MAVEN;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.MAVEN_POM_WRAPPER_CLI;
    }


}
