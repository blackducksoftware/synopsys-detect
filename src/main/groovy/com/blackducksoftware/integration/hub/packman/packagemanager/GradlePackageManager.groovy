package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.gradle.GradleParsingPackager
import com.google.gson.Gson

@Component
class GradlePackageManager extends PackageManager {
    @Value('${packman.gradle.path}')
    String gradlePath

    @Autowired
    Gson gson;

    @Autowired
    ExecutableFinder executableFinder;

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.GRADLE
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        if (sourcePath && sourceDirectory.isDirectory()) {
            File buildGradleFile = new File(sourceDirectory, "build.gradle")
            return buildGradleFile.isFile()
        }

        false
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        //        GradleFilePackager gradleFilePackager = new GradleFilePackager(gson, executableFinder, gradlePath, sourcePath)
        //        gradleFilePackager.makeDependencyNodes()
        GradleParsingPackager gradleParsingPackager = new GradleParsingPackager(executableFinder, gradlePath, sourcePath)
        gradleParsingPackager.makeDependencyNodes()
    }
}