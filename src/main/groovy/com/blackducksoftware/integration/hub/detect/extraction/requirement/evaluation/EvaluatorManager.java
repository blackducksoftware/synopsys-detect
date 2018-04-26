package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.bucket.Requirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;

@Component
public class EvaluatorManager {


    @Autowired
    public FileRequirementEvaluator fileRequirementEvaluator;


    public Evaluation<?> evaluate(final Requirement<?, ?> requirement, final EvaluationContext context) {
        throw new RuntimeException("Unknown requirement " + requirement.getClass());
    }

    public Evaluation<?> evaluate(final FileRequirement<?> requirement, final EvaluationContext context) {
        return fileRequirementEvaluator.evaluate(requirement, context);
    }

    //public Evaluation<?> evaluate(final ExecutableRequirement requirement, final EvaluationContext context) {

    //}

}
