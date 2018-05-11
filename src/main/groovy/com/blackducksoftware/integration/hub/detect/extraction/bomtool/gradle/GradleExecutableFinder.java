package com.blackducksoftware.integration.hub.detect.extraction.bomtool.gradle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class GradleExecutableFinder {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String systemGradle = null;
    private boolean hasLookedForSystemGradle = false;

    public String findGradle(final EvaluationContext context) {
        String resolvedGradle = null;
        final String gradlePath = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLEW, false, context.getDirectory(), detectConfiguration.getGradlePath());
        if (StringUtils.isNotBlank(gradlePath)) {
            resolvedGradle = gradlePath;
        }else {
            if (!hasLookedForSystemGradle) {
                systemGradle = executableManager.getExecutablePathOrOverride(ExecutableType.GRADLE, true, context.getDirectory(), detectConfiguration.getGradlePath());
                hasLookedForSystemGradle = true;
            }
            resolvedGradle = systemGradle;
        }
        return resolvedGradle;
    }
}
