package com.synopsys.integration.detectable.detectables.conan.lockfile.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model.ConanLockfileData;
import com.synopsys.integration.detectable.detectables.conan.lockfile.parser.model.ConanLockfileNode;

public class ConanLockfileParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Gson gson;
    private final ConanCodeLocationGenerator conanCodeLocationGenerator;
    private final ExternalIdFactory externalIdFactory;

    public ConanLockfileParser(Gson gson, ConanCodeLocationGenerator conanCodeLocationGenerator, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.conanCodeLocationGenerator = conanCodeLocationGenerator;
        this.externalIdFactory = externalIdFactory;
    }

    public ConanDetectableResult generateCodeLocationFromConanLockfileContents(String conanLockfileContents) throws DetectableException {
        logger.trace("Parsing conan lockfile contents:\n{}", conanLockfileContents);
        Map<Integer, ConanNode<Integer>> indexedNodeMap = generateIndexedNodeMap(conanLockfileContents);
        // The lockfile references nodes by (integer) index; generator needs nodes referenced by names (component references)
        Map<String, ConanNode<String>> namedNodeMap = convertToNamedNodeMap(indexedNodeMap);
        return conanCodeLocationGenerator.generateCodeLocationFromNodeMap(externalIdFactory, namedNodeMap);
    }

    private Map<Integer, ConanNode<Integer>> generateIndexedNodeMap(String conanLockfileContents) {
        Map<Integer, ConanNode<Integer>> graphNodes = new HashMap<>();
        ConanLockfileData conanLockfileData = gson.fromJson(conanLockfileContents, ConanLockfileData.class);
        logger.trace("conanLockfileData: {}", conanLockfileData);
        if (!conanLockfileData.getConanLockfileGraph().isRevisionsEnabled()) {
            logger.warn("The Conan revisions feature is not enabled, which will significantly reduce Black Duck's ability to identify dependencies");
        } else {
            logger.debug("The Conan revisions feature is enabled");
        }
        for (Map.Entry<Integer, ConanLockfileNode> entry : conanLockfileData.getConanLockfileGraph().getNodeMap().entrySet()) {
            logger.trace("{}: {}:{}#{}", entry.getKey(),
                entry.getValue().getRef().orElse("?"),
                entry.getValue().getPackageId().orElse("?"),
                entry.getValue().getPackageRevision().orElse("?")
            );
            ConanLockfileNode lockfileNode = entry.getValue();
            Optional<ConanNode<Integer>> conanNode = generateConanNode(entry.getKey(), lockfileNode);
            conanNode.ifPresent(node -> graphNodes.put(entry.getKey(), node));
        }
        logger.trace("ConanNode map: {}", graphNodes);
        return graphNodes;
    }

    private Optional<ConanNode<Integer>> generateConanNode(Integer nodeKey, ConanLockfileNode lockfileNode) {
        ConanNodeBuilder<Integer> nodeBuilder = new ConanNodeBuilder<>();
        if (nodeKey == 0) {
            nodeBuilder.forceRootNode();
        }
        setRefAndDerivedFields(nodeBuilder, lockfileNode.getRef().orElse(null));
        nodeBuilder.setPath(lockfileNode.getPath().orElse(null));
        lockfileNode.getPackageId().ifPresent(nodeBuilder::setPackageId);
        lockfileNode.getPackageRevision().ifPresent(nodeBuilder::setPackageRevision);
        lockfileNode.getRequires().ifPresent(requiresList -> requiresList.forEach(nodeBuilder::addRequiresRef));
        lockfileNode.getBuildRequires().ifPresent(buildRequiresList -> buildRequiresList.forEach(nodeBuilder::addBuildRequiresRef));
        return nodeBuilder.build();
    }

    private Map<String, ConanNode<String>> convertToNamedNodeMap(Map<Integer, ConanNode<Integer>> numberedNodeMap) throws DetectableException {
        Map<String, ConanNode<String>> namedNodeMap = new HashMap<>(numberedNodeMap.size());
        for (Map.Entry<Integer, ConanNode<Integer>> entry : numberedNodeMap.entrySet()) {
            ConanNode<Integer> numberedNode = entry.getValue();
            ConanNodeBuilder<String> namedNodeBuilder = new ConanNodeBuilder<>(numberedNode);
            addRefsForGivenIndices(numberedNodeMap, numberedNode.getRequiresRefs().orElse(new ArrayList<>(0)), namedNodeBuilder::addRequiresRef);
            addRefsForGivenIndices(numberedNodeMap, numberedNode.getBuildRequiresRefs().orElse(new ArrayList<>(0)), namedNodeBuilder::addBuildRequiresRef);
            Optional<ConanNode<String>> namedNode = namedNodeBuilder.build();
            if (!namedNode.isPresent()) {
                throw new DetectableException(String.format("Unable to create a named node from numbered noded %s", numberedNode));
            }
            namedNodeMap.put(namedNode.get().getRef(), namedNode.get());
        }
        return namedNodeMap;
    }

    // Translate each of the given map indices to the corresponding dependency ref,
    // and call the given refAdder to put it where it belongs
    private void addRefsForGivenIndices(Map<Integer, ConanNode<Integer>> numberedNodeMap, List<Integer> indices, Consumer<String> refAdder) {
        indices.stream()
            .map(index -> numberedNodeMap.get(index).getRef())
            .forEach(refAdder);
    }

    private void setRefAndDerivedFields(ConanNodeBuilder<Integer> nodeBuilder, String ref) {
        if (StringUtils.isBlank(ref)) {
            return;
        }
        ref = ref.trim();
        StringTokenizer tokenizer = new StringTokenizer(ref, "@/#");
        if (!ref.startsWith("conanfile.")) {
            if (tokenizer.hasMoreTokens()) {
                nodeBuilder.setName(tokenizer.nextToken());
            }
            if (tokenizer.hasMoreTokens()) {
                nodeBuilder.setVersion(tokenizer.nextToken());
            }
            if (ref.contains("@")) {
                nodeBuilder.setUser(tokenizer.nextToken());
                nodeBuilder.setChannel(tokenizer.nextToken());
            }
            if (ref.contains("#")) {
                nodeBuilder.setRecipeRevision(tokenizer.nextToken());
            }
        }
        nodeBuilder.setRef(ref);
    }
}
