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

import com.synopsys.integration.detect.tool.detector.executable.ExecutedExecutable;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;

public class DiagnosticExecutableCapture {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private int executables = 0;
    private final File executableDirectory;
    private final Map<Integer, String> indexToCommand = new HashMap<>();

    public DiagnosticExecutableCapture(File executableDirectory, EventSystem eventSystem) {
        this.executableDirectory = executableDirectory;
        eventSystem.registerListener(Event.Executable, this::executableFinished);
    }

    private void executableFinished(ExecutedExecutable executed) {
        File errorOut = new File(executableDirectory, "EXE-" + executables + "-ERR.xout");
        File standardOut = new File(executableDirectory, "EXE-" + executables + "-STD.xout");
        indexToCommand.put(executables, executed.getExecutable().getExecutableDescription());

        try {
            FileUtils.writeStringToFile(errorOut, executed.getOutput().getErrorOutput(), Charset.defaultCharset());
            FileUtils.writeStringToFile(standardOut, executed.getOutput().getStandardOutput(), Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Failed to capture executable output.", e);
        }
        executables++;
    }

    public void finish() {
        if (indexToCommand.size() <= 0) {
            return;
        }

        AtomicReference<String> executableMap = new AtomicReference<>("");
        indexToCommand.forEach((key, value) -> executableMap.set(executableMap.get() + key + ": " + value + System.lineSeparator()));

        File mapFile = new File(executableDirectory, "EXE-MAP.txt");
        try {
            FileUtils.writeStringToFile(mapFile, executableMap.get(), Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("Failed to write executable map.", e);
        }
    }
}
