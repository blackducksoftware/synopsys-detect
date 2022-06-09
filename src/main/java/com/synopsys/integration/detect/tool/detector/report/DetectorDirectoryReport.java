package com.synopsys.integration.detect.tool.detector.report;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.rule.EvaluatedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.ExtractedDetectorRuleReport;
import com.synopsys.integration.detect.tool.detector.report.rule.NotFoundDetectorRuleReport;

public class DetectorDirectoryReport {
    private final File directory;
    private final int depth;

    private final List<NotFoundDetectorRuleReport> notFoundDetectors; //not relevant - ie, was not searchable or no entry point applied
    private final List<ExtractedDetectorRuleReport> extractedDetectors; //exactly one extraction - ie one entry point applied and one detectable returned a successful extraction
    private final List<EvaluatedDetectorRuleReport> notExtractedDetectors; // failure - no successful extraction - ie, no detectable was extractable, or all detectables failed to extract

    public DetectorDirectoryReport(
        File directory,
        int depth,
        List<NotFoundDetectorRuleReport> notFoundDetectors,
        List<ExtractedDetectorRuleReport> extractedDetectors,
        List<EvaluatedDetectorRuleReport> notExtractedDetectors
    ) {
        this.directory = directory;
        this.depth = depth;
        this.notFoundDetectors = notFoundDetectors;
        this.extractedDetectors = extractedDetectors;
        this.notExtractedDetectors = notExtractedDetectors;
    }

    public File getDirectory() {
        return directory;
    }

    public int getDepth() {
        return depth;
    }

    public List<NotFoundDetectorRuleReport> getNotFoundDetectors() {
        return notFoundDetectors;
    }

    public List<ExtractedDetectorRuleReport> getExtractedDetectors() {
        return extractedDetectors;
    }

    public List<EvaluatedDetectorRuleReport> getNotExtractedDetectors() {
        return notExtractedDetectors;
    }

    public boolean anyFound() {
        return !getExtractedDetectors().isEmpty() || !getNotExtractedDetectors().isEmpty();
    }
}
