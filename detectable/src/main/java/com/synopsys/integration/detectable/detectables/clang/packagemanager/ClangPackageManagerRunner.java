package com.synopsys.integration.detectable.detectables.clang.packagemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.ClangPackageManagerResolver;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.resolver.NotOwnedByAnyPkgException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ClangPackageManagerRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public boolean applies(ClangPackageManager currentPackageManager, File workingDirectory, DetectableExecutableRunner executor) {
        ClangPackageManagerInfo packageManagerInfo = currentPackageManager.getPackageManagerInfo();
        try {
            ExecutableOutput versionOutput = executor.execute(workingDirectory, packageManagerInfo.getPkgMgrName(), packageManagerInfo.getCheckPresenceCommandArgs());
            logger.debug(String.format("packageStatusOutput: %s", versionOutput.getStandardOutput()));
            if (versionOutput.getStandardOutput().contains(packageManagerInfo.getCheckPresenceCommandOutputExpectedText())) {
                logger.debug(String.format("Found package manager %s", packageManagerInfo.getPkgMgrName()));
                return true;
            }
            logger.debug(String.format(
                "Output of %s %s does not look right; concluding that the %s package manager is not present. The output: %s",
                packageManagerInfo.getPkgMgrName(),
                packageManagerInfo.getCheckPresenceCommandArgs(),
                packageManagerInfo.getPkgMgrName(),
                versionOutput
            ));
        } catch (ExecutableRunnerException e) {
            logger.debug(String.format(
                "Error executing %s %s; concluding that the %s package manager is not present. The error: %s",
                packageManagerInfo.getPkgMgrName(),
                packageManagerInfo.getCheckPresenceCommandArgs(),
                packageManagerInfo.getPkgMgrName(),
                e.getMessage()
            ));
            return false;
        }
        return false;
    }

    public PackageDetailsResult getAllPackages(
        ClangPackageManager currentPackageManager,
        File workingDirectory,
        DetectableExecutableRunner executableRunner,
        Set<File> dependencyFiles
    ) {
        Set<PackageDetails> packageDetails = new HashSet<>();
        Set<File> unRecognizedDependencyFiles = new HashSet<>();
        for (File dependencyFile : dependencyFiles) {
            PackageDetailsResult packageDetailsResult = getPackages(currentPackageManager, workingDirectory, executableRunner, dependencyFile);
            packageDetails.addAll(packageDetailsResult.getFoundPackages());
            unRecognizedDependencyFiles.addAll(packageDetailsResult.getUnRecognizedDependencyFiles());
        }

        return new PackageDetailsResult(packageDetails, unRecognizedDependencyFiles);
    }

    public PackageDetailsResult getPackages(ClangPackageManager currentPackageManager, File workingDirectory, DetectableExecutableRunner executableRunner, File dependencyFile) {
        ClangPackageManagerInfo packageManagerInfo = currentPackageManager.getPackageManagerInfo();
        Set<PackageDetails> dependencyDetails = new HashSet<>();
        Set<File> unRecognizedDependencyFiles = new HashSet<>();
        try {
            List<String> fileSpecificGetOwnerArgs = new ArrayList<>(packageManagerInfo.getPkgMgrGetOwnerCmdArgs());
            fileSpecificGetOwnerArgs.add(dependencyFile.getAbsolutePath());
            ExecutableOutput queryPackageResult = executableRunner.execute(workingDirectory, packageManagerInfo.getPkgMgrCmdString(), fileSpecificGetOwnerArgs);
            String queryPackageOutputToParse;
            if (StringUtils.isNotBlank(queryPackageResult.getStandardOutput())) {
                queryPackageOutputToParse = queryPackageResult.getStandardOutput();
            } else {
                queryPackageOutputToParse = queryPackageResult.getErrorOutput();
            }
            ClangPackageManagerResolver resolver = currentPackageManager.getPackageResolver();
            List<PackageDetails> packageDetails = resolver.resolvePackages(
                currentPackageManager.getPackageManagerInfo(),
                executableRunner,
                workingDirectory,
                queryPackageOutputToParse
            );
            dependencyDetails.addAll(packageDetails);
        } catch (NotOwnedByAnyPkgException notOwnedException) {
            logger.debug(String.format("%s is not recognized by the linux package manager (%s)", dependencyFile.getAbsolutePath(), notOwnedException.getMessage()));
            unRecognizedDependencyFiles.add(dependencyFile);
        } catch (ExecutableRunnerException e) {
            logger.debug(String.format("Error with dependency file %s when running %s", dependencyFile.getAbsolutePath(), packageManagerInfo.getPkgMgrCmdString()));
            logger.error(String.format("Error executing %s: %s", packageManagerInfo.getPkgMgrCmdString(), e.getMessage()));
        }
        return new PackageDetailsResult(dependencyDetails, unRecognizedDependencyFiles);
    }

}
