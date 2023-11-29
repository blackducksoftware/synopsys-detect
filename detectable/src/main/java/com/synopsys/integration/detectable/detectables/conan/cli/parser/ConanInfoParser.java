package com.synopsys.integration.detectable.detectables.conan.cli.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoNodeParser conanInfoNodeParser;
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;
    private final ExternalIdFactory externalIdFactory;

    public ConanInfoParser(ConanInfoNodeParser conanInfoNodeParser, ConanCodeLocationGenerator conanCodeLocationGenerator, ExternalIdFactory externalIdFactory) {
        this.conanInfoNodeParser = conanInfoNodeParser;
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
        this.externalIdFactory = externalIdFactory;
    }

    public ConanDetectableResult generateCodeLocationFromConanInfoOutput(String conanInfoOutput) throws DetectableException {
        Map<String, ConanNode<String>> nodeMap = generateNodeMap(conanInfoOutput);
        return conanCodeLocationGenerator.generateCodeLocationFromNodeMap(externalIdFactory, nodeMap);
    }

    /*
     * Conan info command output: some (irrelevant to us) log messages, followed by a list of nodes.
     * A node looks like this:
     * ref:
     *     key1: value
     *     key2:
     *         list of values
     *     ....
     */
    private Map<String, ConanNode<String>> generateNodeMap(String conanInfoOutput) {
        Map<String, ConanNode<String>> graphNodes = new HashMap<>();
        List<String> conanInfoOutputLines = Arrays.asList(conanInfoOutput.split("\n"));
        int lineIndex = 0;
        while (lineIndex < conanInfoOutputLines.size()) {
            String line = conanInfoOutputLines.get(lineIndex);
            logger.trace("Parsing line: {}: {}", lineIndex + 1, line);
            // Parse the entire node
            ConanInfoNodeParseResult nodeParseResult = conanInfoNodeParser.parseNode(conanInfoOutputLines, lineIndex);
            // Some lines that look like the start of nodes aren't actually the start of nodes, and don't result in a node
            nodeParseResult.getConanNode().ifPresent(node -> graphNodes.put(node.getRef(), node));
            lineIndex = nodeParseResult.getLastParsedLineIndex();
            lineIndex++;
        }
        logger.trace("Reached end of Conan info output");
        return graphNodes;
    }
}
