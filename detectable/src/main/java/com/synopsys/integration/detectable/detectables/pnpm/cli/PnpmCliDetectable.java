package com.synopsys.integration.detectable.detectables.pnpm.cli;

import java.io.File;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.PnpmResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PnpmNodeModulesNotFoundDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "Files: node_modules, package.json. <br /><br /> Executable: pnpm.")
public class PnpmCliDetectable extends Detectable {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final PnpmResolver pnpmResolver;
    private final PnpmCliExtractor pnpmCliExtractor;
    private final PnpmCliExtractorOptions pnpmCliExtractorOptions;

    private File packageJson;
    private ExecutableTarget pnpmExe;

    public PnpmCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, PnpmResolver pnpmResolver, PnpmCliExtractor pnpmCliExtractor, PnpmCliExtractorOptions pnpmCliExtractorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.pnpmResolver = pnpmResolver;
        this.pnpmCliExtractor = pnpmCliExtractor;
        this.pnpmCliExtractorOptions = pnpmCliExtractorOptions;
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
            return new PnpmNodeModulesNotFoundDetectableResult(environment.getDirectory().getAbsolutePath());
        }
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.explainDirectory(nodeModules);

        pnpmExe = requirements.executable(() -> pnpmResolver.resolvePnpm(environment), "pnpm");

        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return pnpmCliExtractor.extract(
            environment.getDirectory(),
            pnpmExe,
            pnpmCliExtractorOptions.getPnpmArguments().orElse(null),
            pnpmCliExtractorOptions.shouldIncludeDevDependencies(),
            packageJson
        );
    }
}
