package com.synopsys.integration.detectable.detectables.ivy;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "various", forge = "Maven Central", requirementsMarkdown = "File: ivy.xml.")
public class IvyParseDetectable extends Detectable {
    private static final String IVY_XML_FILENAME = "ivy.xml";

    private final FileFinder fileFinder;
    private final IvyParseExtractor ivyParseExtractor;

    private File ivyXmlFile;

    public IvyParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, IvyParseExtractor ivyParseExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.ivyParseExtractor = ivyParseExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        ivyXmlFile = requirements.file(IVY_XML_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return ivyParseExtractor.extract(ivyXmlFile);
    }
}
