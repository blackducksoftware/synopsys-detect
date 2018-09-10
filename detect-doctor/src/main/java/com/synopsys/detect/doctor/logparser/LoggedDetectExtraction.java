package com.synopsys.detect.doctor.logparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggedDetectExtraction {
    public String extractionIdentifier;
    public String bomToolDescription;
    public String bomToolGroup;
    public String bomToolName;
    public Map<String, String> parameters = new HashMap<>();

    public String resultDescription;
    public String codeLocationDescription;

    public List<String> rawHeader = new ArrayList<>();
    public List<String> rawBody = new ArrayList<>();
    public List<String> rawFooter = new ArrayList<>();

}
