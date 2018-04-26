package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;

@Component
public class RequirementEvaluatorManager {


    @Autowired
    public FileRequirementEvaluator fileRequirementEvaluator;


    public RequirementEvaluation<?> evaluate(final Requirement<?> requirement, final EvaluationContext context) {
        throw new RuntimeException("Unknown requirement " + requirement.getClass());
    }

    public RequirementEvaluation<?> evaluate(final FileRequirement requirement, final EvaluationContext context) {
        return fileRequirementEvaluator.evaluate(requirement, context);
    }

    //public Evaluation<?> evaluate(final ExecutableRequirement requirement, final EvaluationContext context) {

    //}

}
