package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.util.NameVersion;

public class GoGraphParser {
    private static final String INCOMPATIBLE_SUFFIX = "+incompatible";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<GoGraphRelationship> parseRelationshipsFromGoModGraph(List<String> goModGraphOutput) {
        List<GoGraphRelationship> goGraphRelationships = new LinkedList<>();
        for (String line : goModGraphOutput) {
            //example: github.com/gomods/athens cloud.google.com/go@v0.26.0
            line = removeIncompatibleSuffix(line);
            Optional<GoGraphRelationship> goGraphRelationship = parseLine(line);
            goGraphRelationship.ifPresent(goGraphRelationships::add);
        }

        return goGraphRelationships;
    }

    // TODO: Since Detect is no longer getting the version from `go mod graph`, is it still possible to have an incompatible suffix?
    private String removeIncompatibleSuffix(String line) {
        if (line.endsWith(INCOMPATIBLE_SUFFIX)) {
            // Trim incompatible suffix so that KB can match component
            line = line.substring(0, line.length() - INCOMPATIBLE_SUFFIX.length());
        }
        return line;
    }

    private Optional<GoGraphRelationship> parseLine(String line) {
        String[] parts = line.split(" ");
        if (parts.length != 2) {
            logger.warn("Unknown graph line format: {}", line);
            return Optional.empty();
        }

        NameVersion parent = extractNameVersion(parts[0]);
        NameVersion child = extractNameVersion(parts[1]);

        return Optional.of(new GoGraphRelationship(parent, child));
    }

    private NameVersion extractNameVersion(String dependency) {
        if (dependency.contains("@")) {
            String[] parts = dependency.split("@");
            if (parts.length != 2) {
                logger.warn("Unknown graph dependency format, using entire entry as name: {}", dependency);
                return new NameVersion(dependency, null);
            } else {
                String name = parts[0];
                String version = parts[1];
                if (version.contains("-")) { //The KB only supports the git hash, unfortunately we must strip out the rest. This gets just the commit has from a go.mod psuedo version.
                    String[] versionPieces = version.split("-");
                    version = versionPieces[versionPieces.length - 1];
                }
                return new NameVersion(name, version);
            }
        } else {
            return new NameVersion(dependency, null);
        }
    }
}
