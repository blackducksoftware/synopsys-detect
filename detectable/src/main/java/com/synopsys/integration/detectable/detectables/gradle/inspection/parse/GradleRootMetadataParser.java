/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.DETECT_META_DATA_FOOTER;
import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.DETECT_META_DATA_HEADER;
import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.ROOT_PROJECT_NAME_PREFIX;
import static com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser.ROOT_PROJECT_VERSION_PREFIX;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.synopsys.integration.util.NameVersion;

// TODO: Improve later
public class GradleRootMetadataParser {
    public Optional<NameVersion> parseRootProjectNameVersion(final File rootProjectMetadataFile) {
        NameVersion nameVersion;
        String rootProjectName = null;
        String rootProjectVersionName = null;
        boolean processingMetaData = false;

        try (final FileInputStream dependenciesInputStream = new FileInputStream(rootProjectMetadataFile); final BufferedReader reader = new BufferedReader(new InputStreamReader(dependenciesInputStream, StandardCharsets.UTF_8))) {
            while (reader.ready()) {
                final String line = reader.readLine();

                if (line.startsWith(DETECT_META_DATA_HEADER)) {
                    processingMetaData = true;
                    continue;
                }
                if (line.startsWith(DETECT_META_DATA_FOOTER)) {
                    processingMetaData = false;
                    continue;
                }
                if (processingMetaData) {
                    if (line.startsWith(ROOT_PROJECT_NAME_PREFIX)) {
                        rootProjectName = line.substring(ROOT_PROJECT_NAME_PREFIX.length()).trim();
                    } else if (line.startsWith(ROOT_PROJECT_VERSION_PREFIX)) {
                        rootProjectVersionName = line.substring(ROOT_PROJECT_VERSION_PREFIX.length()).trim();
                    }
                }
            }
            nameVersion = new NameVersion(rootProjectName, rootProjectVersionName);
        } catch (final IOException e) {
            nameVersion = null;
        }

        return Optional.ofNullable(nameVersion);
    }
}
