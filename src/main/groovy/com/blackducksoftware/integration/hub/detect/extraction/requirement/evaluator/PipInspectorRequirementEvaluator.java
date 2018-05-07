package com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.PipInspectorRequirement;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluation;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.RequirementEvaluator;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PipInspectorRequirementEvaluator extends RequirementEvaluator<PipInspectorRequirement> {
    public static final String INSPECTOR_NAME = "pip-inspector.py";

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private File resolvedInspector = null;
    private boolean hasResolvedInspector = false;

    @Override
    public RequirementEvaluation<File> evaluate(final PipInspectorRequirement requirement, final EvaluationContext context) {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedInspector = installInspector();
            }
            if (resolvedInspector != null) {
                return RequirementEvaluation.passed( resolvedInspector);
            }else {
                return RequirementEvaluation.failed(null, "No Pip inspector was found.");
            }
        }catch (final Exception e) {
            return RequirementEvaluation.error(null);
        }
    }

    private File installInspector() throws IOException {
        final InputStream insptectorFileStream = getClass().getResourceAsStream(String.format("/%s", INSPECTOR_NAME));
        final String inpsectorScriptContents = IOUtils.toString(insptectorFileStream, StandardCharsets.UTF_8);
        final File inspectorScript = detectFileManager.createSharedFile("pip", INSPECTOR_NAME);
        return detectFileManager.writeToFile(inspectorScript, inpsectorScriptContents);
    }

    @Override
    public Class getRequirementClass() {
        return PipInspectorRequirement.class;
    }
}