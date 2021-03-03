package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class SbtProjectParser {
    //task / projectID
    //  group:name:version
    public SbtProjects parseProjectIDOutput(List<String> output) throws DetectableException {
        SbtProject rootProject = null;
        Map<String, SbtProject> additionalProjects = new HashMap<>();
        for (int i = 0; i < output.size(); i++) {
            String line = output.get(i).trim();
            if (line.endsWith(" / projectID")) {
                String taskName = StringUtils.substringBefore(line, " / projectID");
                SbtProject additionalProject = parseFromLine(output.get(i + 1));
                additionalProjects.put(taskName, additionalProject);
            } else if (line.equals("projectID")) {
                rootProject = parseFromLine(output.get(i + 1));
            }
        }
        return new SbtProjects(rootProject, additionalProjects);
    }

    public SbtProject parseFromLine(String line) throws DetectableException {
        String[] pieces = line.trim().split(":");
        if (pieces.length != 3) {
            throw new DetectableException("Unable to parse SBT project ID: " + line);
        }
        return new SbtProject(pieces[0], pieces[1], pieces[2]);
    }
}
