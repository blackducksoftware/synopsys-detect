package com.blackduck.integration.detectable.detectables.go.vendr;

import java.io.File;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.PassedDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "GoVndr CLI", language = "Golang", forge = "GitHub", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: vendor.conf.")
public class GoVndrDetectable extends Detectable {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    private final FileFinder fileFinder;
    private final GoVndrExtractor goVndrExtractor;

    private File vndrConfig;

    public GoVndrDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoVndrExtractor goVndrExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goVndrExtractor = goVndrExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        vndrConfig = requirements.file(VNDR_CONF_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return goVndrExtractor.extract(vndrConfig);
    }

}
