package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.extraction.result.FilesNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.InspectorNotFoundStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.PassedStrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.result.StrategyResult;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class NugetSolutionStrategy extends Strategy<NugetInspectorContext, NugetInspectorExtractor> {
    static final String[] SUPPORTED_SOLUTION_PATTERNS = new String[] { "*.sln" };

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public NugetInspectorManager nugetInspectorManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public NugetSolutionStrategy() {
        super("Solution", BomToolType.NUGET, NugetInspectorContext.class, NugetInspectorExtractor.class);
    }

    @Override
    public StrategyResult applicable(final StrategyEnvironment environment, final NugetInspectorContext context) {
        for (final String filepattern : SUPPORTED_SOLUTION_PATTERNS) {
            if (fileFinder.findFile(environment.getDirectory(), filepattern) != null) {
                return new PassedStrategyResult();
            }
        }
        return new FilesNotFoundStrategyResult(SUPPORTED_SOLUTION_PATTERNS);
    }

    @Override
    public StrategyResult extractable(final StrategyEnvironment environment, final NugetInspectorContext context){
        context.inspectorExe = nugetInspectorManager.findNugetInspector(environment);

        if (context.inspectorExe == null) {
            return new InspectorNotFoundStrategyResult("nuget");
        }

        return new PassedStrategyResult();
    }


}
