package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import com.blackducksoftware.integration.hub.detect.extraction.bucket.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.ExecutableRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;

public class RequirementEvaluator {

    public Evaluation evaluateRequirement(final Requirement requirement, final EvaluationContext context) {
        throw new RuntimeException("Unknown requirement " + requirement.getClass());
    }

    public Evaluation evaluateRequirement(final FileRequirement requirement, final EvaluationContext context) {

    }

    public Evaluation evaluateRequirement(final ExecutableRequirement requirement, final EvaluationContext context) {

    }


    public class EvaluationContext {

    }
}
