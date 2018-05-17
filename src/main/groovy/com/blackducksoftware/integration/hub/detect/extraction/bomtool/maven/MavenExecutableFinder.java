package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class MavenExecutableFinder {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String systemMaven = null;
    private boolean hasLookedForSystemMaven = false;

    public String findMaven(final StrategyEnvironment environment) {
        String resolvedMaven = null;
        final String gradlePath = executableManager.getExecutablePathOrOverride(ExecutableType.MVNW, false, environment.getDirectory(), detectConfiguration.getMavenPath());
        if (StringUtils.isNotBlank(gradlePath)) {
            resolvedMaven = gradlePath;
        }else {
            if (!hasLookedForSystemMaven) {
                systemMaven = executableManager.getExecutablePathOrOverride(ExecutableType.MVN, true, environment.getDirectory(), detectConfiguration.getMavenPath());
                hasLookedForSystemMaven = true;
            }
            resolvedMaven = systemMaven;
        }
        return resolvedMaven;
    }
}
