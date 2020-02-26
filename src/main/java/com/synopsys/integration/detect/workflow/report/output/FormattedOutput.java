package com.synopsys.integration.detect.workflow.report.output;

import java.util.ArrayList;
import java.util.List;

public class FormattedOutput {
    public String formatVersion = "";
    public String detectVersion = "";
    public List<FormattedDetectorOutput> detectors = new ArrayList<>();

    public List<FormattedStatusOutput> status = new ArrayList<>();
    public List<FormattedIssueOutput> issues = new ArrayList<>();
    public List<FormattedResultOutput> results = new ArrayList<>();
}

