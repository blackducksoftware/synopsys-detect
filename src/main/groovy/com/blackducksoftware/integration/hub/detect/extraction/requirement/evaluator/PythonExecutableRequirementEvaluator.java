package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.PythonExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PythonExecutableRequirementEvaluator extends RequirementEvaluator<PythonExecutableRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String resolvedPython = null;
    private boolean hasLookedForPython = false;

    @Override
    public RequirementEvaluation<String> evaluate(final PythonExecutableRequirement requirement, final EvaluationContext context) {
        try {
            if (!hasLookedForPython) {
                hasLookedForPython = true;
                ExecutableType pythonType = ExecutableType.PYTHON;
                if (detectConfiguration.getPythonThreeOverride()) {
                    pythonType = ExecutableType.PYTHON3;
                }
                resolvedPython = executableManager.getExecutablePathOrOverride(pythonType, true, context.getDirectory(), null);
            }
            if (resolvedPython != null) {
                return RequirementEvaluation.passed( resolvedPython);
            }else {
                return RequirementEvaluation.failed(null, "No Python executable was found.");
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return PythonExecutableRequirement.class;
    }
}