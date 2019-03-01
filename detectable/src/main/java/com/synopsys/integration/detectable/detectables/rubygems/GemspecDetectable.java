package com.synopsys.integration.detectable.detectables.rubygems;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.rubygems.parse.GemspecParser;

public class GemspecDetectable extends Detectable {
    private static final String GEMSPEC_FILENAME = "Gemfile.lock";

    private final FileFinder fileFinder;
    private final GemspecParser gemspecParser;
    private final boolean includeRuntimeDependencies;
    private final boolean includeDevelopmentDependencies;

    private File gemspec;

    public GemspecDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GemspecParser gemspecParser, final boolean includeRuntimeDependencies, final boolean includeDevelopmentDependencies) {
        super(environment, "Gemspec", "RUBYGEMS");
        this.fileFinder = fileFinder;
        this.gemspecParser = gemspecParser;
        this.includeRuntimeDependencies = includeRuntimeDependencies;
        this.includeDevelopmentDependencies = includeDevelopmentDependencies;
    }

    @Override
    public DetectableResult applicable() {
        gemspec = fileFinder.findFile(environment.getDirectory(), GEMSPEC_FILENAME);

        if (gemspec == null) {
            return new FileNotFoundDetectableResult(GEMSPEC_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        try {
            final InputStream inputStream = new FileInputStream(gemspec);
            final DependencyGraph dependencyGraph = gemspecParser.parse(inputStream, includeRuntimeDependencies, includeDevelopmentDependencies);
            final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.RUBYGEMS, dependencyGraph).build();

            return new Extraction.Builder().codeLocations(codeLocation).build();
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
