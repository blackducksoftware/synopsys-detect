/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.packagejson;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "File: package.json.")
public class NpmPackageJsonParseDetectable extends Detectable {
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final PackageJsonExtractor packageJsonExtractor;
    private final boolean includeDevDependencies;
    private final boolean includePeerDependencies;

    private File packageJsonFile;

    public NpmPackageJsonParseDetectable(DetectableEnvironment environment, FileFinder fileFinder, PackageJsonExtractor packageJsonExtractor,
        NpmPackageJsonParseDetectableOptions npmPackageJsonParseDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.packageJsonExtractor = packageJsonExtractor;
        this.includeDevDependencies = npmPackageJsonParseDetectableOptions.shouldIncludeDevDependencies();
        this.includePeerDependencies = npmPackageJsonParseDetectableOptions.shouldIncludePeerDependencies();
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
            return packageJsonExtractor.extract(packageJsonInputStream, includeDevDependencies, includePeerDependencies);
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).failure(String.format("Failed to parse %s", PACKAGE_JSON)).build();
        }
    }
}
