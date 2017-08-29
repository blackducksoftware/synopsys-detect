package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.LinkMetadata
import com.blackducksoftware.integration.hub.detect.nameversion.Metadata
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

@Component
class CocoapodsPackager {
    final List<String> fuzzyVersionIdentifiers = ['>', '<', '~>', '=']

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> extractDependencyNodes(final String podLockText) {
        YAMLMapper mapper = new YAMLMapper()
        PodfileLock podfileLock = mapper.readValue(podLockText, PodfileLock.class)

        def root = new NameVersionNodeImpl()
        root.name = "detectRootNode - ${UUID.randomUUID()}"
        NameVersionNodeBuilder builder = new NameVersionNodeBuilder(root)

        podfileLock.pods.each { podToNameVersionNode(builder, it) }

        podfileLock.dependencies.each {
            def child = new NameVersionNodeImpl([name: cleanPodName(it.name)])
            builder.addChildNodeToParent(child, root)
        }

        podfileLock.externalSources?.sources.each { source ->
            Metadata metadata = getMetadata(builder, source.name)

            if (source.git && source.git.contains('github')) {
                // Change the forge to GitHub when there is better KB support
                metadata.setForge(Forge.COCOAPODS)
            } else if (source.path && source.path.contains('node_modules')) {
                metadata.setForge(Forge.NPM)
            }

            builder.setMetadata(cleanPodName(source.name), metadata)
        }



        builder.build().children.collect { nameVersionNodeTransformer.createDependencyNode(Forge.COCOAPODS, it) } as Set
    }

    private NameVersionNode podToNameVersionNode(NameVersionNodeBuilder builder, Pod pod) {
        def nameVersionNode = new NameVersionNodeImpl()
        nameVersionNode.name = cleanPodName(pod.name)
        pod.cleanName = nameVersionNode.name
        String[] segments = pod.name.split(' ')
        if (segments.length > 1) {
            String version = segments[1]
            version = version.replace('(','').replace(')','').trim()
            if (!isVersionFuzzy(version)) {
                nameVersionNode.version = version
            }
        }

        pod.dependencies.each { builder.addChildNodeToParent(podToNameVersionNode(builder, new Pod(it)), nameVersionNode) }

        if (pod.dependencies.isEmpty()) {
            builder.addToCache(nameVersionNode)
        }

        if (nameVersionNode.name.contains('/')) {
            String linkNodeName = nameVersionNode.name.split('/')[0].trim()
            def linkNode = builder.addToCache(new NameVersionNodeImpl([name: linkNodeName]))
            LinkMetadata metadata = getMetadata(builder, nameVersionNode.name)
            metadata.linkNode = linkNode
            builder.setMetadata(nameVersionNode.name, metadata)
        }

        nameVersionNode
    }

    private LinkMetadata getMetadata(NameVersionNodeBuilder builder, String name) {
        LinkMetadata metadata = (LinkMetadata) builder.getMetadata(cleanPodName(name))
        if (!metadata) {
            metadata = new LinkMetadata()
        }

        metadata
    }

    private boolean isVersionFuzzy(String versionName) {
        fuzzyVersionIdentifiers.any { versionName.contains(it) }
    }

    private String cleanPodName(String rawPodName) {
        rawPodName?.split(' ')[0].trim()
    }
}
