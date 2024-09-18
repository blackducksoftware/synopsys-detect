package com.blackduck.integration.detect.battery.util;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.blackduck.integration.detect.workflow.report.output.FormattedOutput;
import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.detect.battery.util.assertions.OutputAssert;

public class DetectOutput extends OutputAssert {
    private final File sourceDirectory;
    private final FormattedOutput statusJson;
    @Nullable
    private final File extractedDiagnosticZip;

    public DetectOutput(List<String> standardOutput, File sourceDirectory, FormattedOutput statusJson, @Nullable File extractedDiagnosticZip) {
        super(standardOutput);
        this.sourceDirectory = sourceDirectory;
        this.statusJson = statusJson;
        this.extractedDiagnosticZip = extractedDiagnosticZip;
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public FormattedOutput getStatusJson() {
        return statusJson;
    }

    public Optional<File> getExtractedDiagnosticZip() {
        return Optional.ofNullable(extractedDiagnosticZip);
    }
}
