package com.blackducksoftware.integration.hub.detect.extraction.bomtool.conda;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class CondaCliStrategy extends Strategy<CondaCliContext, CondaCliExtractor> {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    public CondaCliStrategy() {
        super("Conda Cli", BomToolType.COCOAPODS, CondaCliContext.class, CondaCliExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final CondaCliContext context) {
        final File ymlFile = fileFinder.findFile(evaluation.getDirectory(), ENVIRONEMNT_YML);
        if (ymlFile == null) {
            return Applicable.doesNotApply("No environement.yml file was found with pattern: " + ENVIRONEMNT_YML);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final CondaCliContext context){
        final File conda = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (conda == null) {
            return Extractable.canNotExtract("No Conda executable was found.");
        }else {
            context.condaExe = conda;
        }

        return Extractable.canExtract();
    }


}
