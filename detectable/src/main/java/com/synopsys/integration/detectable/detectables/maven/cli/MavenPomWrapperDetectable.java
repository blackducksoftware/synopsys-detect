/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
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

    private ExecutableTarget mavenExe;

    public MavenPomWrapperDetectable(DetectableEnvironment environment, FileFinder fileFinder, MavenResolver mavenResolver, MavenCliExtractor mavenCliExtractor,
        MavenCliExtractorOptions mavenCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.mavenResolver = mavenResolver;
        this.mavenCliExtractor = mavenCliExtractor;
        this.mavenCliExtractorOptions = mavenCliExtractorOptions; //TODO: Should this be wrapped in a detectable options? - jp
    }

    @Override
    public DetectableResult applicable() {
        File pom = fileFinder.findFile(environment.getDirectory(), POM_WRAPPER_FILENAME);

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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return mavenCliExtractor.extract(environment.getDirectory(), mavenExe, mavenCliExtractorOptions);
    }

}
