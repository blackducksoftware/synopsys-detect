package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import org.apache.commons.lang3.StringUtils;
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
public class PipInspectorStrategy extends Strategy<PipInspectorContext, PipInspectorExtractor> {
    public static final String SETUP_FILE_NAME= "setup.py";

    @Autowired
    public DetectFileFinder fileFinder;

    @Autowired
    public PipExecutableFinder pipExecutableFinder;

    @Autowired
    public PythonExecutableFinder pythonExecutableFinder;

    @Autowired
    public PipInspectorManager pipInspectorManager;


    @Autowired
    public DetectConfiguration detectConfiguration;

    public PipInspectorStrategy() {
        super("Pip Inspector", BomToolType.PIP, PipInspectorContext.class, PipInspectorExtractor.class);
    }

    public Applicable applicable(final EvaluationContext evaluation, final PipInspectorContext context) {
        context.setupFile = fileFinder.findFile(evaluation.getDirectory(), SETUP_FILE_NAME);
        context.requirementFilePath = detectConfiguration.getRequirementsFilePath();

        final boolean hasSetups = context.setupFile != null;
        final boolean hasRequirements = context.requirementFilePath != null && StringUtils.isNotBlank(context.requirementFilePath);
        if (hasSetups || hasRequirements) {
            return Applicable.doesApply();
        } else {
            return Applicable.doesNotApply("No requirements file or setup file matching pattern: " + SETUP_FILE_NAME);
        }

    }

    public Extractable extractable(final EvaluationContext evaluation, final PipInspectorContext context){
        final String pipExe = pipExecutableFinder.findPip(evaluation);
        if (pipExe == null) {
            return Extractable.canNotExtract("No pip executable was found.");
        }

        context.pythonExe = pythonExecutableFinder.findPython(evaluation);
        if (context.pythonExe == null) {
            return Extractable.canNotExtract("No python executable was found.");
        }

        context.pipInspector = pipInspectorManager.findPipInspector(evaluation);
        if (context.pipInspector == null) {
            return Extractable.canNotExtract("No pip inspector was found.");
        }

        return Extractable.canExtract();
    }


}
