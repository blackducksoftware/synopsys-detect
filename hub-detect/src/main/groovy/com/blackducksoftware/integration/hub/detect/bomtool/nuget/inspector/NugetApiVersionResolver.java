package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion2.NugetApi2;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3.NugetApi3;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.util.VersionUtil;
import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;

public class NugetApiVersionResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final NugetApi3 nugetApi3;
    private final NugetApi2 nugetApi2;
    private final DetectConfigurationUtility detectConfigurationUtility;

    public NugetApiVersionResolver(final NugetApi3 nugetApi3, final NugetApi2 nugetApi2, final DetectConfigurationUtility detectConfigurationUtility) {
        this.nugetApi3 = nugetApi3;
        this.nugetApi2 = nugetApi2;
        this.detectConfigurationUtility = detectConfigurationUtility;
    }

    public Optional<Version> resolveVersion(List<String> nugetPackageRepos, String inspectorName, String versionRange) throws DetectUserFriendlyException, IOException {
        logger.debug("Running online. Resolving version through Nuget API v3");
        Optional<Version> version = Optional.empty();
        for (final String source : nugetPackageRepos) {
            logger.debug("Attempting source: " + source);
            try (UnauthenticatedRestConnection restConnection = detectConfigurationUtility.createUnauthenticatedRestConnection(source)) {

                List<Version> availableVersions = nugetApi3.findVersions(source, inspectorName, restConnection);

                if (availableVersions.size() == 0) {
                    logger.debug("Failed to resolve version from Nuget API v3, attempting to resolve through Nuget API v2");
                    availableVersions.addAll(nugetApi2.findVersions(source, inspectorName, restConnection));
                }

                if (availableVersions.size() > 0) {
                    version = VersionUtil.bestMatch(availableVersions, versionRange);
                    if (version.isPresent()) {
                        logger.debug(String.format("Found version [%s] in source [%s]", version.get().toString(), source));
                        break;
                    }
                } else {
                    logger.debug(String.format("No version found in source [%s] matching version [%s]", source, versionRange));
                }
            }
        }
        return version;
    }
}
