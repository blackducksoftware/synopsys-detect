package com.blackducksoftware.integration.hub.detect.bomtool

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.cocoapods.CocoapodsPackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class CocoapodsBomTool extends BomTool {
    @Autowired
    CocoapodsPackager cocoapodsPackager

    private List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.COCOAPODS
    }

    boolean isBomToolApplicable() {
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern('Podfile.lock')

        !matchingSourcePaths.empty
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each {
            projectNodes.addAll(cocoapodsPackager.makeDependencyNodes(it))
        }

        projectNodes
    }
}