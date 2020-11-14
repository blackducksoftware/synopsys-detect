package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNode;

public class ConanInfoParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoNodeParser conanInfoNodeParser;

    public ConanInfoParser(ConanInfoNodeParser conanInfoNodeParser) {
        this.conanInfoNodeParser = conanInfoNodeParser;
    }

    public ConanParseResult generateCodeLocation(String conanInfoOutput) {
        List<ConanGraphNode> graphNodes = new ArrayList<>();
        List<String> conanInfoOutputLines = Arrays.asList(conanInfoOutput.split("\n"));
        int lineIndex = 0;
        while (lineIndex < conanInfoOutputLines.size()) {
            String line = conanInfoOutputLines.get(lineIndex);
            System.out.println(line);
            int indentDepth = conanInfoNodeParser.measureIndentDepth(line);
            if (lineIndex > 0 && indentDepth > 0) {
                ConanInfoNodeParseResult nodeParseResult = conanInfoNodeParser.parseNode(conanInfoOutputLines, lineIndex - 1);
                graphNodes.add(nodeParseResult.getConanGraphNode());
                lineIndex = nodeParseResult.getLastParsedLineIndex();
            }
            lineIndex++;
        }
        System.out.printf("Reached end of Conan info output\n");

        Optional<ConanGraphNode> rootNode = graphNodes.stream().filter(ConanGraphNode::isRootNode).findFirst();
        String projectName = "Unknown";
        String projectVersion = "Unknown";
        if (rootNode.isPresent()) {
            String ref = rootNode.get().getRef();
            String[] refParts = ref.split("\\s");
            if (refParts.length == 2) {
                if (refParts[1].startsWith("(") && refParts[1].endsWith(")")) {
                    String nameVersion = refParts[1].substring(1, refParts[1].length() - 1);
                    String[] projectVersionParts = nameVersion.split("/");
                    if (projectVersionParts.length == 2) {
                        projectName = projectVersionParts[0];
                        projectVersion = projectVersionParts[1];
                    }
                }
            } else {
                projectName = ref;
                projectVersion = "Unknown";
            }
        }
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
        return new ConanParseResult(projectName, projectVersion, codeLocation);
    }
}
