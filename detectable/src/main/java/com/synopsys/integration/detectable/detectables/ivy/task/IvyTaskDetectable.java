package com.synopsys.integration.detectable.detectables.ivy.task;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.AntResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "Maven Central", requirementsMarkdown = "File: build.xml. Executable: ant.")
public class IvyTaskDetectable extends Detectable {
    private static final String BUILD_XML_FILENAME = "build.xml";

    private final FileFinder fileFinder;
    private final AntResolver antResolver;
    private final IvyTaskExtractor ivyTaskExtractor;
    private final IvyTaskDetectableOptions ivyTaskDetectableOptions;

    private ExecutableTarget antExe;
    private File buildXml;

    public IvyTaskDetectable(DetectableEnvironment environment, FileFinder fileFinder, AntResolver antResolver, IvyTaskExtractor ivyTaskExtractor,
                             IvyTaskDetectableOptions ivyTaskDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.antResolver = antResolver;
        this.ivyTaskExtractor = ivyTaskExtractor;
        this.ivyTaskDetectableOptions = ivyTaskDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        buildXml = requirements.file(BUILD_XML_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        antExe = requirements.executable(() -> antResolver.resolveAnt(), "ant");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException, IOException {
        return ivyTaskExtractor.extract(environment.getDirectory(), antExe, buildXml, ivyTaskDetectableOptions);
    }
}
