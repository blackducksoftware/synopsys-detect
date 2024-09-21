package com.blackduck.integration.detectable.detectables.pear;

import java.io.File;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.resolver.PearResolver;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Pear CLI", language = "PHP", forge = "Pear", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: package.xml. Executable: pear.")
public class PearCliDetectable extends Detectable {
    public static final String PACKAGE_XML_FILENAME = "package.xml";

    private final FileFinder fileFinder;
    private final PearResolver pearResolver;
    private final PearCliExtractor pearCliExtractor;

    private ExecutableTarget pearExe;
    private File packageDotXml;

    public PearCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, PearResolver pearResolver, PearCliExtractor pearCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pearResolver = pearResolver;
        this.pearCliExtractor = pearCliExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packageDotXml = requirements.file(PACKAGE_XML_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        pearExe = requirements.executable(pearResolver::resolvePear, "pear");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return pearCliExtractor.extract(pearExe, packageDotXml, environment.getDirectory());
    }

}
