package com.blackducksoftware.integration.hub.detect.extraction.bomtool.rubygems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GemlockStrategy extends Strategy<GemlockContext, GemlockExtractor> {
    public static final String GEMFILE_LOCK_FILENAME = "Gemfile.lock";

    @Autowired
    public DetectFileFinder fileFinder;

    public GemlockStrategy() {
        super("Gemlock", BomToolType.RUBYGEMS, GemlockContext.class, GemlockExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final GemlockContext context) {
        context.gemlock = fileFinder.findFile(evaluation.getDirectory(), GEMFILE_LOCK_FILENAME);
        if (context.gemlock == null) {
            return Applicable.doesNotApply("No gemlock file was found with pattern: " + GEMFILE_LOCK_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final GemlockContext context){
        return Extractable.canExtract();
    }

}