package com.blackducksoftware.integration.hub.packman.packagemanager

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType

@Component
class GradlePackageManager extends PackageManager {
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
        //        File sourceDirectory = new File(sourcePath)
        //        File podfileLockFile = new File(sourceDirectory, PODFILE_LOCK_FILENAME)
        //        File podfileFile = new File(sourceDirectory, PODFILE_FILENAME)
        //        final InputStream podfileLockStream = new FileInputStream(podfileLockFile)
        //        final InputStream podfileStream = new FileInputStream(podfileFile)
        //        try {
        //            def cocoaPodsPackager = new CocoapodsPackager(podfileStream, podfileLockStream)
        //            def projects = cocoaPodsPackager.makeDependencyNodes()
        //        } finally {
        //            IOUtils.closeQuietly(podfileLockFile)
        //            IOUtils.closeQuietly(podfileFile)
        //        }
        return null
    }
}