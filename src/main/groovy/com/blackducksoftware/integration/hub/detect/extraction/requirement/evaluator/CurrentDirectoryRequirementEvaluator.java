package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.CurrentDirectoryRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

@Component
public class CurrentDirectoryRequirementEvaluator extends RequirementEvaluator<CurrentDirectoryRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Override
    public RequirementEvaluation<File> evaluate(final CurrentDirectoryRequirement requirement, final EvaluationContext context) {
        return new RequirementEvaluation<>(EvaluationResult.Passed, context.getDirectory());
    }

    @Override
    public Class getRequirementClass() {
        return CurrentDirectoryRequirement.class;
    }
}
