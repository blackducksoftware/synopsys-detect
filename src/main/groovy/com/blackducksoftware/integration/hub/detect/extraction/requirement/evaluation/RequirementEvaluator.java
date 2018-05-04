package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;

public abstract class RequirementEvaluator<T extends Requirement> {

    public abstract Class getRequirementClass();

    public abstract RequirementEvaluation evaluate(T requirement, EvaluationContext context);
}
