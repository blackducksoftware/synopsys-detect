package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.File;

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
public class NpmCliStrategy extends Strategy<NpmCliContext, NpmCliExtractor>{
    public static final String NODE_MODULES= "node_modules";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public NpmExecutableFinder npmExecutableFinder;

    public NpmCliStrategy() {
        super("Npm Cli", BomToolType.NPM, NpmCliContext.class, NpmCliExtractor.class);
    }

    @Override
    public Applicable applicable(final EvaluationContext evaluation, final NpmCliContext context) {
        final File pom= fileFinder.findFile(evaluation.getDirectory(), NODE_MODULES);
        if (pom == null) {
            return Applicable.doesNotApply("No node_modules directory was found with pattern: " + NODE_MODULES);
        }

        return Applicable.doesApply();
    }

    @Override
    public Extractable extractable(final EvaluationContext evaluation, final NpmCliContext context){
        context.npmExe = npmExecutableFinder.findNpm(evaluation);

        if (context.npmExe == null) {
            return Extractable.canNotExtract("No npm executable was found.");
        }

        return Extractable.canExtract();
    }

}
