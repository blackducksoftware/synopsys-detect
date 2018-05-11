package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepsContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepsExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoDepsStrategy extends Strategy<GoDepsContext, GoDepsExtractor> {
    public static final String GODEPS_DIRECTORYNAME = "Godeps";

    @Autowired
    public DetectFileFinder fileFinder;

    public GoDepsStrategy() {
        super("Go Deps Lock File", BomToolType.GO_GODEP, GoDepsContext.class, GoDepsExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final GoDepsContext context) {
        context.goDepsDirectory = fileFinder.findFile(evaluation.getDirectory(), GODEPS_DIRECTORYNAME);
        if (context.goDepsDirectory == null) {
            return Applicable.doesNotApply("No do deps folder was found matching: " + GODEPS_DIRECTORYNAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final GoDepsContext context){
        return Extractable.canExtract();
    }

}