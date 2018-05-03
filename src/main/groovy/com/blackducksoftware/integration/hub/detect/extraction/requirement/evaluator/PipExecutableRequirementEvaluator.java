package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.PipExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PipExecutableRequirementEvaluator extends RequirementEvaluator<PipExecutableRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String resolvedPip = null;
    private boolean hasLookedForPip = false;

    @Override
    public RequirementEvaluation<String> evaluate(final PipExecutableRequirement requirement, final EvaluationContext context) {
        try {
            if (!hasLookedForPip) {
                hasLookedForPip = true;
                ExecutableType pipType = ExecutableType.PIP;
                if (detectConfiguration.getPythonThreeOverride()) {
                    pipType = ExecutableType.PIP3;
                }
                resolvedPip = executableManager.getExecutablePathOrOverride(pipType, true, context.getDirectory(), null);
            }
            if (resolvedPip != null) {
                return new RequirementEvaluation<>(EvaluationResult.Passed, resolvedPip);
            }else {
                return new RequirementEvaluation<>(EvaluationResult.Failed, null);
            }
        }catch (final Exception e) {
            return new RequirementEvaluation<>(EvaluationResult.Exception, null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return PipExecutableRequirement.class;
    }
}