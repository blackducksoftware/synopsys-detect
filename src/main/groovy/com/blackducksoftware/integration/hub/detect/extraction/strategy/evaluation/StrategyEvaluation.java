package com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation;

import java.util.Map;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;

@SuppressWarnings("rawtypes")
public class StrategyEvaluation {

    public Map<Requirement, RequirementEvaluation> requirementEvaluationMap;
    public Map<Requirement, RequirementEvaluation> demandEvaluationMap;

    public void addRequirementEvaluation(final Requirement<?> requirement, final RequirementEvaluation<?> requirementEvaluation) {
        requirementEvaluationMap.put(requirement, requirementEvaluation);
    }

    public RequirementEvaluation<?> getRequirementEvaluation(final Requirement<?> requirement) {
        return requirementEvaluationMap.get(requirement);
    }

    public void addDemandEvaluation(final Requirement<?> requirement, final RequirementEvaluation<?> requirementEvaluation) {
        demandEvaluationMap.put(requirement, requirementEvaluation);
    }

    public boolean areRequirementsFulfilled() {
        boolean allPassed = true;
        for (final RequirementEvaluation evaluation : requirementEvaluationMap.values()) {
            allPassed = allPassed && evaluation.result == EvaluationResult.Passed;
        }
        return allPassed;
    }


}
