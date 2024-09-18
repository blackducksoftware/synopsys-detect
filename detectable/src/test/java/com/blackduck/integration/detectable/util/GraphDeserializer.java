package com.blackduck.integration.detectable.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(GraphDeserializer.class);

    static ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    static Map<String, Forge> knownForges = Forge.getKnownForges();

    public static DependencyGraph deserialize(String text) {
        DependencyGraph graph = new BasicDependencyGraph();
        //three sections
        List<String> lines = Arrays.asList(text.split("\n"));
        int currentLineIndex = 0;
        String currentLine = lines.get(0);

        while (!currentLine.startsWith("Root Dependencies")) {
            currentLineIndex++;
            currentLine = lines.get(currentLineIndex);

        }

        while (!currentLine.startsWith("Relationships")) { //In Root
            currentLineIndex++;
            currentLine = lines.get(currentLineIndex);

            if (!currentLine.startsWith("Relationships")) {
                graph.addDirectDependency(make(currentLine));
            }

        }

        Dependency parent = null;
        while (currentLineIndex + 1 < lines.size()) { //In Relationships
            currentLineIndex++;
            currentLine = lines.get(currentLineIndex);

            if (currentLine.startsWith("\t\t")) {
                graph.addChildWithParent(make(currentLine), parent);
            } else if (currentLine.startsWith("\t")) {
                parent = make(currentLine);
            }
        }

        return graph;
    }

    private static Dependency make(String line) {
        String[] pieces = line.trim().split(",");
        if (pieces.length <= 3) {
            logger.debug("helpy!");
        }
        String name = unescape(pieces[0]);
        String version = unescape(pieces[1]);
        Forge forge = knownForges.get(unescape(pieces[2]));
        ExternalId externalId = externalIdFromString(forge, Arrays.stream(pieces).skip(3).collect(Collectors.toList()));
        return new Dependency(name, version, externalId);
    }

    private static String unescape(String target) {
        return target.replaceAll("%commma%", ",");
    }

    private static ExternalId externalIdFromString(Forge forge, List<String> text) {
        String[] pieces = text.stream()
            .map(GraphDeserializer::unescape)
            .collect(Collectors.toList())
            .toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        return externalIdFactory.createModuleNamesExternalId(forge, pieces);
    }
}
