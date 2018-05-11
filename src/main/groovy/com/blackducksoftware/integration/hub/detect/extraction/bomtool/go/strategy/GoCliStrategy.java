package com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.strategy;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.extraction.Applicable;
import com.blackducksoftware.integration.hub.detect.extraction.Extractable;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepContext;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.strategy.Strategy;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

@Component
public class GoCliStrategy extends Strategy<GoDepContext, GoDepExtractor> {
    public static final String GOFILE_FILENAME_PATTERN = "*.go";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public GoInspectorManager goInspectorManager;

    @Autowired
    public StandardExecutableFinder standardExecutableFinder;


    public GoCliStrategy() {
        super("Go Cli", BomToolType.GO_DEP, GoDepContext.class, GoDepExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final GoDepContext context) {
        final List<File> found = fileFinder.findFiles(evaluation.getDirectory(), GOFILE_FILENAME_PATTERN);
        if (found == null || found.size() == 0) {
            return Applicable.doesNotApply("No go files were found matching: " + GOFILE_FILENAME_PATTERN);
        }

        return Applicable.doesApply();
    }

    public Extractable extractable(final EvaluationContext evaluation, final GoDepContext context){
        context.goExe = standardExecutableFinder.getExecutable(StandardExecutableType.GO);
        if (context.goExe == null) {
            return Extractable.canNotExtract("No Go executable was found.");
        }

        context.goDepInspector = goInspectorManager.evaluate(evaluation);
        if (context.goDepInspector == null) {
            return Extractable.canNotExtract("Go inspector was not found.");
        }

        return Extractable.canExtract();
    }

}