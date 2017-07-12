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
package com.blackducksoftware.integration.hub.detect.bomtool.output

class DetectProject {
    private String projectName
    private String projectVersionName
    private String projectVersionHash
    private List<DetectCodeLocation> detectCodeLocations = []

    public String getProjectName() {
        projectName
    }

    public String getProjectVersionName() {
        projectVersionName
    }

    public void addAllDetectCodeLocations(List<DetectCodeLocation> detectCodeLocations) {
        detectCodeLocations.each { addDetectCodeLocation(it) }
    }

    public void addDetectCodeLocation(DetectCodeLocation detectCodeLocation) {
        if (!projectName) {
            projectName = detectCodeLocation.bomToolProjectName
        }

        if (!projectVersionName) {
            projectVersionName = detectCodeLocation.bomToolProjectVersionName
        }

        if (!projectVersionHash) {
            projectVersionHash = detectCodeLocation.bomToolFileHash
        }

        detectCodeLocations.add(detectCodeLocation)
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        detectCodeLocations
    }
}