package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class PubSpecExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PubSpecLockParser pubSpecLockParser;

    public PubSpecExtractor(PubSpecLockParser pubSpecLockParser) {
        this.pubSpecLockParser = pubSpecLockParser;
    }

    public Extraction extract(File pubSpecLockFile) {
        try {
            List<String> pubSpecLockLines = Files.readAllLines(pubSpecLockFile.toPath(), StandardCharsets.UTF_8);
            logger.debug(String.join(System.lineSeparator(), pubSpecLockLines));

            DependencyGraph dependencyGraph = pubSpecLockParser.parse(pubSpecLockLines);

            CodeLocation codeLocation = new CodeLocation(dependencyGraph);
            return new Extraction.Builder().success(codeLocation).build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
