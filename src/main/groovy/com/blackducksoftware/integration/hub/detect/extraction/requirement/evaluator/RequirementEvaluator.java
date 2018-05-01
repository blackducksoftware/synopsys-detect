package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;

public abstract class RequirementEvaluator<T extends Requirement> {

    public abstract Class getRequirementClass();

    public abstract RequirementEvaluation evaluate(T requirement, EvaluationContext context);
}
