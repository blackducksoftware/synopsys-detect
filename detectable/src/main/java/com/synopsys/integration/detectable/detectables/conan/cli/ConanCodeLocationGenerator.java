package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanGraphNode;
import com.synopsys.integration.detectable.detectables.conan.cli.graph.ConanInfoNode;
import com.synopsys.integration.exception.IntegrationException;

public class ConanCodeLocationGenerator {
    
    @NotNull
    public ConanDetectableResult generateCodeLocationFromNodeMap(final boolean includeBuildDependencies, final Map<String, ConanInfoNode> nodes) throws IntegrationException {
        Optional<ConanInfoNode> rootNode = getRoot(nodes.values());
        if (!rootNode.isPresent()) {
            throw new IntegrationException("No root node found in 'conan info' output");
        }
        ConanGraphNode rootGraphNode = new ConanGraphNode(rootNode.get());
        populateGraphUnderNode(rootGraphNode, nodes, includeBuildDependencies);
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        CodeLocation codeLocation = generateCodeLocationFromConanInfoOutput(dependencyGraph, rootGraphNode);
        ConanDetectableResult result = new ConanDetectableResult(rootGraphNode.getConanInfoNode().getName(), rootGraphNode.getConanInfoNode().getVersion(), codeLocation);
        return result;
    }

    private void populateGraphUnderNode(ConanGraphNode curGraphNode, Map<String, ConanInfoNode> graphNodes, boolean includeBuildDependencies) throws IntegrationException {
        Set<String> dependencyRefs = new HashSet<>(curGraphNode.getConanInfoNode().getRequiresRefs());
        if (includeBuildDependencies) {
            dependencyRefs.addAll(curGraphNode.getConanInfoNode().getBuildRequiresRefs());
        }
        for (String childRef : dependencyRefs) {
            ConanInfoNode childNode = graphNodes.get(childRef);
            if (childNode == null) {
                throw new IntegrationException(String.format("%s requires non-existent node %s", curGraphNode.getConanInfoNode().getRef(), childRef));
            }
            ConanGraphNode childGraphNode = new ConanGraphNode(childNode);
            populateGraphUnderNode(childGraphNode, graphNodes, includeBuildDependencies);
            curGraphNode.addChild(childGraphNode);
        }
    }

    @NotNull
    private CodeLocation generateCodeLocationFromConanInfoOutput(MutableMapDependencyGraph dependencyGraph, ConanGraphNode rootNode) {
        addNodeChildrenUnderNode(dependencyGraph, 0, rootNode, null);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return codeLocation;
    }

    private void addNodeChildrenUnderNode(MutableMapDependencyGraph dependencyGraph, int depth, ConanGraphNode currentNode, Dependency currentDep) {
        Consumer<Dependency> childAdder;
        if (depth == 0) {
            childAdder = childDep -> dependencyGraph.addChildToRoot(childDep);
        } else {
            childAdder = childDep -> dependencyGraph.addChildWithParent(childDep, currentDep);
        }
        for (ConanGraphNode childNode : currentNode.getChildren()) {
            Dependency childDep = generateDependency(childNode);
            childAdder.accept(childDep);
            addNodeChildrenUnderNode(dependencyGraph, depth + 1, childNode, childDep);
        }
    }

    @NotNull
    private Dependency generateDependency(ConanGraphNode graphNode) {
        // TODO eventually should use ExternalIdFactory; doubt it can handle these IDs
        //ExternalIdFactory f;
        // The KB supports two forms:
        // <name>/<version>@<user>/<channel>#<recipe_revision>
        // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
        // TODO generate forge once
        ExternalId externalId = new ExternalId(new Forge("/", "conan"));
        externalId.setName(graphNode.getConanInfoNode().getName());
        externalId.setVersion(generateExternalIdVersionString(graphNode.getConanInfoNode()));
        Dependency dep = new Dependency(graphNode.getConanInfoNode().getName(), graphNode.getConanInfoNode().getVersion(), externalId);
        return dep;
    }

    @NotNull
    private Optional<ConanInfoNode> getRoot(Collection<ConanInfoNode> graphNodes) {
        Optional<ConanInfoNode> rootNode = graphNodes.stream().filter(ConanInfoNode::isRootNode).findFirst();
        return rootNode;
    }

    private String generateExternalIdVersionString(ConanInfoNode node) {
        String externalId;
        if (hasValue(node.getRecipeRevision()) && !hasValue(node.getPackageRevision())) {
            // generate short form
            // <name>/<version>@<user>/<channel>#<recipe_revision>
            externalId = String.format("%s@%s/%s#%s",
                node.getVersion(),
                node.getUser() == null ? "_" : node.getUser(),
                node.getChannel() == null ? "_" : node.getChannel(),
                node.getRecipeRevision());
        } else {
            // generate long form
            // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
            externalId = String.format("%s@%s/%s#%s:%s#%s",
                node.getVersion(),
                node.getUser() == null ? "_" : node.getUser(),
                node.getChannel() == null ? "_" : node.getChannel(),
                node.getRecipeRevision() == null ? "0" : node.getRecipeRevision(),
                node.getPackageId() == null ? "0" : node.getPackageId(),
                node.getPackageRevision() == null ? "0" : node.getPackageRevision());
        }
        return externalId;
    }

    private boolean hasValue(String value) {
        if ((value == null) || ("None".equals(value))) {
            return false;
        }
        return true;
    }

}
