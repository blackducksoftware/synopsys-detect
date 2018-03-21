package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer;
import com.blackducksoftware.integration.hub.detect.nameversion.NodeMetadata;
import com.blackducksoftware.integration.hub.detect.nameversion.builder.NameVersionNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.builder.SubcomponentNodeBuilder;
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.SubcomponentMetadata;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
public class CocoapodsPackager {
    final List<String> fuzzyVersionIdentifiers = new ArrayList<>(Arrays.asList(">", "<", "~>", "="));

    @Autowired
    private NameVersionNodeTransformer nameVersionNodeTransformer;

    @Autowired
    private DetectConfiguration detectConfiguration;

    public DependencyGraph extractDependencyGraph(final String podLockText) throws IOException {
        YAMLMapper mapper = new YAMLMapper();
        PodfileLock podfileLock = mapper.readValue(podLockText, PodfileLock.class);

        NameVersionNode root = new NameVersionNode();
        root.setName(String.format("detectRootNode - %s", UUID.randomUUID()));
        final SubcomponentNodeBuilder builder = new SubcomponentNodeBuilder(root);

        for (Pod pod : podfileLock.getPods()) {
            buildNameVersionNode(builder, pod);
        };

        for (Pod dependency : podfileLock.getDependencies()) {
            NameVersionNode child = new NameVersionNode();
            child.setName(cleanPodName(dependency.getName()));
            builder.addChildNodeToParent(child, root);
        };

        if(null != podfileLock.getExternalSources() && !podfileLock.getExternalSources().getSources().isEmpty()){
            for(PodSource podSource : podfileLock.getExternalSources().getSources()) {
                NodeMetadata nodeMetadata = createMetadata(builder, podSource.getName());
                if (null != podSource.getGit() && podSource.getGit().contains("github")) {
                    // Change the forge to GitHub when there is better KB support
                    nodeMetadata.setForge(Forge.COCOAPODS);
                } else if (null != podSource.getPath() && podSource.getPath().contains("node_modules")) {
                    nodeMetadata.setForge(Forge.NPM);
                }
            }
        }

        MutableDependencyGraph graph = new MutableMapDependencyGraph();

        for(NameVersionNode nameVersionNode : builder.build().getChildren()) {
            Dependency childDependency = nameVersionNodeTransformer.addNameVersionNodeToDependencyGraph(graph, Forge.COCOAPODS, nameVersionNode);
            graph.addChildToRoot(childDependency);
        }

        return graph;
    }

    private NameVersionNode buildNameVersionNode(SubcomponentNodeBuilder builder, Pod pod) {
        NameVersionNode nameVersionNode = new NameVersionNode();
        nameVersionNode.setName(cleanPodName(pod.getName()));
        pod.setCleanName(nameVersionNode.getName());
        String[] segments = pod.getName().split(" ");
        if (segments.length > 1) {
            String version = segments[1];
            version = version.replace("(","").replace(")","").trim();
            if (!isVersionFuzzy(version)) {
                nameVersionNode.setVersion(version);
            }
        }
        for(String dependency : pod.getDependencies()) {
            builder.addChildNodeToParent(buildNameVersionNode(builder, new Pod(dependency)), nameVersionNode);
        }

        if (pod.getDependencies().isEmpty()) {
            builder.addToCache(nameVersionNode);
        }

        if (nameVersionNode.getName().contains("/")) {
            String superNodeName = nameVersionNode.getName().split("/")[0].trim();
            NameVersionNode superNode = new NameVersionNode();
            superNode.setName(superNodeName);
            superNode = builder.addToCache(superNode);
            SubcomponentMetadata superNodeMetadata = createMetadata(builder, superNode.getName());
            superNodeMetadata.getSubcomponents().add(nameVersionNode);

            SubcomponentMetadata subcomponentMetadata = createMetadata(builder, nameVersionNode.getName());
            subcomponentMetadata.setLinkNode(superNode);

            builder.getSuperComponents().add(superNode);
        }

        return nameVersionNode;
    }

    private SubcomponentMetadata createMetadata(NameVersionNodeBuilder builder, String name) {
        SubcomponentMetadata metadata = (SubcomponentMetadata) builder.getNodeMetadata(cleanPodName(name));
        if (null == metadata) {
            metadata = new SubcomponentMetadata();
            builder.setMetadata(name, metadata);
        }

        return metadata;
    }

    private boolean isVersionFuzzy(final String versionName) {
        for (String identifier: fuzzyVersionIdentifiers) {
            if(versionName.contains(identifier)) {
                return true;
            }
        }
        return false;
    }

    private String cleanPodName(String rawPodName) {
        if(StringUtils.isNotBlank(rawPodName)) {
            return rawPodName.split(" ")[0].trim();
        }
        return null;
    }

}
