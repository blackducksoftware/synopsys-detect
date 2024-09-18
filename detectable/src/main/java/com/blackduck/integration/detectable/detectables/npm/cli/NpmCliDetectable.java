package com.blackduck.integration.detectable.detectables.npm.cli;

import java.io.File;

import com.blackduck.integration.common.util.finder.FileFinder;
import com.blackduck.integration.detectable.Detectable;
import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.DetectableAccuracyType;
import com.blackduck.integration.detectable.detectable.Requirements;
import com.blackduck.integration.detectable.detectable.annotation.DetectableInfo;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.detectable.executable.resolver.NpmResolver;
import com.blackduck.integration.detectable.detectable.result.DetectableResult;
import com.blackduck.integration.detectable.detectable.result.NpmNodeModulesNotFoundDetectableResult;
import com.blackduck.integration.detectable.extraction.Extraction;
import com.blackduck.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "NPM CLI", language = "Node JS", forge = "npmjs", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "Files: node_modules, package.json. Executable: npm.")
public class NpmCliDetectable extends Detectable {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final NpmResolver npmResolver;
    private final NpmCliExtractor npmCliExtractor;
    private final NpmCliExtractorOptions npmCliExtractorOptions;

    private File packageJson;
    private ExecutableTarget npmExe;

    public NpmCliDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        NpmResolver npmResolver,
        NpmCliExtractor npmCliExtractor,
        NpmCliExtractorOptions npmCliExtractorOptions
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.npmResolver = npmResolver;
        this.npmCliExtractor = npmCliExtractor;
        this.npmCliExtractorOptions = npmCliExtractorOptions; // TODO: Should this be wrapped in a detectables option?
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
            packageJson
        );
    }

}
