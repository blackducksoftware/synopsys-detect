package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "NPM Package Json Parse", language = "Node JS", forge = "npmjs", accuracy = DetectableAccuracyType.LOW, requirementsMarkdown = "File: package.json.")
public class NpmPackageJsonParseDetectable extends Detectable {
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final PackageJsonExtractor packageJsonExtractor;

    private File packageJsonFile;

    public NpmPackageJsonParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, PackageJsonExtractor packageJsonExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageJsonExtractor = packageJsonExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packageJsonFile = requirements.file(PACKAGE_JSON);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        try (InputStream packageJsonInputStream = new FileInputStream(packageJsonFile)) {
            return packageJsonExtractor.extract(packageJsonInputStream);
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).failure(String.format("Failed to parse %s", PACKAGE_JSON)).build();
        }
    }
}
