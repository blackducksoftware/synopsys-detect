package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;

public class DetailedSearchSummaryData {
    private String directory;
    private final List<BomToolSearchDetails> applicable;
    private final List<BomToolSearchDetails> notApplicable;
    private final List<BomToolSearchDetails> notSearchable;

    public DetailedSearchSummaryData(final String directory) {
        applicable = new ArrayList<>();
        notApplicable = new ArrayList<>();
        notSearchable = new ArrayList<>();
    }

    public void addApplicable(final BomTool bomTool, final String reason) {
        applicable.add(new BomToolSearchDetails(bomTool, reason));
    }

    public void addNotApplicable(final BomTool bomTool, final String reason) {
        notApplicable.add(new BomToolSearchDetails(bomTool, reason));
    }

    public void addNotSearchable(final BomTool bomTool, final String reason) {
        notSearchable.add(new BomToolSearchDetails(bomTool, reason));
    }

    public List<BomToolSearchDetails> getApplicable() {
        return applicable;
    }

    public List<BomToolSearchDetails> getNotApplicable() {
        return notApplicable;
    }

    public List<BomToolSearchDetails> getNotSearchable() {
        return notSearchable;
    }

    public String getDirectory() {
        return directory;
    }

    public class BomToolSearchDetails {
        private final BomTool bomTool;
        private final String reason;

        public BomToolSearchDetails(final BomTool bomTool, final String reason) {
            this.bomTool = bomTool;
            this.reason = reason;
        }

        public BomTool getBomTool() {
            return bomTool;
        }

        public String getReason() {
            return reason;
        }
    }
}
