package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.npm.packagejson.PackageJsonExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "PIP Requirements Parse", language = "Python", forge = "PyPi", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: requirements.txt")
public class PipRequirementsParseDetectable extends Detectable {
    public static final String REQUIREMENTS_FILE_NAME = "requirements.txt"; // TODO if provided, override with requirements txt file name from Detect's property

    private final FileFinder fileFinder;
    private final PackageJsonExtractor packageJsonExtractor;

    private File packageJsonFile;

    public PipRequirementsParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, PackageJsonExtractor packageJsonExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageJsonExtractor = packageJsonExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packageJsonFile = requirements.file(REQUIREMENTS_FILE_NAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        try {
            return packageJsonExtractor.extract(packageJsonFile);
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).failure(String.format("Failed to parse %s", REQUIREMENTS_FILE_NAME)).build();
        }
    }
}
