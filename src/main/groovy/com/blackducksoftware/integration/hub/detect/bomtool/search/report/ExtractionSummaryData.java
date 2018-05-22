package com.blackducksoftware.integration.hub.detect.bomtool.search.report;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.extraction.StrategyEvaluation;

public class ExtractionSummaryData {
    public String directory;

    public List<StrategyEvaluation> success = new ArrayList<>();
    public List<StrategyEvaluation> failed = new ArrayList<>();
    public List<StrategyEvaluation> exception = new ArrayList<>();

    public int searchable;
    public int applicable;
    public int extractable;

    public int codeLocationsExtracted;
    public List<String> codeLocationNames = new ArrayList<>();
}
