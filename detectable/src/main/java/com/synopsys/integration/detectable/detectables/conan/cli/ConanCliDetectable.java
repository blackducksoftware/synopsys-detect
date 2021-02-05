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
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "C/C++", forge = "conan", requirementsMarkdown = "Files: conanfile.txt or conanfile.py. <br /><br /> Executable: conan.")
public class ConanCliDetectable extends Detectable {
    public static final String CONANFILETXT = "conanfile.txt";
    public static final String CONANFILEPY = "conanfile.py";
    private final FileFinder fileFinder;
    private final ConanResolver conanResolver;
    private final ConanCliExtractor conanCliExtractor;
    private final ConanCliExtractorOptions conanCliExtractorOptions;

    private File conanExe;

    public ConanCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, ConanResolver conanResolver, ConanCliExtractor conanCliExtractor,
        ConanCliExtractorOptions conanCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.conanResolver = conanResolver;
        this.conanCliExtractor = conanCliExtractor;
        this.conanCliExtractorOptions = conanCliExtractorOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(CONANFILETXT);
        requirements.ifCurrentlyMet(() -> requirements.file(CONANFILEPY));//This is how it was written, don't really see why it was this way - jp
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        conanExe = requirements.executable(() -> conanResolver.resolveConan(environment), "conan");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return conanCliExtractor.extract(environment.getDirectory(), conanExe, conanCliExtractorOptions);
    }
}
