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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pear;

import java.io.File;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.manager.result.search.ExtractionId;
import com.blackducksoftware.integration.hub.detect.manager.result.search.StrategyType;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.strategy.result.ExecutableNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.FileNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.strategy.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class PearCliStrategy extends Strategy {
    public static final String PACKAGE_XML_FILENAME= "package.xml";

    private final DetectFileFinder fileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final PearCliExtractor pearCliExtractor;

    private File pearExe;

    public PearCliStrategy(final StrategyEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final PearCliExtractor pearCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.standardExecutableFinder = standardExecutableFinder;
        this.pearCliExtractor = pearCliExtractor;
    }

    @Override
    public StrategyResult applicable() {
        final File PEAR= fileFinder.findFile(environment.getDirectory(), PACKAGE_XML_FILENAME);
        if (PEAR == null) {
            return new FileNotFoundStrategyResult(PACKAGE_XML_FILENAME);
        }

        return new PassedStrategyResult();
    }

    @Override
    public StrategyResult extractable() throws StrategyException {
        pearExe = standardExecutableFinder.getExecutable(StandardExecutableType.PEAR);

        if (pearExe == null) {
            return new ExecutableNotFoundStrategyResult("pear");
        }

        return new PassedStrategyResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pearCliExtractor.extract(environment.getDirectory(), pearExe);
    }

    @Override
    public String getName() {
        return "Pear Cli";
    }

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.PEAR;
    }

    @Override
    public StrategyType getStrategyType() {
        return StrategyType.PEAR_CLI;
    }


}
