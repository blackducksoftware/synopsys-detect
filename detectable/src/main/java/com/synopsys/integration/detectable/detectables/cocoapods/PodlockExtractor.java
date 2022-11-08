package com.synopsys.integration.detectable.detectables.cocoapods;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.cocoapods.parser.PodlockParser;
import com.synopsys.integration.detectable.extraction.Extraction;

public class PodlockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PodlockParser podlockParser;

    public PodlockExtractor(PodlockParser podlockParser) {
        this.podlockParser = podlockParser;
    }

    public Extraction extract(File podlock) {
        String podLockText;
        try {
            logger.trace(String.format("Reading from the pod lock file %s", podlock.getAbsolutePath()));
            podLockText = FileUtils.readFileToString(podlock, StandardCharsets.UTF_8);
            logger.trace("Finished reading from the pod lock file.");
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        DependencyGraph dependencyGraph;
        try {
            logger.trace("Attempting to create the dependency graph from the pod lock file.");
            dependencyGraph = podlockParser.extractDependencyGraph(podLockText);
            logger.trace("Finished creating the dependency graph from the pod lock file.");
        } catch (IOException | MissingExternalIdException e) {
            return new Extraction.Builder().exception(e).build();
        }

        CodeLocation codeLocation = new CodeLocation(dependencyGraph);

        return new Extraction.Builder().success(codeLocation).build();
    }

}
