package com.blackducksoftware.integration.hub.packman.packagemanager

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.RubygemsNodePackager
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

@Component
class RubygemsPackageManager extends PackageManager {
    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    FileFinder fileFinder

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.RUBYGEMS
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        fileFinder.containsAllFiles(sourcePath, 'Gemfile.lock')
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        File gemlockFile = new File(sourceDirectory, 'Gemfile.lock')

        final InputStream gemlockStream
        try {
            gemlockStream = new FileInputStream(gemlockFile)
            String potentialProjectName = sourceDirectory.getName()
            String gemlock = IOUtils.toString(gemlockStream, StandardCharsets.UTF_8)
            def rubygemsPackager = new RubygemsNodePackager(projectInfoGatherer)
            def projects = rubygemsPackager.makeDependencyNodes(sourcePath, gemlock)
            return projects
        } finally {
            IOUtils.closeQuietly(gemlockStream)
        }
    }
}