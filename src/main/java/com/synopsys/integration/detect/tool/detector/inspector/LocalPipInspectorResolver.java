package com.synopsys.integration.detect.tool.detector.inspector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;

public class LocalPipInspectorResolver implements PipInspectorResolver {
    public static final String INSPECTOR_NAME = "pip-inspector.py";

    private final DirectoryManager directoryManager;

    private File resolvedInspector = null;
    private boolean hasResolvedInspector = false;

    public LocalPipInspectorResolver(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    @Override
    public File resolvePipInspector() throws DetectableException {
        try {
            if (!hasResolvedInspector) {
                hasResolvedInspector = true;
                resolvedInspector = installInspector();
            }
            return resolvedInspector;
        } catch (Exception e) {
            throw new DetectableException(e);
        }
    }

    private File installInspector() throws IOException {
        String inspectorScriptContents;
        try (InputStream inspectorFileStream = getClass().getResourceAsStream(String.format("/%s", INSPECTOR_NAME))) {
            inspectorScriptContents = IOUtils.toString(inspectorFileStream, StandardCharsets.UTF_8);
        }
        File inspectorScript = directoryManager.getSharedFile("pip", INSPECTOR_NAME); //Moved the file getting so the pip folder would not be created every time. -jp
        FileUtils.write(inspectorScript, inspectorScriptContents, StandardCharsets.UTF_8);
        return inspectorScript;
    }

}
