package com.blackduck.integration.detectable.detectables.ivy;

import java.io.File;
import java.io.IOException;

import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.common.util.finder.FileFinder;

@DetectableInfo(name = "Ivy Build Parse", language = "various", forge = "Maven Central", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: ivy.xml, build.xml.")
public class IvyParseDetectable extends Detectable {
    private static final String IVY_XML_FILENAME = "ivy.xml";
    private static final String BUILD_XML_FILENAME = "build.xml";

    private final FileFinder fileFinder;
    private final IvyParseExtractor ivyParseExtractor;

    private File ivyXmlFile;
    @Nullable
    private File buildXml;

    public IvyParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, IvyParseExtractor ivyParseExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.ivyParseExtractor = ivyParseExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        ivyXmlFile = requirements.file(IVY_XML_FILENAME);
        buildXml = requirements.optionalFile(BUILD_XML_FILENAME).orElse(null); // used just for project name information
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws IOException {
        return ivyParseExtractor.extract(ivyXmlFile, buildXml);
    }
}
