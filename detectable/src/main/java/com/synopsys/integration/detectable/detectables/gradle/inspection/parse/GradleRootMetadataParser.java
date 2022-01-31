package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.DETECT_META_DATA_FOOTER;
import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.DETECT_META_DATA_HEADER;
import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.ROOT_PROJECT_NAME_PREFIX;
import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.ROOT_PROJECT_VERSION_PREFIX;

import java.util.List;

import com.synopsys.integration.util.NameVersion;

public class GradleRootMetadataParser {
    public NameVersion parseRootProjectNameVersion(List<String> rootProjectMetadataLines) {
        String rootProjectName = null;
        String rootProjectVersionName = null;
        boolean processingMetaData = false;

        for (String line : rootProjectMetadataLines) {
            if (line.startsWith(DETECT_META_DATA_HEADER)) {
                processingMetaData = true;
            } else if (line.startsWith(DETECT_META_DATA_FOOTER)) {
                processingMetaData = false;
            } else if (processingMetaData) {
                if (line.startsWith(ROOT_PROJECT_NAME_PREFIX)) {
                    rootProjectName = line.substring(ROOT_PROJECT_NAME_PREFIX.length()).trim();
                } else if (line.startsWith(ROOT_PROJECT_VERSION_PREFIX)) {
                    rootProjectVersionName = line.substring(ROOT_PROJECT_VERSION_PREFIX.length()).trim();
                }
            }
        }
        return new NameVersion(rootProjectName, rootProjectVersionName);
    }
}
