package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NugetSolutionStrategy extends Strategy<NugetInspectorContext, NugetInspectorExtractor> {
    static final String[] SUPPORTED_SOLUTION_PATTERNS = new String[] { "*.sln" };
    static final String SUPPORTED_SOLUTION_PATTERNS_DESC = Arrays.asList(SUPPORTED_SOLUTION_PATTERNS).stream().collect(Collectors.joining(","));

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public NugetInspectorManager nugetInspectorManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public NugetSolutionStrategy() {
        super("Solution", BomToolType.NUGET, NugetInspectorContext.class, NugetInspectorExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final NugetInspectorContext context) {
        for (final String filepattern : SUPPORTED_SOLUTION_PATTERNS) {
            if (fileFinder.findFile(evaluation.getDirectory(), filepattern) != null) {
                return Applicable.doesApply();
            }
        }
        return Applicable.doesNotApply("No files found with pattern: " + SUPPORTED_SOLUTION_PATTERNS_DESC);
    }

    public Extractable extractable(final EvaluationContext evaluation, final NugetInspectorContext context){
        context.inspectorExe = nugetInspectorManager.findNugetInspector(evaluation);

        if (context.inspectorExe == null) {
            return Extractable.canNotExtract("No Nuget executable was found.");
        }

        return Extractable.canExtract();
    }


}
