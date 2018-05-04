package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.MavenExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class MavenExecutableRequirementEvaluator extends RequirementEvaluator<MavenExecutableRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String systemMaven= null;
    private boolean hasLookedForSystemMaven = false;

    @Override
    public RequirementEvaluation<String> evaluate(final MavenExecutableRequirement requirement, final EvaluationContext context) {
        try {
            String resolvedMaven = null;
            final String mavenPath = executableManager.getExecutablePathOrOverride(ExecutableType.MVNW, false, context.getDirectory(), detectConfiguration.getMavenPath());
            if (StringUtils.isNotBlank(mavenPath)) {
                resolvedMaven = mavenPath;
            }else {
                if (!hasLookedForSystemMaven) {
                    systemMaven = executableManager.getExecutablePathOrOverride(ExecutableType.MVN, true, context.getDirectory(), detectConfiguration.getMavenPath());
                    hasLookedForSystemMaven = true;
                }
                resolvedMaven = systemMaven;
            }
            if (resolvedMaven != null) {
                return RequirementEvaluation.passed( resolvedMaven);
            }else {
                return RequirementEvaluation.failed(null, "No Maven executable was found.");
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return MavenExecutableRequirement.class;
    }
}