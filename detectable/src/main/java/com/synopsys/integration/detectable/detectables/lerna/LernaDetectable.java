/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.LernaResolver;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmPackageLockDetectable;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmShrinkwrapDetectable;
import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "File: " + LernaDetectable.PACKAGE_JSON + ", and one of the following: " + LernaDetectable.PACKAGE_LOCK_JSON + ", " + LernaDetectable.SHRINKWRAP_JSON + ", "
                                                                                  + LernaDetectable.YARN_LOCK + ".")
public class LernaDetectable extends Detectable {
    public static final String LERNA_JSON = "lerna.json";
    public static final String PACKAGE_JSON = NpmPackageLockDetectable.PACKAGE_JSON;
    public static final String PACKAGE_LOCK_JSON = NpmPackageLockDetectable.PACKAGE_LOCK_JSON;
    public static final String SHRINKWRAP_JSON = NpmShrinkwrapDetectable.SHRINKWRAP_JSON;
    public static final String YARN_LOCK = YarnLockDetectable.YARN_LOCK_FILENAME;

    private final FileFinder fileFinder;
    private final LernaResolver lernaResolver;
    private final LernaExtractor lernaExtractor;

    private ExecutableTarget lernaExecutable;
    private File packageJson;

    public LernaDetectable(DetectableEnvironment environment, FileFinder fileFinder, LernaResolver lernaResolver, LernaExtractor lernaExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.lernaResolver = lernaResolver;
        this.lernaExtractor = lernaExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(LERNA_JSON);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);

        // Lerna is used in conjunction with traditional NPM projects or Yarn projects.
        File packageLockFile = fileFinder.findFile(environment.getDirectory(), PACKAGE_LOCK_JSON);
        File shrinkwrapFile = fileFinder.findFile(environment.getDirectory(), SHRINKWRAP_JSON);
        File yarnLockFile = fileFinder.findFile(environment.getDirectory(), YARN_LOCK);
        if (packageLockFile == null && shrinkwrapFile == null && yarnLockFile == null) {
            return new FilesNotFoundDetectableResult(PACKAGE_LOCK_JSON, YARN_LOCK);
        }
        requirements.explainNullableFile(packageLockFile);
        requirements.explainNullableFile(shrinkwrapFile);
        requirements.explainNullableFile(yarnLockFile);

        packageJson = requirements.file(PACKAGE_JSON);
        lernaExecutable = requirements.executable(lernaResolver::resolveLerna, "lerna");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return lernaExtractor.extract(environment.getDirectory(), packageJson, lernaExecutable);
    }
}
