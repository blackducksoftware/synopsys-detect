package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StringRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;

@Component
public class StringRequirementEvaluator extends RequirementEvaluator<StringRequirement> {

    @Override
    public RequirementEvaluation<String> evaluate(final StringRequirement requirement, final EvaluationContext context) {
        if (StringUtils.isNotBlank(requirement.value)) {
            return new RequirementEvaluation<>(EvaluationResult.Passed, requirement.value);
        }else {
            return new RequirementEvaluation<>(EvaluationResult.Failed, null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return StringRequirement.class;
    }
}
