/*
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
package com.blackducksoftware.integration.hub.detect.model

class DetectProject {
    private String projectName
    private String projectVersionName
    private List<DetectCodeLocation> detectCodeLocations = []

    public String getProjectName() {
        projectName
    }

    public String getProjectVersionName() {
        projectVersionName
    }

    public void setProjectNameIfNotSet(String projectName) {
        if (!this.projectName) {
            this.projectName = projectName
        }
    }
    public void setProjectVersionNameIfNotSet(String projectVersionName) {
        if (!this.projectVersionName) {
            this.projectVersionName = projectVersionName
        }
    }

    public void addAllDetectCodeLocations(List<DetectCodeLocation> detectCodeLocations) {
        detectCodeLocations.each { addDetectCodeLocation(it) }
    }

    public void addDetectCodeLocation(DetectCodeLocation detectCodeLocation) {
        setProjectNameIfNotSet(detectCodeLocation.bomToolProjectName)
        setProjectVersionNameIfNotSet(detectCodeLocation.bomToolProjectVersionName)

        detectCodeLocations.add(detectCodeLocation)
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        detectCodeLocations
    }

    //the implementation of these methods should NOT be changed - doing so will result in the code locations being added to the BOM instead of replacing the BOM
    @Deprecated
    public String getBomToolCodeLocationName(final BomToolType bomToolType, String finalSourcePathPiece, String prefix) {
        String codeLocation = String.format('%s/%s/%s/%s %s', bomToolType.toString(), finalSourcePathPiece, projectName, projectVersionName, 'Hub Detect Tool')
        if (prefix) {
            codeLocation = String.format('%s/%s', prefix, codeLocation)
        }
        codeLocation
    }

    @Deprecated
    public String getScanCodeLocationName(final String canonicalProjectSourcePath, final String canonicalCodeLocationSourcePath, String finalSourcePathPiece, String prefix) {
        String sourcePath = canonicalCodeLocationSourcePath.replace(canonicalProjectSourcePath, finalSourcePathPiece);
        String codeLocation = String.format('%s/%s/%s %s', sourcePath, projectName, projectVersionName, 'Hub Detect Scan')
        if (prefix) {
            codeLocation = String.format('%s/%s', prefix, codeLocation)
        }
        codeLocation
    }

    public String getBomToolCodeLocationName(String sourcePathPiece, BomToolType bomToolType, String prefix, String suffix) {
        getCodeLocationName(sourcePathPiece, bomToolType, CodeLocationType.BOM, prefix, suffix)
    }

    public String getScanCodeLocationName(String sourcePathPiece, String prefix, String suffix) {
        getCodeLocationName(sourcePathPiece, null, CodeLocationType.SCAN, prefix, suffix)
    }

    private String getCodeLocationName(String sourcePathPiece, BomToolType bomToolType, CodeLocationType codeLocationType, String prefix, String suffix) {
        String codeLocation = String.format('%s/%s/%s', sourcePathPiece, projectName, projectVersionName)
        if (prefix) {
            codeLocation = String.format('%s/%s', prefix, codeLocation)
        }
        if (suffix) {
            codeLocation = String.format('%s/%s', codeLocation, suffix)
        }
        String endPiece = codeLocationType.toString().toLowerCase()
        if (bomToolType != null) {
            endPiece = String.format('%s/%s', bomToolType.toString().toLowerCase(), endPiece)
        }
        codeLocation = String.format('%s %s', codeLocation, endPiece)
        codeLocation
    }
}