package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class RebarStrategy extends Strategy<RebarContext, RebarExtractor> {
    public static final String REBAR_CONFIG = "rebar.config";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;

    public RebarStrategy() {
        super("Rebar Config", BomToolType.HEX, RebarContext.class, RebarExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final RebarContext context) {
        final File rebar = fileFinder.findFile(evaluation.getDirectory(), REBAR_CONFIG);
        if (rebar == null) {
            return Applicable.doesNotApply("No rebar config file was found with pattern: " + REBAR_CONFIG);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final RebarContext context){
        context.rebarExe = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (context.rebarExe == null) {
            return Extractable.canNotExtract("No Rebar executable was found.");
        }

        return Extractable.canExtract();
    }

}