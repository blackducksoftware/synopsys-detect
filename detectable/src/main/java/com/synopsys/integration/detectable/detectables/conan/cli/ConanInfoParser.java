package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConanParseResult generateCodeLocation(String conanInfoOutput) {

        List<String> conanInfoOutputLines = Arrays.asList(conanInfoOutput.split("\n"));
        int lineIndex = 0;
        while (lineIndex < conanInfoOutputLines.size()) {
            String line = conanInfoOutputLines.get(lineIndex);
            System.out.println(line);
            int indentDepth = measureIndentDepth(line);
            if (lineIndex > 0 && indentDepth > 0) {
                int nodeLastLineIndex = parseNode(conanInfoOutputLines, lineIndex - 1);
                lineIndex = nodeLastLineIndex;
            }
            lineIndex++;
        }
        System.out.printf("Reached end of Conan info output\n");

        // TODO eventually should use ExternalIdFactory; doubt it can handle these IDs
        //ExternalIdFactory f;
        List<Dependency> dependencies = new ArrayList<>();
        ExternalId externalId = new ExternalId(new Forge("/", "conan"));
        externalId.setName("tbdpkg");
        externalId.setVersion("1.0@user/channel#rrev:pkgid#pkgrev");
        Dependency dep = new Dependency("tbdpkg", "tbdpkgversion", externalId);
        dependencies.add(dep);
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return new ConanParseResult("tbdproject", "tbdprojectversion", codeLocation);
    }

    private int parseNode(List<String> lines, int nodeStartIndex) {
        String nodeHeaderLine = lines.get(nodeStartIndex);
        if (nodeHeaderLine.matches(".+ (.+/.+)")) {
            System.out.printf("Line '%s' has name (name/version)\n", nodeHeaderLine);
        } else {
            System.out.printf("Line '%s' has a different format\n", nodeHeaderLine);
        }
        for (int i = nodeStartIndex + 1; i < lines.size(); i++) {
            String nodeBodyLine = lines.get(i);
            int indentDepth = measureIndentDepth(nodeBodyLine);
            if (indentDepth > 0) {
                System.out.printf("Slewing past node line '%s'\n", nodeBodyLine);
            } else {
                System.out.printf("Reached end of node\n");
                return i - 1;
            }
        }
        System.out.printf("Reached end of output\n");
        return lines.size() - 1;
    }

    private int measureIndentDepth(String line) {
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
