/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.packman.PackageManagerType;

@Component
public class ProjectInfoGatherer {

    @Value("${packman.project.name}")
    String projectName;

    @Value("${packman.project.version}")
    String projectVersion;

    public String getRawProjectName() {
        return projectName;
    }

    public String getProjectName(final PackageManagerType packageManagerType, final String sourcePath) {
        return getProjectName(packageManagerType, sourcePath, null);
    }

    public String getProjectName(final PackageManagerType packageManagerType, final String sourcePath, final String defaultName) {
        String projectName;
        if (shouldAggregate()) {
            projectName = this.projectName;
        } else if (StringUtils.isNotBlank(defaultName)) {
            projectName = defaultName;
        } else {
            final File sourcePathFile = new File(sourcePath);
            projectName = String.format("%s_%s", sourcePathFile.getName(), packageManagerType.toString().toLowerCase());
        }
        return projectName;
    }

    public String getProjectVersion() {
        return getProjectVersion(null);
    }

    public String getProjectVersion(final String defaultVersion) {
        String projectVersion;
        if (StringUtils.isNotBlank(this.projectVersion)) {
            projectVersion = this.projectVersion;
        } else if (StringUtils.isNotBlank(defaultVersion)) {
            projectVersion = defaultVersion;
        } else {
            projectVersion = DateTime.now().toString("MM-dd-YYYY_HH:mm:Z");
        }
        return projectVersion;
    }

    public boolean shouldAggregate() {
        return StringUtils.isNotBlank(projectName);
    }
}
