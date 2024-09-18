package com.blackduck.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

public class GradleRootMetadataParser {
    public NameVersion parseRootProjectNameVersion(List<String> rootProjectMetadataLines) {
        String rootProjectName = null;
        String rootProjectVersionName = null;
        boolean processingMetaData = false;

        for (String line : rootProjectMetadataLines) {
            if (line.startsWith(GradleReportParser.DETECT_META_DATA_HEADER)) {
                processingMetaData = true;
            } else if (line.startsWith(GradleReportParser.DETECT_META_DATA_FOOTER)) {
                processingMetaData = false;
            } else if (processingMetaData) {
                if (line.startsWith(GradleReportParser.ROOT_PROJECT_NAME_PREFIX)) {
                    rootProjectName = line.substring(GradleReportParser.ROOT_PROJECT_NAME_PREFIX.length()).trim();
                } else if (line.startsWith(GradleReportParser.ROOT_PROJECT_VERSION_PREFIX)) {
                    rootProjectVersionName = line.substring(GradleReportParser.ROOT_PROJECT_VERSION_PREFIX.length()).trim();
                }
            }
        }
        return new NameVersion(rootProjectName, rootProjectVersionName);
    }
}
