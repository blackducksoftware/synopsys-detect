package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;

public class ExtractionSummaryData {
    public String directory;

    public List<StrategyEvaluation> success;
    public List<StrategyEvaluation> failed;
    public List<StrategyEvaluation> exception;

    public int searchable;
    public int applicable;
    public int extractable;

    public int codeLocationsExtracted;
    public List<String> codeLocationNames;
}
