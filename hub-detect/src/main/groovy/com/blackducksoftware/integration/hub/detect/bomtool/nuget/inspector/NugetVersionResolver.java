package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.github.zafarkhaja.semver.Version;

public class NugetVersionResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DetectConfiguration detectConfiguration;
    private final DetectConfigurationUtility detectConfigurationUtility;
    private final NugetExeVersionResolver nugetExeVersionResolver;
    private final NugetApiVersionResolver nugetApiVersionResolver;

    public NugetVersionResolver(final DetectConfiguration detectConfiguration, final DetectConfigurationUtility detectConfigurationUtility,
        final NugetExeVersionResolver nugetExeVersionResolver, final NugetApiVersionResolver nugetApiVersionResolver) {
        this.detectConfiguration = detectConfiguration;
        this.detectConfigurationUtility = detectConfigurationUtility;
        this.nugetExeVersionResolver = nugetExeVersionResolver;
        this.nugetApiVersionResolver = nugetApiVersionResolver;
    }

    public Optional<Version> resolveInspectorVersion(final String nugetExe, String inspectorName, boolean airGap, ExecutableRunner executableRunner) throws ExecutableRunnerException, DetectUserFriendlyException, IOException {
        Optional<Version> version = Optional.empty();

        String[] nugetPackageRepos = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL);
        String nugetConfig = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
        File workingDirectory = new File(detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH));
        if (airGap) {
            logger.debug("Running in airgap mode. Resolving version from local path");
            String airgapPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH);
            version = nugetExeVersionResolver.resolveVersion(airgapPath, inspectorName, nugetConfig, workingDirectory, nugetExe, executableRunner);
        }

        // Attempt to retrieve version from Nuget APIs
        if (!version.isPresent()) {
            final String versionRange = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_VERSION);
            version = nugetApiVersionResolver.resolveVersion(Arrays.asList(nugetPackageRepos), inspectorName, versionRange);
        }

        // Version resolution via air gap or from the APIs have failed. Attempt to get a version from the nuget executable.
        if (!version.isPresent()) {
            logger.debug("Running online. Resolving version through Nuget executable");
            version = nugetExeVersionResolver.resolveVersion(nugetPackageRepos, inspectorName, nugetConfig, workingDirectory, nugetExe, executableRunner);
        }

        return version;
    }
}
