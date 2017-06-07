package com.blackducksoftware.integration.hub.detect.bomtool

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.rubygems.RubygemsNodePackager
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class RubygemsBomTool extends BomTool {
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