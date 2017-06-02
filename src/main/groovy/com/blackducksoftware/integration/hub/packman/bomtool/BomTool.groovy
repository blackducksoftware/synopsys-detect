package com.blackducksoftware.integration.hub.packman.bomtool

import org.springframework.beans.factory.annotation.Autowired

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.type.BomToolType

abstract class BomTool {
    @Autowired
    PackmanProperties packmanProperties

    abstract BomToolType getBomToolType()
    abstract boolean isBomToolApplicable()

    /**
     * Each DependencyNode in the returned List should be a root project with all
     * its children dependencies. The expectation would be to create a Hub
     * project for each item in the List.
     */
    abstract List<DependencyNode> extractDependencyNodes()
}
