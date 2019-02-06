package com.synopsys.integration.detectable.detectables.gradle.model;

import java.util.ArrayList;
import java.util.List;

public class GradleReport {
    public String projectSourcePath = "";
    public String projectGroup = "";
    public String projectName = "";
    public String projectVersionName = "";
    public List<GradleConfiguration> configurations = new ArrayList<>();
}
