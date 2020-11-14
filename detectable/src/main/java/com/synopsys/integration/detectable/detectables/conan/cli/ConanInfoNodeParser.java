package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNodeBuilder;

public class ConanInfoNodeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConanInfoNodeParseResult parseNode(List<String> conanInfoOutputLines, int nodeStartIndex) {
        ConanGraphNodeBuilder nodeBuilder = new ConanGraphNodeBuilder();
        String nodeHeaderLine = conanInfoOutputLines.get(nodeStartIndex);
        nodeBuilder.setRef(nodeHeaderLine);
        for (int i = nodeStartIndex + 1; i < conanInfoOutputLines.size(); i++) {
            String nodeBodyLine = conanInfoOutputLines.get(i);
            int indentDepth = measureIndentDepth(nodeBodyLine);
            if ((indentDepth == 1) && (nodeBodyLine.trim().startsWith("ID: "))) {
                i = parseId(conanInfoOutputLines, i, nodeBuilder);
            } else if (indentDepth > 0) {
                System.out.printf("Slewing past node line '%s'\n", nodeBodyLine);
            } else {
                System.out.printf("Reached end of node\n");
                return new ConanInfoNodeParseResult(i - 1, nodeBuilder.build());
            }
        }
        System.out.printf("Reached end of conan info output\n");
        return new ConanInfoNodeParseResult(conanInfoOutputLines.size() - 1, nodeBuilder.build());
    }

    public int measureIndentDepth(String line) {
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % 4) != 0) {
            logger.warn(String.format("Leading space count for '%s' is %d; expected it to be divisible by 4",
                line, leadingSpaceCount));
        }
        return countLeadingSpaces(line) / 4;
    }

    private int parseId(List<String> conanInfoOutputLines, int idIndex, ConanGraphNodeBuilder builder) {
        String idLine = conanInfoOutputLines.get(idIndex).trim();
        String[] idLineParts = idLine.split("\\s");
        builder.setPackageId(idLineParts[1]);
        return idIndex;
    }

    private int countLeadingSpaces(String line) {
        int leadingSpaceCount = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                leadingSpaceCount++;
            } else if (line.charAt(i) == '\t') {
                leadingSpaceCount += 4;
            } else {
                break;
            }
        }
        return leadingSpaceCount;
    }
}
