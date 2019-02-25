package com.synopsys.integration.detectable.detectables.maven.parsing;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

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
import com.synopsys.integration.detectable.detectables.maven.parsing.parse.PomXmlParser;

public class PomXmlDetectable extends Detectable {
    public static final String POM_XML_FILENAME = "pom.xml";

    private final FileFinder fileFinder;
    private final PomXmlParser pomXmlParser;

    private File pomXmlFile;

    public PomXmlDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final PomXmlParser pomXmlParser) {
        super(environment, "pom.xml", "MAVEN");
        this.fileFinder = fileFinder;
        this.pomXmlParser = pomXmlParser;
    }

    @Override
    public DetectableResult applicable() {
        pomXmlFile = fileFinder.findFile(environment.getDirectory(), POM_XML_FILENAME);

        if (pomXmlFile == null) {
            return new FileNotFoundDetectableResult(POM_XML_FILENAME);
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
            final InputStream pomXmlInputStream = new FileInputStream(pomXmlFile);
            final Optional<DependencyGraph> dependencyGraph = pomXmlParser.parse(pomXmlInputStream);

            if (dependencyGraph.isPresent()) {
                final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.MAVEN, dependencyGraph.get()).build();
                return new Extraction.Builder().codeLocations(codeLocation).build();
            } else {
                return new Extraction.Builder().failure(String.format("Failed to extract dependencies from %s", POM_XML_FILENAME)).build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
