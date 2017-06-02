package com.blackducksoftware.integration.hub.packman.bomtool

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.bomtool.rubygems.RubygemsNodePackager
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.SourcePathSearcher

@Component
class RubygemsBomTool extends BomTool {
    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    SourcePathSearcher sourcePathSearcher

    List<String> matchingSourcePaths = []

    BomToolType getBomToolType() {
        return BomToolType.RUBYGEMS
    }

    boolean isBomToolApplicable() {
        matchingSourcePaths = sourcePathSearcher.findSourcePathsContainingFilenamePattern('Gemfile.lock')

        !matchingSourcePaths.empty
    }

    List<DependencyNode> extractDependencyNodes() {
        List<DependencyNode> projectNodes = []
        matchingSourcePaths.each { sourcePath ->
            File sourceDirectory = new File(sourcePath)
            File gemlockFile = new File(sourceDirectory, 'Gemfile.lock')

            final InputStream gemlockStream
            try {
                gemlockStream = new FileInputStream(gemlockFile)
                String potentialProjectName = sourceDirectory.getName()
                String gemlock = IOUtils.toString(gemlockStream, StandardCharsets.UTF_8)
                def rubygemsPackager = new RubygemsNodePackager(projectInfoGatherer)
                def projects = rubygemsPackager.makeDependencyNodes(sourcePath, gemlock)
                projectNodes.addAll(projects)
            } finally {
                IOUtils.closeQuietly(gemlockStream)
            }
        }

        projectNodes
    }
}