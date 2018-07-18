package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;

public class PreparationSummaryData {
    public String directory;

    public List<BomToolEvaluation> ready = new ArrayList<>();
    public List<BomToolEvaluation> failed = new ArrayList<>();
}
