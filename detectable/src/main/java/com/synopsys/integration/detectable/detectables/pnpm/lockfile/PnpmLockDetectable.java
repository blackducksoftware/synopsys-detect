/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pnpm.lockfile;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.enums.DependencyType;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.yarn.packagejson.PackageJsonFiles;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "Files: pnpm-lock.yaml and package.json.")
public class PnpmLockDetectable extends Detectable {
    public static final String PNPM_LOCK_YAML_FILENAME = "pnpm-lock.yaml";
    public static final String PACKAGE_JSON = "package.json";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final PnpmLockExtractor pnpmExtractor;
    private final List<DependencyType> dependencyTypes;
    private final PackageJsonFiles packageJsonFiles;

    private File pnpmLockYaml;
    private File packageJson;

    public PnpmLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, PnpmLockExtractor pnpmExtractor, List<DependencyType> dependencyTypes, PackageJsonFiles packageJsonFiles) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pnpmExtractor = pnpmExtractor;
        this.dependencyTypes = dependencyTypes;
        this.packageJsonFiles = packageJsonFiles;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        pnpmLockYaml = requirements.file(PNPM_LOCK_YAML_FILENAME);
        packageJson = requirements.optionalFile(PACKAGE_JSON, () -> logger.warn("Pnpm applied but it could not find a package.json so project name and version may not be determined."));
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        PnpmLinkedPackageResolver linkedPackageResolver = new PnpmLinkedPackageResolver(pnpmLockYaml.getParentFile(), packageJsonFiles); // we are assuming parent of the lock file we are parsing is the project root
        return pnpmExtractor.extract(pnpmLockYaml, packageJson, dependencyTypes, linkedPackageResolver);
    }
}
