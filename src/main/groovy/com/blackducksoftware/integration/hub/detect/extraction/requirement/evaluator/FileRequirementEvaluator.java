package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

public class FileRequirementEvaluator {

    public DetectFileManager detectFileManager;

    public RequirementEvaluation<File> evaluate(final FileRequirement requirement, final EvaluationContext context) {
        try {
            final File file = detectFileManager.findFile(context.getDirectory(), requirement.filename);
            if (file != null) {
                return new RequirementEvaluation<>();
            } else {
                return new RequirementEvaluation<>();
            }
        }catch (final Exception e) {
            return new RequirementEvaluation<>();
        }
    }
}
