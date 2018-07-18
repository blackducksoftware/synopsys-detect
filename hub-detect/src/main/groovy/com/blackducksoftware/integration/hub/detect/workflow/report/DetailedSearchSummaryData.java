package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class DetailedSearchSummaryData {
    public String directory;
    public List<BomToolSearchDetails> applicable;
    public List<BomToolSearchDetails> notApplicable;
    public List<BomToolSearchDetails> notSearchable;

    public DetailedSearchSummaryData() {
        applicable = new ArrayList<>();
        notApplicable = new ArrayList<>();
        notSearchable = new ArrayList<>();
    }

    public class BomToolSearchDetails {
        public BomTool bomTool;
        public String reason;
    }
}
