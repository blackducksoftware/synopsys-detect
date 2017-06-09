package com.blackducksoftware.integration.hub.detect.bomtool

import org.springframework.beans.factory.annotation.Autowired

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectProperties
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.FileFinder
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.SourcePathSearcher
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

abstract class BomTool {
    @Autowired
    DetectProperties detectProperties

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    ExecutableManager executableManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    SourcePathSearcher sourcePathSearcher

    @Autowired
    FileFinder fileFinder

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    abstract BomToolType getBomToolType()
    abstract boolean isBomToolApplicable()

    /**
     * Each DependencyNode in the returned List should be a root project with all
     * its children dependencies. The expectation would be to create a Hub
     * project for each item in the List.
     */
    abstract List<DependencyNode> extractDependencyNodes()
}
