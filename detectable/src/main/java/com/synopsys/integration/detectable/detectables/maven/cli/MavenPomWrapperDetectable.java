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
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "Maven Central", requirementsMarkdown = "File: pom.groovy.<br/><br/>Executable: mvnw or mvn.")
public class MavenPomWrapperDetectable extends Detectable {
    public static final String POM_WRAPPER_FILENAME = "pom.groovy";

    private final FileFinder fileFinder;
    private final MavenResolver mavenResolver;
    private final MavenCliExtractor mavenCliExtractor;
    private final MavenCliExtractorOptions mavenCliExtractorOptions;

    private File mavenExe;

    public MavenPomWrapperDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final MavenResolver mavenResolver, final MavenCliExtractor mavenCliExtractor,
        final MavenCliExtractorOptions mavenCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.mavenResolver = mavenResolver;
        this.mavenCliExtractor = mavenCliExtractor;
        this.mavenCliExtractorOptions = mavenCliExtractorOptions; //TODO: Should this be wrapped in a detectable options? - jp
    }

    @Override
    public DetectableResult applicable() {
        final File pom = fileFinder.findFile(environment.getDirectory(), POM_WRAPPER_FILENAME);

        if (pom == null) {
            return new FileNotFoundDetectableResult(POM_WRAPPER_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        mavenExe = mavenResolver.resolveMaven(environment);

        if (mavenExe == null) {
            return new ExecutableNotFoundDetectableResult("mvn");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return mavenCliExtractor.extract(environment.getDirectory(), mavenExe, mavenCliExtractorOptions);
    }

}
