package com.synopsys.integration.detect.battery.accuracy;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detect.battery.util.DetectorBatteryTestRunner;
import com.synopsys.integration.detect.battery.util.assertions.DiagnosticAssert;
import com.synopsys.integration.detect.battery.util.assertions.OutputAssert;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticReportHandler;

@Tag("battery")
class CascadeDiagnosticReportBatteryTests {
    @Test
    void detectorReportGenerated() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("detector-report", "none");
        test.setManualCleanup(true); //Need to keep diagnostics around long enough to assert.
        test.enableDiagnostics();
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "bad{json}");
        test.sourceFileNamed("npm-shrinkwrap.json", "bad{json}");
        DiagnosticAssert diagnostics = DiagnosticAssert.fromDetectOutput(test.run());

        OutputAssert detectorReport = diagnostics.getReport(DiagnosticReportHandler.ReportTypes.DETECTOR);
        detectorReport.assertContains("DETECTOR: NPM");
        detectorReport.assertContainsBlock("ATTEMPTED: NPM Shrinkwrap", "REASON: JsonSyntaxException");
        detectorReport.assertContainsBlock("ATTEMPTED: NPM CLI", "REASON: A package.json was located");
        detectorReport.assertContainsBlock("ATTEMPTED: NPM Package Json Parse", "REASON: JsonSyntaxException");

        OutputAssert searchReport = diagnostics.getReport(DiagnosticReportHandler.ReportTypes.SEARCH);
        searchReport.assertContains("FOUND: NPM");

        OutputAssert searchDetailedReport = diagnostics.getReport(DiagnosticReportHandler.ReportTypes.SEARCH_DETAILED);

        searchDetailedReport.assertContains("ATTEMPTED: NPM - NPM CLI - NPM_NODE_MODULES_NOT_FOUND");
        searchDetailedReport.assertContains("ATTEMPTED: NPM - NPM Package Json Parse - EXTRACTION_FAILED");
        searchDetailedReport.assertContains("ATTEMPTED: NPM - NPM Shrinkwrap - EXTRACTION_FAILED");

        searchDetailedReport.assertContains("NOT FOUND: GRADLE");
        searchDetailedReport.assertContains("Gradle Native Inspector: No files were found");
        test.cleanup();
    }

    @Test
    void detectorCompleteReportGenerated() {
        DetectorBatteryTestRunner test = new DetectorBatteryTestRunner("detector-complete-report", "none");
        test.setManualCleanup(true); //Need to keep diagnostics around long enough to assert.
        test.enableDiagnostics();
        test.withToolsValue("DETECTOR");
        test.sourceDirectoryNamed("npm");
        test.sourceFileNamed("package.json", "{ \"dependencies\": {\"got\": \"^12.1.0\"} }");
        test.sourceFileNamed("npm-shrinkwrap.json", "bad{json}");
        DiagnosticAssert diagnostics = DiagnosticAssert.fromDetectOutput(test.run());

        OutputAssert detectorReport = diagnostics.getReport(DiagnosticReportHandler.ReportTypes.DETECTOR);
        detectorReport.assertContains("DETECTOR: NPM");
        detectorReport.assertContainsBlock("EXTRACTED: NPM Package Json Parse", "EXTRACTION: 1 Code Locations", "Found file:");
        detectorReport.assertContainsBlock("ATTEMPTED: NPM Shrinkwrap", "REASON: JsonSyntaxException");
        detectorReport.assertContainsBlock("ATTEMPTED: NPM CLI", "REASON: A package.json was located");

        OutputAssert searchReport = diagnostics.getReport(DiagnosticReportHandler.ReportTypes.SEARCH);
        searchReport.assertContains("FOUND: NPM");

        OutputAssert searchDetailedReport = diagnostics.getReport(DiagnosticReportHandler.ReportTypes.SEARCH_DETAILED);
        searchDetailedReport.assertContains("EXTRACTED: NPM - NPM Package Json Parse");
        searchDetailedReport.assertContains("ATTEMPTED: NPM - NPM Shrinkwrap - EXTRACTION_FAILED");
        searchDetailedReport.assertContains("ATTEMPTED: NPM - NPM CLI - NPM_NODE_MODULES_NOT_FOUND");
        test.cleanup();
    }
}

