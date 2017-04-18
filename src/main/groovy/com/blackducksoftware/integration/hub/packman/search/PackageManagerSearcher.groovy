package com.blackducksoftware.integration.hub.packman.search

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManager

abstract class PackageManagerSearcher {
    abstract PackageManager getPackageManager()
    abstract boolean isPackageManagerApplicable(String sourcePath)
    abstract List<DependencyNode> extractDependencyNodes(String sourcePath)
}
