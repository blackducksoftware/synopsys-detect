package com.synopsys.integration.detect.workflow.diagnostic;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DiagnosticFileCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int files = 0;
    private final File fileDirectory;
    private final Map<Integer, String> fileNames = new HashMap<>();

    public DiagnosticFileCapture(File fileDirectory, EventSystem eventSystem) {
        this.fileDirectory = fileDirectory;
        eventSystem.registerListener(Event.CustomerFileOfInterest, this::fileFound);
    }

    private void fileFound(File foundFile) {
        File savedFile = new File(fileDirectory, "FILE-" + files + "-" + foundFile.getName());
        fileNames.put(files, foundFile.toString());

        try {
            FileUtils.copyFile(foundFile, savedFile);
            logger.info("Saved file to diagnostics zip: " + foundFile);
        } catch (IOException e) {
            logger.error("Failed to copy file of interest.", e);
        }
        files++;
    }

    public void finish() {
        if (fileNames.size() <= 0) {
            return;
        }

        AtomicReference<String> executableMap = new AtomicReference<>("");
        fileNames.forEach((key, value) -> executableMap.set(executableMap.get() + key + ": " + value + System.lineSeparator()));

        File mapFile = new File(fileDirectory, "FILE-MAP.txt");
        try {
            FileUtils.writeStringToFile(mapFile, executableMap.get(), Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Failed to write executable map.", e);
        }
    }
}
