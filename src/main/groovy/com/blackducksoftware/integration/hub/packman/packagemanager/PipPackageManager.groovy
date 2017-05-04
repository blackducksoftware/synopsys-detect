package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.packagemanager.pip.PipPackager
import com.blackducksoftware.integration.hub.packman.util.FileFinder

@Component
class PipPackageManager extends PackageManager {
    public static final String SETUP_FILENAME = 'setup.py'

    @Autowired
    FileFinder fileFinder

    @Autowired
    PackmanProperties packmanProperties

    @Value('${packman.pip.createVirtualEnv}')
    boolean createVirtualEnv

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
        def pipPackager = new PipPackager(fileFinder, sourcePath, packmanProperties.outputDirectoryPath, createVirtualEnv)
        def projects = pipPackager.makeDependencyNodes()
        return projects
    }
}