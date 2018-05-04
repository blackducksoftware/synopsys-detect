package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.StringRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;

@Component
public class StringRequirementEvaluator extends RequirementEvaluator<StringRequirement> {

    @Override
    public RequirementEvaluation<String> evaluate(final StringRequirement requirement, final EvaluationContext context) {
        if (StringUtils.isNotBlank(requirement.value)) {
            return RequirementEvaluation.passed(requirement.value);
        }else {
            String description = "Required a value, but did not find a value.";
            if (requirement.failedDescriptionOverride != null) {
                description = requirement.failedDescriptionOverride;
            }
            return RequirementEvaluation.failed(null, description);
        }
    }

    @Override
    public Class getRequirementClass() {
        return StringRequirement.class;
    }
}
