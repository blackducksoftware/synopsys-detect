package com.synopsys.integration.detectable.detectables.pear;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PearResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

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
