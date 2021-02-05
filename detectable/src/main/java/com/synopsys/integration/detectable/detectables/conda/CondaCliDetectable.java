/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.conda;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.CondaResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Python", forge = "Anaconda", requirementsMarkdown = "File: environment.yml. <br /><br /> Executable: conda.")
public class CondaCliDetectable extends Detectable {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    private final FileFinder fileFinder;
    private CondaResolver condaResolver;
    private final CondaCliExtractor condaExtractor;
    private CondaCliDetectableOptions condaCliDetectableOptions;

    private File condaExe;

    public CondaCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final CondaResolver condaResolver, final CondaCliExtractor condaExtractor, CondaCliDetectableOptions condaCliDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.condaResolver = condaResolver;
        this.condaExtractor = condaExtractor;
        this.condaCliDetectableOptions = condaCliDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(ENVIRONEMNT_YML);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        condaExe = requirements.executable(condaResolver::resolveConda, "conda");
        return requirements.result();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return condaExtractor.extract(environment.getDirectory(), condaExe, extractionEnvironment.getOutputDirectory(), condaCliDetectableOptions.getCondaEnvironmentName().orElse(""));
    }

}
