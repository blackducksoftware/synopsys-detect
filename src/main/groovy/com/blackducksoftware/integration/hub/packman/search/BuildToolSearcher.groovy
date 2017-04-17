package com.blackducksoftware.integration.hub.packman.search

import com.blackducksoftware.integration.hub.packman.BuildTool

abstract class BuildToolSearcher {
    abstract BuildTool getBuildTool()

    List<String> findSourcePathsForBuildTool(List<String> sourcePaths) {
    }
}
