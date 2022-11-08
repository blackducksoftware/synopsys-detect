package com.synopsys.integration.detect.battery.util.assertions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.battery.util.DetectOutput;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticReportHandler;

public class DiagnosticAssert {
    private final File extractedDiagnosticZip;
    private final File reports;

    public static DiagnosticAssert fromDetectOutput(DetectOutput output) {
        Assertions.assertTrue(output.getExtractedDiagnosticZip().isPresent());
        File extractedDiagnosticZip = output.getExtractedDiagnosticZip().get();
        Assertions.assertTrue(extractedDiagnosticZip.exists());
        File reports = new File(extractedDiagnosticZip, "reports");
        Assertions.assertTrue(reports.exists());
        return new DiagnosticAssert(extractedDiagnosticZip, reports);
    }

    public DiagnosticAssert(File extractedDiagnosticZip, File reports) {
        this.extractedDiagnosticZip = extractedDiagnosticZip;
        this.reports = reports;
    }

    public OutputAssert getReport(DiagnosticReportHandler.ReportTypes reportType) {
        File reportFile = new File(reports, reportType.getReportFileName() + ".txt");
        Assertions.assertTrue(reportFile.exists());
        try {
            List<String> reportContents = FileUtils.readLines(reportFile, Charset.defaultCharset());
            return new OutputAssert(reportContents);
        } catch (IOException e) {
            Assertions.fail(e);
        }
        return null;
    }
}
