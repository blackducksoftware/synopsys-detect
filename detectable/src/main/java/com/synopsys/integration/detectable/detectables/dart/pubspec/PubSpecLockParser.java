package com.synopsys.integration.detectable.detectables.dart.pubspec;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.util.DependencyCreator;

public class PubSpecLockParser {

    private static final String PACKAGES_SECTION_HEADER = "packages:";
    private static final String DESCRIPTION_SECTION_HEADER = "description:";
    private static final String NAME_LINE_KEY = "name:";
    private static final String VERSION_LINE_KEY = "version:";

    public DependencyGraph parse(List<String> pubSpecLockLines) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        //TODO- do we even need these booleans? will there be cases we run into name and version keys that we don't want to read/don't correspond to a dependency?
        boolean inPackages = false;
        boolean inDescription = false;

        Optional<String> dependencyName = Optional.empty();

        for (String line : pubSpecLockLines) {
            String trimmedLine = StringUtils.trimToEmpty(line);

            if (StringUtils.isBlank(trimmedLine)) {
                continue;
            } else if (trimmedLine.equals(PACKAGES_SECTION_HEADER)) {
                inPackages = true;
            } else if (inPackages && trimmedLine.equals(DESCRIPTION_SECTION_HEADER)) {
                inDescription = true;
            } else if (inDescription && trimmedLine.startsWith(NAME_LINE_KEY)) {
                dependencyName = parseValueFromLine(trimmedLine, NAME_LINE_KEY);
            } else if (inPackages && trimmedLine.startsWith(VERSION_LINE_KEY)) {
                Optional<String> dependencyVersion = parseValueFromLine(trimmedLine, VERSION_LINE_KEY);
                if (dependencyName.isPresent() && dependencyVersion.isPresent()) {
                    dependencyGraph.addChildToRoot(DependencyCreator.nameVersion(Forge.DART, dependencyName.get(), dependencyVersion.get()));
                    // After process dependency, reset name and version variables
                    dependencyName = Optional.empty();
                }
            }
        }

        return dependencyGraph;
    }

    private Optional<String> parseValueFromLine(String line, String keyToken) {
        String[] linePieces = line.split(" ");
        if (linePieces.length != 2 || !linePieces[0].equals(keyToken)) {
            // line should be <keyToken> <value>
            return Optional.empty();
        }
        String value = StringUtils.strip(linePieces[1], "\""); //remove quotes
        return Optional.of(value);
    }
}
