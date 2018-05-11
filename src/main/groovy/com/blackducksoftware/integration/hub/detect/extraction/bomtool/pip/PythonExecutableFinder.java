package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.requirement.evaluation.EvaluationContext;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PythonExecutableFinder {
    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableManager executableManager;

    @Autowired
    public ExecutableRunner executableRunner;

    private String resolvedPython = null;
    private boolean hasLookedForPython = false;

    public String findPython(final EvaluationContext context) {
        try {
            if (!hasLookedForPython) {
                hasLookedForPython = true;
                ExecutableType pythonType = ExecutableType.PYTHON;
                if (detectConfiguration.getPythonThreeOverride()) {
                    pythonType = ExecutableType.PYTHON3;
                }
                resolvedPython = executableManager.getExecutablePathOrOverride(pythonType, true, context.getDirectory(), null);
            }
            return resolvedPython;
        }catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
