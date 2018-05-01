package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileListRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation.EvaluationResult;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

@Component
public class FileListRequirementEvaluator extends RequirementEvaluator<FileListRequirement> {

    @Autowired
    public DetectFileManager detectFileManager;

    @Override
    public RequirementEvaluation<List<File>> evaluate(final FileListRequirement requirement, final EvaluationContext context) {
        try {
            final List<File> files = new ArrayList<>();
            for (final String filepattern : requirement.filepatterns) {
                final List<File> found = detectFileManager.findFiles(context.getDirectory(), filepattern);
                if (found != null) {
                    files.addAll(found);
                }
            }
            if (files != null && files.size() > 0) {
                return new RequirementEvaluation<>(EvaluationResult.Passed, files);
            } else {
                return new RequirementEvaluation<>(EvaluationResult.Failed, null);
            }
        }catch (final Exception e) {
            return new RequirementEvaluation<>(EvaluationResult.Exception, e);
        }
    }

    @Override
    public Class getRequirementClass() {
        return FileListRequirement.class;
    }
}
