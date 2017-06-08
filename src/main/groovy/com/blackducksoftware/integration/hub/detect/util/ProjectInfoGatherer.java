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
package com.blackducksoftware.integration.hub.detect.util;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.type.BomToolType;

@Component
public class ProjectInfoGatherer {
    public static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    public String getDefaultProjectName(final BomToolType bomToolType, final String sourcePath) {
        return getDefaultProjectName(bomToolType, sourcePath, null);
    }

    public String getDefaultProjectName(final BomToolType bomToolType, final String sourcePath, final String defaultProjectName) {
        if (StringUtils.isNotBlank(defaultProjectName)) {
            return defaultProjectName;
        } else {
            final File sourcePathFile = new File(sourcePath);
            return String.format("%s_%s", sourcePathFile.getName(), bomToolType.toString().toLowerCase());
        }
    }

    public String getDefaultProjectVersionName() {
        return getDefaultProjectVersionName(null);
    }

    public String getDefaultProjectVersionName(final String defaultVersionName) {
        if (StringUtils.isNotBlank(defaultVersionName)) {
            return defaultVersionName;
        } else {
            return DateTime.now().toString(DATE_FORMAT);
        }
    }

}
