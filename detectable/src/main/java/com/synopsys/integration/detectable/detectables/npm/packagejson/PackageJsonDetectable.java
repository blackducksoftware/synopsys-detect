package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class PackageJsonDetectable extends Detectable {
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final PackageJsonExtractor packageJsonExtractor;
    private final boolean includeDevDependencies;

    private File packageJsonFile;

    public PackageJsonDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PackageJsonExtractor packageJsonExtractor, final boolean includeDevDependencies) {
        super(environment, "package.json", "NPM");
        this.fileFinder = fileFinder;
        this.packageJsonExtractor = packageJsonExtractor;
        this.includeDevDependencies = includeDevDependencies;
    }

    @Override
    public DetectableResult applicable() {
        packageJsonFile = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);

        if (packageJsonFile == null) {
            return new FileNotFoundDetectableResult(PACKAGE_JSON);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try (final InputStream packageJsonInputStream = new FileInputStream(packageJsonFile)) {
            return packageJsonExtractor.extract(packageJsonInputStream, includeDevDependencies);
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).failure(String.format("Failed to parse %s", PACKAGE_JSON)).build();
        }
    }
}
