package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoVndrContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoVndrExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoVndrStrategy extends Strategy<GoVndrContext, GoVndrExtractor> {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    @Autowired
    public DetectFileFinder fileFinder;

    public GoVndrStrategy() {
        super("Vendor Config", BomToolType.GO_VNDR, GoVndrContext.class, GoVndrExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final GoVndrContext context) {
        context.vndrConfig = fileFinder.findFile(evaluation.getDirectory(), VNDR_CONF_FILENAME);
        if (context.vndrConfig == null) {
            return Applicable.doesNotApply("No vendor config file was found with pattern: " + VNDR_CONF_FILENAME);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final GoVndrContext context){
        return Extractable.canExtract();
    }

}