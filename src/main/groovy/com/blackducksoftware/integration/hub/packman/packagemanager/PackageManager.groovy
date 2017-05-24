package com.blackducksoftware.integration.hub.packman.packagemanager

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType

abstract class PackageManager {
    abstract PackageManagerType getPackageManagerType()
    abstract boolean isPackageManagerApplicable(String sourcePath)

    /**
     * Each DependencyNode in the returned List should be a root project with all
     * its children dependencies. The expectation would be to create a Hub
     * project for each item in the list.
     */
    abstract List<DependencyNode> extractDependencyNodes(String sourcePath)
}
