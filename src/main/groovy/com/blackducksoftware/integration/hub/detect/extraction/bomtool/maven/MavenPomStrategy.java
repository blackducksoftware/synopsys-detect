package com.blackducksoftware.integration.hub.detect.extraction.bomtool.maven;

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
public class MavenPomStrategy extends Strategy<MavenCliContext, MavenCliExtractor> {
    public static final String POM_WRAPPER_FILENAME= "pom.groovy";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public MavenExecutableFinder mavenExecutableFinder;

    public MavenPomStrategy() {
        super("Pom file", BomToolType.MAVEN, MavenCliContext.class, MavenCliExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final MavenCliContext context) {
        final File pom= fileFinder.findFile(evaluation.getDirectory(), POM_WRAPPER_FILENAME);
        if (pom == null) {
            return Applicable.doesNotApply("No environment.yml file was found with pattern: " + POM_WRAPPER_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final MavenCliContext context){
        context.mavenExe = mavenExecutableFinder.findMaven(evaluation);

        if (context.mavenExe == null) {
            return Extractable.canNotExtract("No MVN executable was found.");
        }

        return Extractable.canExtract();
    }


}
