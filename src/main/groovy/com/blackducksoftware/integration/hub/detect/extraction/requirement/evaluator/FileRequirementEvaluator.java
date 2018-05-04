package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

@Component
public class FileRequirementEvaluator extends RequirementEvaluator<FileRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Override
    public RequirementEvaluation<File> evaluate(final FileRequirement requirement, final EvaluationContext context) {
        try {
            final File file = detectFileManager.findFile(context.getDirectory(), requirement.filename);
            if (file != null) {
                return RequirementEvaluation.passed( file);
            } else {
                return RequirementEvaluation.failed(null, "No file matched the pattern: " + requirement.filename);
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    @Override
    public Class getRequirementClass() {
        return FileRequirement.class;
    }
}
