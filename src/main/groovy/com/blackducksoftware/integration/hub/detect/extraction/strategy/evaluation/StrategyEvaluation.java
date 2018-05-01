package com.blackducksoftware.integration.hub.detect.extraction.strategy.evaluation;

import java.util.HashMap;
import java.util.Map;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;

@SuppressWarnings("rawtypes")
public class StrategyEvaluation {

    public Map<Requirement, RequirementEvaluation> needEvaluationMap = new HashMap<>();
    public Map<Requirement, RequirementEvaluation> demandEvaluationMap = new HashMap<>();

    public void addNeedEvaluation(final Requirement<?> requirement, final RequirementEvaluation<?> requirementEvaluation) {
        needEvaluationMap.put(requirement, requirementEvaluation);
    }

    public RequirementEvaluation<?> getNeedEvaluation(final Requirement<?> requirement) {
        return needEvaluationMap.get(requirement);
    }

    public void addDemandEvaluation(final Requirement<?> requirement, final RequirementEvaluation<?> requirementEvaluation) {
        demandEvaluationMap.put(requirement, requirementEvaluation);
    }

    public RequirementEvaluation<?> getDemandEvaluation(final Requirement<?> requirement) {
        return demandEvaluationMap.get(requirement);
    }

    public boolean areNeedsMet() {
        boolean allPassed = true;
        for (final RequirementEvaluation evaluation : needEvaluationMap.values()) {
            allPassed = allPassed && evaluation.result == EvaluationResult.Passed;
        }
        return allPassed;
    }

    public boolean areDemandsMet() {
        boolean allPassed = true;
        for (final RequirementEvaluation evaluation : demandEvaluationMap.values()) {
            allPassed = allPassed && evaluation.result == EvaluationResult.Passed;
        }
        return allPassed;
    }


}
