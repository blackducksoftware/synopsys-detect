package com.synopsys.integration.detectable.detectables.cran.parse;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

public class PackratDescriptionFileParser {
    private static final String PACKAGE_TOKEN = "Package:";
    private static final String VERSION_TOKEN = "Version:";

    public NameVersion getProjectNameVersion(List<String> descriptionFileLines, String defaultProjectName, String defaultProjectVersion) {
        NameVersion nameVersion = new NameVersion(defaultProjectName, defaultProjectVersion);

        for (String rawLine : descriptionFileLines) {
            String line = rawLine.trim();

            if (line.startsWith(PACKAGE_TOKEN)) {
                String projectName = line.replace(PACKAGE_TOKEN, "").trim();
                nameVersion.setName(projectName);
            } else if (line.startsWith(VERSION_TOKEN)) {
                String projectVersion = line.replace(VERSION_TOKEN, "").trim();
                nameVersion.setVersion(projectVersion);
            }
        }

        return nameVersion;
    }
}
