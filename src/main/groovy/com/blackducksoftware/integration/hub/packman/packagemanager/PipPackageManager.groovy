package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipPackager
import com.blackducksoftware.integration.hub.packman.util.ExecutableFinder

@Component
class PipPackageManager extends PackageManager {
    public static final String SETUP_FILENAME = 'setup.py'

    @Autowired
    ExecutableFinder executableFinder

    @Value('${packman.pip.createVirtualEnv}')
    boolean createVirtualEnv

    @Value('${packman.output.path}')
    String outputDirectory

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.PIP
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        if (sourcePath && sourceDirectory.isDirectory()) {
            File setupFile = new File(sourceDirectory, SETUP_FILENAME)
            return setupFile.isFile()
        }

        false
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        def pipPackager = new PipPackager(executableFinder, sourcePath, outputDirectory, createVirtualEnv)
        def projects = pipPackager.makeDependencyNodes()
        return projects
    }
}