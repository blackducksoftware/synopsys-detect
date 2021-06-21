/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.cli;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.NpmNodeModulesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectables.npm.NpmPackageJsonNameVersionExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "Files: node_modules, package.json. <br /><br /> Executable: npm.")
public class NpmCliDetectable extends Detectable {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final NpmResolver npmResolver;
    private final NpmCliExtractor npmCliExtractor;
    private final NpmCliExtractorOptions npmCliExtractorOptions;
    private final NpmPackageJsonNameVersionExtractor npmPackageJsonNameVersionExtractor;

    private File packageJson;
    private ExecutableTarget npmExe;

    public NpmCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, NpmResolver npmResolver, NpmCliExtractor npmCliExtractor, NpmPackageJsonNameVersionExtractor npmPackageJsonNameVersionExtractor,
        NpmCliExtractorOptions npmCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.npmResolver = npmResolver;
        this.npmCliExtractor = npmCliExtractor;
        this.npmCliExtractorOptions = npmCliExtractorOptions; // TODO: Should this be wrapped in a detectables option?
        this.npmPackageJsonNameVersionExtractor = npmPackageJsonNameVersionExtractor;
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        packageJson = requirements.file(PACKAGE_JSON);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        File nodeModules = fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (nodeModules == null) {
            return new NpmNodeModulesNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.explainDirectory(nodeModules);

        npmExe = requirements.executable(() -> npmResolver.resolveNpm(environment), "npm");

        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return npmCliExtractor.extract(
            environment.getDirectory(),
            npmExe,
            npmCliExtractorOptions.getNpmArguments().orElse(null),
            npmCliExtractorOptions.shouldIncludeDevDependencies(),
            npmCliExtractorOptions.shouldIncludePeerDependencies(),
            packageJson
        );
    }

}
