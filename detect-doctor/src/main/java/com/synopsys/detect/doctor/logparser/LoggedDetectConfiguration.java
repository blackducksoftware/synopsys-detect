package com.synopsys.detect.doctor.logparser;

import java.util.ArrayList;
import java.util.List;

public class LoggedDetectConfiguration {
    public String detectVersion;
    public List<LoggedDetectProperty> loggedPropertyList = new ArrayList<>();
    public List<LoggedDetectExtraction> extractions = new ArrayList<>();
}
