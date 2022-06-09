package com.synopsys.integration.detect.tool.detector.report.detectable;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.util.DetectorReportUtil;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class ExtractedDetectableReport {
    private final DetectableDefinition detectable;

    private final List<Explanation> explanations;
    private final List<File> relevantFiles; //just from appl/extr not from the extractions

    private final Extraction extraction;
    private final ExtractionEnvironment extractionEnvironment; //sadly neeeded for a diagnostic report.

    public ExtractedDetectableReport(
        DetectableDefinition detectable,
        List<Explanation> explanations,
        List<File> relevantFiles,
        Extraction extraction,
        ExtractionEnvironment extractionEnvironment
    ) {
        this.detectable = detectable;
        this.explanations = explanations;
        this.relevantFiles = relevantFiles;
        this.extraction = extraction;
        this.extractionEnvironment = extractionEnvironment;
    }

    public static ExtractedDetectableReport extracted(
        DetectableDefinition detectable,
        DetectableResult applicable,
        DetectableResult extractable,
        Extraction extraction,
        ExtractionEnvironment extractionEnvironment
    ) {
        return new ExtractedDetectableReport(
            detectable,
            DetectorReportUtil.combineExplanations(applicable, extractable),
            DetectorReportUtil.combineRelevantFiles(applicable, extractable),
            extraction,
            extractionEnvironment
        );
    }

    public DetectableDefinition getDetectable() {
        return detectable;
    }

    public List<Explanation> getExplanations() {
        return explanations;
    }

    public List<File> getRelevantFiles() {
        return relevantFiles;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public ExtractionEnvironment getExtractionEnvironment() {
        return extractionEnvironment;
    }
}
