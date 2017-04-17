package com.blackducksoftware.integration.hub.packman.search

import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.PackageManager
import com.blackducksoftware.integration.hub.packman.parser.cocoapods.CocoapodsPackager

@Component
class CocoapodsSearcher extends PackageManagerSearcher {
    public static final String PODFILE_LOCK_FILENAME = 'Podfile.lock';
    public static final String PODFILE_FILENAME = 'Podfile';

    PackageManager getPackageManager() {
        return PackageManager.COCOAPODS
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
        final InputStream podfileLockStream = new FileInputStream(podfileLockFile)
        final InputStream podfileStream = new FileInputStream(podfileFile)
        try {
            def cocoaPodsPackager = new CocoapodsPackager(podfileStream, podfileLockStream)
            def projects = cocoaPodsPackager.makeDependencyNodes()
        } finally {
            IOUtils.closeQuietly(podfileLockFile)
            IOUtils.closeQuietly(podfileFile)
        }
    }
}