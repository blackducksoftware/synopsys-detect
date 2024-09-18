package com.blackduck.integration.detectable.detectables.docker.parser;

import java.util.Optional;

import com.blackduck.integration.detectable.detectables.docker.model.DockerInspectorResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DockerInspectorResultsFileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;

    public DockerInspectorResultsFileParser(Gson gson) {
        this.gson = gson;
    }

    public Optional<DockerInspectorResults> parse(String resultsFileContents) {
        try {
            DockerInspectorResults results = gson.fromJson(resultsFileContents, DockerInspectorResults.class);
            return Optional.of(results);
        } catch (JsonSyntaxException e) {
            logger.warn("Failed to parse results file from run of Docker Inspector; results file contents: {}", resultsFileContents);
            return Optional.empty();
        }
    }
}
