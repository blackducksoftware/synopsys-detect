package com.blackducksoftware.integration.hub.packman.packagemanager

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.rubygems.RubygemsPackager

@Component
class RubygemsPackageManager extends PackageManager {
    public static final String GEMFILELOCK_FILENAME = 'Gemfile.lock'

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.RUBYGEMS
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        if (sourcePath && sourceDirectory.isDirectory()) {
            File gemlockFile = new File(sourceDirectory, GEMFILELOCK_FILENAME)
            return gemlockFile.isFile()
        }
        false
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        File gemlockFile = new File(sourceDirectory, GEMFILELOCK_FILENAME)

        final InputStream gemlockStream
        try {
            gemlockStream = new FileInputStream(gemlockFile)
            String potentialProjectName = sourceDirectory.getName()
            def rubygemsPackager = new RubygemsPackager(IOUtils.toString(gemlockStream, StandardCharsets.UTF_8))
            def projects = rubygemsPackager.makeDependencyNodes()
            return projects
        } finally {
            IOUtils.closeQuietly(gemlockStream)
        }
    }
}