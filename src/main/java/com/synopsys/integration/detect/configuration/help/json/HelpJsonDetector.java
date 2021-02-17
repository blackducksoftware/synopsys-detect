/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.configuration.help.json;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonDetector {
    private String detectableLanguage = "";
    private String detectableRequirementsMarkdown = "";
    private String detectableForge = "";
    private String detectorType = "";
    private String detectorName = "";
    private String detectorDescriptiveName = "";
    private Integer maxDepth = 0;
    private Boolean nestable = false;
    private Boolean nestInvisible = false;

    private List<String> yieldsTo = new ArrayList<>();
    private String fallbackTo = "";

    public String getDetectableLanguage() {
        return detectableLanguage;
    }

    public void setDetectableLanguage(final String detectableLanguage) {
        this.detectableLanguage = detectableLanguage;
    }

    public String getDetectableRequirementsMarkdown() {
        return detectableRequirementsMarkdown;
    }

    public void setDetectableRequirementsMarkdown(final String detectableRequirementsMarkdown) {
        this.detectableRequirementsMarkdown = detectableRequirementsMarkdown;
    }

    public String getDetectableForge() {
        return detectableForge;
    }

    public void setDetectableForge(final String detectableForge) {
        this.detectableForge = detectableForge;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(final String detectorType) {
        this.detectorType = detectorType;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(final String detectorName) {
        this.detectorName = detectorName;
    }

    public String getDetectorDescriptiveName() {
        return detectorDescriptiveName;
    }

    public void setDetectorDescriptiveName(final String detectorDescriptiveName) {
        this.detectorDescriptiveName = detectorDescriptiveName;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(final Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Boolean getNestable() {
        return nestable;
    }

    public void setNestable(final Boolean nestable) {
        this.nestable = nestable;
    }

    public Boolean getNestInvisible() {
        return nestInvisible;
    }

    public void setNestInvisible(final Boolean nestInvisible) {
        this.nestInvisible = nestInvisible;
    }

    public List<String> getYieldsTo() {
        return yieldsTo;
    }

    public void setYieldsTo(final List<String> yieldsTo) {
        this.yieldsTo = yieldsTo;
    }

    public String getFallbackTo() {
        return fallbackTo;
    }

    public void setFallbackTo(final String fallbackTo) {
        this.fallbackTo = fallbackTo;
    }
}
