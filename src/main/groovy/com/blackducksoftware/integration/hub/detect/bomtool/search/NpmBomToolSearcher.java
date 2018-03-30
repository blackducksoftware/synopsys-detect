package com.blackducksoftware.integration.hub.detect.bomtool.search;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NpmBomToolSearcher extends PartialBomToolSearcher<NpmBomToolSearchResult> {
    private final Logger logger = LoggerFactory.getLogger(NpmBomToolSearcher.class);

    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";
    public static final String PACKAGE_LOCK_JSON = "package-lock.json";
    public static final String SHRINKWRAP_JSON = "npm-shrinkwrap.json";

    private final YarnBomToolSearcher yarnBomToolSearcher;

    public NpmBomToolSearcher(final ExecutableManager executableManager, final ExecutableRunner executableRunner, final DetectFileManager detectFileManager, final DetectConfiguration detectConfiguration,
            final YarnBomToolSearcher yarnBomToolSearcher) {
        super(executableManager, executableRunner, detectFileManager, detectConfiguration);
        this.yarnBomToolSearcher = yarnBomToolSearcher;
    }

    @Override
    public NpmBomToolSearchResult getSearchResult(final File directoryToSearch) {
        if (yarnBomToolSearcher.getSearchResult(directoryToSearch).isApplicable()) {
            logger.debug("The npm bomtool does not apply because yarn applies.");
            return BomToolSearchResultFactory.createNpmDoesNotApply();
        }

        String npmExePath = null;
        final File packageLockJson = detectFileManager.findFile(directoryToSearch, NpmBomToolSearcher.PACKAGE_LOCK_JSON);
        final File shrinkwrapJson = detectFileManager.findFile(directoryToSearch, NpmBomToolSearcher.SHRINKWRAP_JSON);

        final boolean containsNodeModules = detectFileManager.containsAllFiles(directoryToSearch, NpmBomToolSearcher.NODE_MODULES);
        final boolean containsPackageJson = detectFileManager.containsAllFiles(directoryToSearch, NpmBomToolSearcher.PACKAGE_JSON);
        final boolean containsPackageLockJson = packageLockJson != null && packageLockJson.exists();
        final boolean containsShrinkwrapJson = shrinkwrapJson != null && shrinkwrapJson.exists();

        if (containsPackageJson && !containsNodeModules) {
            logger.warn(String.format("package.json was located in %s, but the node_modules folder was NOT located. Please run 'npm install' in that location and try again.", directoryToSearch.getAbsolutePath()));
        } else if (containsPackageJson && containsNodeModules) {
            npmExePath = findExecutablePath(ExecutableType.NPM, true, directoryToSearch, detectConfiguration.getNpmPath());
            if (StringUtils.isBlank(npmExePath)) {
                logger.warn(String.format("Could not find an %s executable", executableManager.getExecutableName(ExecutableType.NPM)));
            } else {
                Executable npmVersionExe = null;
                final List<String> arguments = new ArrayList<>();
                arguments.add("-version");

                String npmNodePath = detectConfiguration.getNpmNodePath();
                if (StringUtils.isNotBlank(npmNodePath)) {
                    final int lastSlashIndex = npmNodePath.lastIndexOf("/");
                    if (lastSlashIndex >= 0) {
                        npmNodePath = npmNodePath.substring(0, lastSlashIndex);
                    }
                    final Map<String, String> environmentVariables = new HashMap<>();
                    environmentVariables.put("PATH", npmNodePath);

                    npmVersionExe = new Executable(directoryToSearch, environmentVariables, npmExePath, arguments);
                } else {
                    npmVersionExe = new Executable(directoryToSearch, npmExePath, arguments);
                }
                try {
                    final String npmVersion = executableRunner.execute(npmVersionExe).getStandardOutput();
                    logger.debug(String.format("Npm version %s", npmVersion));
                } catch (final ExecutableRunnerException e) {
                    logger.error(String.format("Could not run npm to get the version: %s", e.getMessage()));
                    return BomToolSearchResultFactory.createNpmDoesNotApply();
                }
            }
        } else if (containsPackageLockJson) {
            logger.info(String.format("Using %s", NpmBomToolSearcher.PACKAGE_LOCK_JSON));
        } else if (containsShrinkwrapJson) {
            logger.info(String.format("Using %s", NpmBomToolSearcher.SHRINKWRAP_JSON));
        }

        final boolean lockFileIsApplicable = containsShrinkwrapJson || containsPackageLockJson;
        final boolean isApplicable = lockFileIsApplicable || (containsNodeModules && StringUtils.isNotBlank(npmExePath));

        if (isApplicable) {
            return BomToolSearchResultFactory.createNpmApplies(directoryToSearch, npmExePath, packageLockJson, shrinkwrapJson);
        } else {
            return BomToolSearchResultFactory.createNpmDoesNotApply();
        }
    }

}
