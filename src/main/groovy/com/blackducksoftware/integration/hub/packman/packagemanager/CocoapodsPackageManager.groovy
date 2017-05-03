package com.blackducksoftware.integration.hub.packman.packagemanager

import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManagerType
import com.blackducksoftware.integration.hub.packman.packagemanager.cocoapods.CocoapodsPackager
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

@Component
class CocoapodsPackageManager extends PackageManager {
    public static final String PODFILE_LOCK_FILENAME = 'Podfile.lock'
    public static final String PODFILE_FILENAME = 'Podfile'
    public static final String PODSPEC_FILENAME_EXTENSION = '.podspec'

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    PackageManagerType getPackageManagerType() {
        return PackageManagerType.COCOAPODS
    }

    boolean isPackageManagerApplicable(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        if (sourcePath && sourceDirectory.isDirectory()) {
            File podfileLockFile = new File(sourceDirectory, PODFILE_LOCK_FILENAME)
            File podfileFile = new File(sourceDirectory, PODFILE_FILENAME)
            return podfileLockFile.isFile() && podfileFile.isFile()
        }

        false
    }

    List<DependencyNode> extractDependencyNodes(String sourcePath) {
        File sourceDirectory = new File(sourcePath)
        File podfileLockFile = new File(sourceDirectory, PODFILE_LOCK_FILENAME)
        File podfileFile = new File(sourceDirectory, PODFILE_FILENAME)
        File podspecFile = findPodspecFile(sourceDirectory)
        final InputStream podfileLockStream
        final InputStream podfileStream
        final InputStream podspecStream
        try {
            podfileLockStream = new FileInputStream(podfileLockFile)
            podfileStream = new FileInputStream(podfileFile)
            podspecStream = podspecFile != null ? new FileInputStream(podspecFile) : null
            def cocoaPodsPackager = new CocoapodsPackager(projectInfoGatherer, podfileLockStream, sourcePath)
            def projects = cocoaPodsPackager.makeDependencyNodes()
            return projects
        } finally {
            IOUtils.closeQuietly(podfileLockStream)
            IOUtils.closeQuietly(podfileStream)
            IOUtils.closeQuietly(podspecStream)
        }
    }

    private File findPodspecFile(File sourceDirectory) {
        sourceDirectory.listFiles().find {
            it.name.endsWith(PODSPEC_FILENAME_EXTENSION)
        }
    }
}