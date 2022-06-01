package com.synopsys.integration.detectable.detectables.rubygems.gemlock;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class GemlockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GemlockExtractor(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Extraction extract(File gemlock) {
        try {
            List<String> gemlockText = Files.readAllLines(gemlock.toPath(), StandardCharsets.UTF_8);

            GemlockParser gemlockParser = new GemlockParser(externalIdFactory);
            DependencyGraph dependencyGraph = gemlockParser.parseProjectDependencies(gemlockText);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
