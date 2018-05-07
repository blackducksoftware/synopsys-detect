package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.requirement.FileListRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class FileListRequirementEvaluator extends RequirementEvaluator<FileListRequirement> {

    @Autowired
    public DetectFileFinder detectFileFinder;

    @Override
    public RequirementEvaluation<List<File>> evaluate(final FileListRequirement requirement, final EvaluationContext context) {
        try {
            final List<File> files = new ArrayList<>();
            for (final String filepattern : requirement.filepatterns) {
                final List<File> found = detectFileFinder.findFiles(context.getDirectory(), filepattern);
                if (found != null) {
                    files.addAll(found);
                }
            }
            if (files != null && files.size() > 0) {
                return RequirementEvaluation.passed(files);
            } else {
                final String filepatternPrint = Arrays.asList(requirement.filepatterns).stream().map(it -> it.toString()).collect(Collectors.joining(","));
                return RequirementEvaluation.failed(null, "No files matched the patterns: " + filepatternPrint);
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(e);
        }
    }

    @Override
    public Class getRequirementClass() {
        return FileListRequirement.class;
    }
}
