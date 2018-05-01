package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;

@Component
public class RequirementEvaluatorManager {

    @Autowired
    public List<RequirementEvaluator> requirementEvaluators;

    public RequirementEvaluation<?> evaluate(final Requirement requirement, final EvaluationContext context) {
        for (final RequirementEvaluator evaluator : requirementEvaluators) {
            if (evaluator.getRequirementClass().equals(requirement.getClass())) {
                return evaluator.evaluate(requirement, context);
            }
        }

        throw new RuntimeException("Unknown requirement " + requirement.getClass());
    }

}
