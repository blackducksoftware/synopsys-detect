package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import com.blackducksoftware.integration.hub.detect.bomtool.PartialBomToolSearcher;
import com.blackducksoftware.integration.hub.detect.bomtool.yarn.YarnBomToolSearcher;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class NpmBomToolSearcher extends PartialBomToolSearcher<NpmBomToolSearchResult> {
    private final Logger logger = LoggerFactory.getLogger(NpmBomToolSearcher.class);

    private final DetectFileManager detectFileManager;
    private final YarnBomToolSearcher yarnBomToolSearcher;

    public NpmBomToolSearcher(final DetectFileManager detectFileManager, final YarnBomToolSearcher yarnBomToolSearcher) {
        this.detectFileManager = detectFileManager;
        this.yarnBomToolSearcher = yarnBomToolSearcher;
    }

    @Override
    public NpmBomToolSearchResult isApplicable(final File directoryToSearch) {
        if (yarnBomToolSearcher.getSearchResult(directoryToSearch).isApplicable()) {
            logger.debug("The npm bomtool does not apply because yarn applies.");
            return false;
        }

        packageLockJson = detectFileManager.findFile(directoryToSearch, PACKAGE_LOCK_JSON) shrinkwrapJson = detectFileManager.findFile(directoryToSearch, SHRINKWRAP_JSON)

        final boolean containsNodeModules = detectFileManager.containsAllFiles(directoryToSearch, NODE_MODULES) final boolean containsPackageJson = detectFileManager.containsAllFiles(directoryToSearch, PACKAGE_JSON)
        final boolean containsPackageLockJson = packageLockJson final boolean containsShrinkwrapJson = shrinkwrapJson

        if (containsPackageJson && !containsNodeModules) {
            logger.warn("package.json was located in ${sourcePath}, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.")
        } else if (containsPackageJson && containsNodeModules) {

        } else if (containsPackageLockJson) {
            logger.info("Using ${PACKAGE_LOCK_JSON}")
        } else if (shrinkwrapJson) {
            logger.info("Using ${SHRINKWRAP_JSON}")
        }

        final boolean lockFileIsApplicable = containsShrinkwrapJson || containsPackageLockJson;
        final boolean isApplicable = lockFileIsApplicable || (containsNodeModules && npmExePath);

        return isApplicable;
    }

}
