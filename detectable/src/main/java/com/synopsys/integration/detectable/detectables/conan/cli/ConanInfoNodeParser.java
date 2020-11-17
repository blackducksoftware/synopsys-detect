package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNodeBuilder;

public class ConanInfoNodeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConanInfoNodeParseResult parseNode(List<String> conanInfoOutputLines, int nodeStartIndex) {
        String nodeHeaderLine = conanInfoOutputLines.get(nodeStartIndex);
        logger.info(String.format("Parsing: %s", nodeHeaderLine));
        ConanGraphNodeBuilder nodeBuilder = new ConanGraphNodeBuilder();
        nodeBuilder.setRef(nodeHeaderLine);
        int bodyLineCount = 0;
        for (int i = nodeStartIndex + 1; i < conanInfoOutputLines.size(); i++) {
            String nodeBodyLine = conanInfoOutputLines.get(i);
            int indentDepth = measureIndentDepth(nodeBodyLine);
            if (indentDepth == 0) {
                if (bodyLineCount == 0) {
                    System.out.printf("This wasn't a node\n");
                    return new ConanInfoNodeParseResult(nodeStartIndex);
                } else {
                    System.out.printf("Reached end of node\n");
                    return new ConanInfoNodeParseResult(i - 1, nodeBuilder.build());
                }
            }
            bodyLineCount++;
            i = parseBodyElement(conanInfoOutputLines, i, nodeBuilder);
        }
        System.out.printf("Reached end of conan info output\n");
        return new ConanInfoNodeParseResult(conanInfoOutputLines.size() - 1, nodeBuilder.build());
    }

    private int parseBodyElement(List<String> conanInfoOutputLines, int bodyElementLineIndex, ConanGraphNodeBuilder nodeBuilder) {
        StringTokenizer stringTokenizer = new StringTokenizer(conanInfoOutputLines.get(bodyElementLineIndex).trim(), ":");
        String key = stringTokenizer.nextToken();
        if (stringTokenizer.hasMoreTokens()) {
            String value = stringTokenizer.nextToken().trim();
            if ("ID".equals(key)) {
                System.out.printf("Found Package ID: %s\n", value);
                nodeBuilder.setPackageId(value);
            } else if ("Revision".equals(key)) {
                System.out.printf("Found Recipe Revision: %s\n", value);
                nodeBuilder.setRecipeRevision(value);
            } else if ("Package revision".equals(key)) {
                System.out.printf("Found Package Revision: %s\n", value);
                nodeBuilder.setPackageRevision(value);
            }
        }
        return bodyElementLineIndex;
    }

    private int measureIndentDepth(String line) {
        if (StringUtils.isBlank(line)) {
            return 0;
        }
        int leadingSpaceCount = countLeadingSpaces(line);
        if ((leadingSpaceCount % 4) != 0) {
            logger.warn(String.format("Leading space count for '%s' is %d; expected it to be divisible by 4",
                line, leadingSpaceCount));
        }
        return countLeadingSpaces(line) / 4;
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
