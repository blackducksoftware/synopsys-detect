package com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer;

import java.io.File;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface ProjectInspectorInstaller {
    @Nullable File install(File destDirectory) throws DetectableException;

    boolean shouldFallbackToPreviousInstall();
}
