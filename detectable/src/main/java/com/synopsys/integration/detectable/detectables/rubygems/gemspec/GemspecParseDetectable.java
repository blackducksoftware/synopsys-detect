/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecParser;

public class GemspecParseDetectable extends Detectable {
    private static final String GEMSPEC_FILENAME = "*.gemspec";

    private final FileFinder fileFinder;
    private final GemspecParser gemspecParser;
    private final GemspecParseDetectableOptions gemspecParseDetectableOptions;

    private File gemspec;

    public GemspecParseDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GemspecParser gemspecParser,
        final GemspecParseDetectableOptions gemspecParseDetectableOptions) {
        super(environment, "Gemspec", "RUBYGEMS");
        this.fileFinder = fileFinder;
        this.gemspecParser = gemspecParser;
        this.gemspecParseDetectableOptions = gemspecParseDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        gemspec = fileFinder.findFile(environment.getDirectory(), GEMSPEC_FILENAME);

        if (gemspec == null) {
            return new FileNotFoundDetectableResult(GEMSPEC_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try (final InputStream inputStream = new FileInputStream(gemspec)) {
            final DependencyGraph dependencyGraph = gemspecParser.parse(inputStream, gemspecParseDetectableOptions.shouldIncludeRuntimeDependencies(), gemspecParseDetectableOptions.shouldIncludeDevelopmentDependencies());
            final CodeLocation codeLocation = new CodeLocation(dependencyGraph);

            return new Extraction.Builder().success(codeLocation).build();
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
