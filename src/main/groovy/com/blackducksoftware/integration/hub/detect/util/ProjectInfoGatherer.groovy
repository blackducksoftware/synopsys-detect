/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.detect.util

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class ProjectInfoGatherer {
    public static String DATE_FORMAT = 'yyyy-MM-dd\'T\'HH:mm:ss.SSS'

    @Autowired
    DetectConfiguration detectConfiguration

    String getProjectName(final BomToolType bomToolType, final String sourcePath) {
        getProjectName(bomToolType, sourcePath, null)
    }

    String getProjectName(final BomToolType bomToolType, final String sourcePath, final String defaultProjectName) {
        String projectName = defaultProjectName?.trim()

        if (detectConfiguration.getProjectName()) {
            projectName = detectConfiguration.getProjectName()
        } else if (!projectName && sourcePath) {
            final File sourcePathFile = new File(sourcePath)
            projectName = "${sourcePathFile.getName()}_${bomToolType.toString().toLowerCase()}"
        }

        projectName
    }

    String getProjectVersionName() {
        getProjectVersionName(null)
    }

    String getProjectVersionName(final String defaultVersionName) {
        String projectVersion = defaultVersionName?.trim()

        if (detectConfiguration.getProjectVersionName()) {
            projectVersion = detectConfiguration.getProjectVersionName()
        } else if (!projectVersion) {
            projectVersion = DateTime.now().toString(DATE_FORMAT)
        }

        projectVersion
    }

    String getCodeLocationName(final BomToolType bomToolType, final String projectName, final String projectVersion) {
        String codeLocationFormat = '%s/%s Hub Detect Export'
        String codeLocation = detectConfiguration.getProjectCodeLocationName()
        if (!codeLocation?.trim()) {
            codeLocation = String.format('%s/%s', projectName, projectVersion)
        }
        return String.format(codeLocationFormat, bomToolType, codeLocation)
    }
}
