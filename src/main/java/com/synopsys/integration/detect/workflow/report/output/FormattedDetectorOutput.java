package com.synopsys.integration.detect.workflow.report.output;

import java.util.ArrayList;
import java.util.List;

public class FormattedDetectorOutput {
    public String folder = "";
    public String detectorType = "";
    public String detectorName = "";
    public String descriptiveName = "";

    public boolean searchable = true;
    public boolean applicable = true;
    public boolean extractable = true;
    public boolean discoverable = true;
    public boolean extracted = true;

    public String searchableReason = "";
    public String applicableReason = "";
    public String extractableReason = "";
    public String discoveryReason = "";
    public String extractedReason = "";

    public List<String> relevantFiles = new ArrayList<>();

    public String projectName = "";
    public String projectVersion = "";
    public int codeLocationCount = 0;
}

