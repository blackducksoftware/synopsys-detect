package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.BomToolRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

@Component
public class BomToolRequirementEvaluator extends RequirementEvaluator<BomToolRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Override
    public RequirementEvaluation<BomToolType> evaluate(final BomToolRequirement requirement, final EvaluationContext context) {
        if (detectConfiguration.isBomToolIncluded(requirement.type)) {
            return new RequirementEvaluation<>(EvaluationResult.Passed, requirement.type);
        }else {
            return new RequirementEvaluation<>(EvaluationResult.Failed, requirement.type);
        }
    }

    @Override
    public Class getRequirementClass() {
        return BomToolRequirement.class;
    }
}
