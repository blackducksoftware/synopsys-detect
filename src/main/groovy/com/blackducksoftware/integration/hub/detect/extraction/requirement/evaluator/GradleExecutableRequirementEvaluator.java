package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.GradleExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class GradleExecutableRequirementEvaluator extends RequirementEvaluator<GradleExecutableRequirement> {

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

    @Override
    public RequirementEvaluation<String> evaluate(final GradleExecutableRequirement requirement, final EvaluationContext context) {
        try {
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
            if (resolvedGradle != null) {
                return new RequirementEvaluation<>(EvaluationResult.Passed, resolvedGradle);
            }else {
                return new RequirementEvaluation<>(EvaluationResult.Failed, null);
            }
        }catch (final Exception e) {
            return new RequirementEvaluation<>(EvaluationResult.Exception, null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return GradleExecutableRequirement.class;
    }
}
