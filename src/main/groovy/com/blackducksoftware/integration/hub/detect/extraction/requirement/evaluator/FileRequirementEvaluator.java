package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileRequirement;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

public class FileRequirementEvaluator {

    public DetectFileManager detectFileManager;

    public Evaluation<File> evaluate(final FileRequirement<?> requirement, final EvaluationContext context) {
        try {
            final File file = detectFileManager.findFile(context.getDirectory(), requirement.filename);
            if (file != null) {
                return new Evaluation<>();
            } else {
                return new Evaluation<>();
            }
        }catch (final Exception e) {
            return new Evaluation<>();
        }
    }
}
