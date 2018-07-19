package com.blackducksoftware.integration.hub.detect.workflow.report;

import java.util.List;

public class DetailedSearchSummaryData {
    private final String directory;
    private final List<DetailedSearchSummaryBomToolData> applicable;
    private final List<DetailedSearchSummaryBomToolData> notApplicable;
    private final List<DetailedSearchSummaryBomToolData> notSearchable;

    public DetailedSearchSummaryData(final String directory, final List<DetailedSearchSummaryBomToolData> applicable, final List<DetailedSearchSummaryBomToolData> notApplicable, final List<DetailedSearchSummaryBomToolData> notSearchable) {
        this.directory = directory;
        this.applicable = applicable;
        this.notApplicable = notApplicable;
        this.notSearchable = notSearchable;
    }

    public List<DetailedSearchSummaryBomToolData> getApplicable() {
        return applicable;
    }

    public List<DetailedSearchSummaryBomToolData> getNotApplicable() {
        return notApplicable;
    }

    public List<DetailedSearchSummaryBomToolData> getNotSearchable() {
        return notSearchable;
    }

    public String getDirectory() {
        return directory;
    }
}
