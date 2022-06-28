package com.synopsys.integration.detect.tool.detector.report.detectable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detect.tool.detector.report.DetectorStatusUtil;
import com.synopsys.integration.detect.tool.detector.report.util.DetectorReportUtil;
import com.synopsys.integration.detectable.detectable.explanation.Explanation;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detector.base.DetectorStatusCode;
import com.synopsys.integration.detector.result.DetectorResult;
import com.synopsys.integration.detector.rule.DetectableDefinition;

public class AttemptedDetectableReport { //Tosses some info from failed extractions (metaData, unrecognizedpaths, relevant files, may be worth it to keep? might be easier to hold onto the extraction? a passed extraction is kept (see ExtractedDetectableReport)
    private final DetectableDefinition detectable;
    private final List<Explanation> explanations;
    private final List<File> relevantFiles;

    private final DetectorStatusCode statusCode;
    private final String statusReason;

    public AttemptedDetectableReport(
        DetectableDefinition detectable,
        List<Explanation> explanations,
        List<File> relevantFiles,
        DetectorStatusCode statusCode,
        String statusReason
    ) {
        this.detectable = detectable;
        this.explanations = explanations;
        this.relevantFiles = relevantFiles;
        this.statusCode = statusCode;
        this.statusReason = statusReason;
    }

    public static AttemptedDetectableReport notApplicable(DetectableDefinition detectable, DetectableResult applicable) {
        return new AttemptedDetectableReport(
            detectable,
            applicable.getExplanation(),
            applicable.getRelevantFiles(),
            DetectorStatusUtil.getStatusCode(applicable),
            applicable.toDescription()
        );
    }

    public static AttemptedDetectableReport notExtractable(DetectableDefinition detectable, DetectableResult applicable, DetectableResult extractable) {
        return new AttemptedDetectableReport(
            detectable,
            DetectorReportUtil.combineExplanations(applicable, extractable),
            DetectorReportUtil.combineRelevantFiles(applicable, extractable),
            DetectorStatusUtil.getStatusCode(extractable),
            extractable.toDescription()
        );
    }

    public static AttemptedDetectableReport notExtracted(DetectableDefinition detectable, DetectableResult applicable, DetectableResult extractable, Extraction extraction) {
        return new AttemptedDetectableReport(
            detectable,
            DetectorReportUtil.combineExplanations(applicable, extractable),
            DetectorReportUtil.combineRelevantFiles(applicable, extractable),
            DetectorStatusUtil.getFailedStatusCode(extraction),
            DetectorStatusUtil.getFailedStatusReason(extraction)
        );
    }

    public static AttemptedDetectableReport notSearchable(DetectableDefinition detectable, DetectorResult searchable) {
        return new AttemptedDetectableReport(
            detectable,
            new ArrayList<>(),
            new ArrayList<>(),
            DetectorStatusUtil.getStatusCode(searchable),
            searchable.getDescription()
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

    public DetectorStatusCode getStatusCode() {
        return statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }
}
