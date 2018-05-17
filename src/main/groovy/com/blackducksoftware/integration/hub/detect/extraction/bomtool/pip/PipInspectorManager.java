package com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyEnvironment;
import com.blackducksoftware.integration.hub.detect.strategy.evaluation.StrategyException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class PipInspectorManager {
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

    public File findPipInspector(final StrategyEnvironment environment) throws StrategyException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedInspector = installInspector();
            }
            return resolvedInspector;
        }catch (final Exception e) {
            throw new StrategyException(e);
        }
    }

    private File installInspector() throws IOException {
        final InputStream insptectorFileStream = getClass().getResourceAsStream(String.format("/%s", INSPECTOR_NAME));
        final String inpsectorScriptContents = IOUtils.toString(insptectorFileStream, StandardCharsets.UTF_8);
        final File inspectorScript = detectFileManager.createSharedFile("pip", INSPECTOR_NAME);
        return detectFileManager.writeToFile(inspectorScript, inpsectorScriptContents);
    }

}
