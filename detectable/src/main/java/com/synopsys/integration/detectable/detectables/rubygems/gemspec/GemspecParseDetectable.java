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
package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Ruby", forge = "RubyGems", requirementsMarkdown = "File: A gemspec file (with .gemspec extension).")
public class GemspecParseDetectable extends Detectable {
    private static final String GEMSPEC_FILENAME = "*.gemspec";

    private final FileFinder fileFinder;
    private final GemspecParseExtractor gemspecParseExtractor;
    private final GemspecParseDetectableOptions gemspecParseDetectableOptions;

    private File gemspec;

    public GemspecParseDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GemspecParseExtractor gemspecParseExtractor,
        final GemspecParseDetectableOptions gemspecParseDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.gemspecParseExtractor = gemspecParseExtractor;
        this.gemspecParseDetectableOptions = gemspecParseDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        gemspec = requirements.file(GEMSPEC_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return gemspecParseExtractor.extract(gemspec, gemspecParseDetectableOptions.shouldIncludeRuntimeDependencies(), gemspecParseDetectableOptions.shouldIncludeDevelopmentDependencies());
    }
}
