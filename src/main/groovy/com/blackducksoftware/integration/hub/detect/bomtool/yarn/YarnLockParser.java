package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer;
import com.blackducksoftware.integration.hub.detect.nameversion.builder.LinkedNameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.builder.NameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.LinkMetadata;

@Component
public class YarnLockParser extends BaseYarnParser {

    @Autowired
    private NameVersionNodeTransformer nameVersionNodeTransformer;

    public DependencyGraph parseYarnLock(final List<String> yarnLockText) {
        NameVersionNode rootNode = new NameVersionNode();
        rootNode.setName(String.format("detectRootNode - %s", UUID.randomUUID()));
        LinkedNameVersionNodeBuilder nameVersionLinkNodeBuilder = new LinkedNameVersionNodeBuilder(rootNode);

        NameVersionNode currentNode = null;
        boolean dependenciesStarted = false;
        for (String line : yarnLockText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            String trimmedLine = line.trim();

            if (trimmedLine.startsWith("#")) {
                continue;
            }

            int level = getLineLevel(line);
            if (level == 0) {
                currentNode = lineToNameVersionNode(nameVersionLinkNodeBuilder, rootNode, trimmedLine);
                dependenciesStarted = false;
                continue;
            }

            if (level == 1 && trimmedLine.startsWith("version")) {
                String fieldName = trimmedLine.split(" ")[0];
                currentNode.setVersion(trimmedLine.substring(fieldName.length()).replaceAll("\"", "").trim());
                continue;
            }

            if (level == 1 && trimmedLine == "dependencies:") {
                dependenciesStarted = true;
                continue;
            }

            if (level == 2 && dependenciesStarted) {
                NameVersionNode dependency = dependencyLineToNameVersionNode(line);
                nameVersionLinkNodeBuilder.addChildNodeToParent(dependency, currentNode);
                continue;
            }
        }

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        nameVersionLinkNodeBuilder.build().getChildren().stream().forEach(nameVersionNode -> {
            Dependency root = nameVersionNodeTransformer.addNameVersionNodeToDependencyGraph(graph, Forge.NPM, nameVersionNode);
            graph.addChildToRoot(root);
        });

        return graph;
    }

    // Example: "mime-types@^2.1.12" becomes "mime-types"
    private String getNameFromFuzzyName(final String fuzzyName) {
        String cleanName = fuzzyName.replace("\"", "");
        String version = cleanName.split("@")[-1];
        String name = cleanName.substring(0, cleanName.indexOf(version) - 2).trim();

        return name;
    }

    private NameVersionNode dependencyLineToNameVersionNode(final String line) {
        final NameVersionNode nameVersionNode = new NameVersionNode();
        nameVersionNode.setName(line.trim().replaceFirst(" ", "@").replace("\"", ""));

        return nameVersionNode;
    }

    private NameVersionNode lineToNameVersionNode(final NameVersionNodeBuilder nameVersionNodeBuilder, final NameVersionNode root, final String line) {
        String cleanLine = line.replace("\"", "").replace(":", "");
        List<String> fuzzyNames = Arrays.asList(cleanLine.split(",")).stream().map(name -> name.trim()).collect(Collectors.toList());

        if (fuzzyNames.isEmpty()) {
            return null;
        }

        final NameVersionNode linkedNameVersionNode = new NameVersionNode();
        linkedNameVersionNode.setName(getNameFromFuzzyName(fuzzyNames.get(0)));

        fuzzyNames.stream().forEach(fuzzyName -> {
            NameVersionNode nameVersionLinkNode = new NameVersionNode();
            nameVersionLinkNode.setName(fuzzyName);

            LinkMetadata linkMetadata = new LinkMetadata();
            linkMetadata.setLinkNode(linkedNameVersionNode);
            nameVersionLinkNode.setMetadata(linkMetadata);
            nameVersionNodeBuilder.addChildNodeToParent(nameVersionLinkNode, root);
        });

        return linkedNameVersionNode;
    }

}
