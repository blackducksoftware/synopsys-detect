package com.synopsys.integration.detectable.detectables.maven.cli;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.executable.resolver.MavenResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Maven CLI", language = "various", forge = "Maven Central", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: pom.xml. Executable: mvnw or mvn.")
public class MavenPomDetectable extends Detectable {
    public static final String POM_FILENAME = "pom.xml";

    private final FileFinder fileFinder;
    private final MavenResolver mavenResolver;
    private final MavenCliExtractor mavenCliExtractor;
    private final MavenCliExtractorOptions mavenCliExtractorOptions;

    private ExecutableTarget mavenExe;

    public MavenPomDetectable(
        DetectableEnvironment environment,
        FileFinder fileFinder,
        MavenResolver mavenResolver,
        MavenCliExtractor mavenCliExtractor,
        MavenCliExtractorOptions mavenCliExtractorOptions
    ) {
        super(environment);
        this.fileFinder = fileFinder;
        this.mavenResolver = mavenResolver;
        this.mavenCliExtractor = mavenCliExtractor;
        this.mavenCliExtractorOptions = mavenCliExtractorOptions; //TODO: Should this be wrapped in a detectables options? -jp
    }

    @Override
    public DetectableResult applicable() {
        Requirements requirements = new Requirements(fileFinder, environment);
        requirements.file(POM_FILENAME);
        return requirements.result();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        Requirements requirements = new Requirements(fileFinder, environment);
        mavenExe = requirements.executable(() -> mavenResolver.resolveMaven(environment), "maven");
        return requirements.result();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) throws ExecutableFailedException {
        return mavenCliExtractor.extract(environment.getDirectory(), mavenExe, mavenCliExtractorOptions);
    }

}
