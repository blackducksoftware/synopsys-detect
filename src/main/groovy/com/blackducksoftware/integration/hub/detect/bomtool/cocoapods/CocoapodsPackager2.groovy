package com.blackducksoftware.integration.hub.detect.bomtool.cocoapods

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionLinkNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionLinkNodeBuilder
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

@Component
class CocoapodsPackager2 {
    public Set<DependencyNode> extractDependencyNodes(final String podLockText) {
        YAMLMapper mapper = new YAMLMapper()
        PodfileLock podfileLock = mapper.readValue(podLockText, PodfileLock.class)

        def root = new NameVersionLinkNode()
        root.name = "detectRootNode - ${UUID.randomUUID()}"
        def builder = new NameVersionLinkNodeBuilder(root)







        builder.build().children as Set
    }

    private String cleanPodName(String podName) {
    }
}
