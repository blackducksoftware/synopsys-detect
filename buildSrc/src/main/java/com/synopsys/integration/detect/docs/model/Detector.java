/**
 * buildSrc
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
package com.synopsys.integration.detect.docs.model;

import com.synopsys.integration.detect.docs.copied.HelpJsonDetector;

public class Detector {
    private final String detectorType;
    private final String detectorName;
    private final String detectableLanguage;
    private final String detectableForge;
    private final String detectableRequirementsMarkdown;

    public Detector(final HelpJsonDetector detector) {
        this(detector.getDetectorType(), detector.getDetectorName(), detector.getDetectableLanguage(), detector.getDetectableForge(), detector.getDetectableRequirementsMarkdown());
    }

    public Detector(final String detectorType, final String detectorName, final String detectableLanguage, final String detectableForge, final String detectableRequirementsMarkdown) {
        this.detectorType = detectorType;
        this.detectorName = detectorName;
        this.detectableLanguage = detectableLanguage;
        this.detectableForge = detectableForge;
        this.detectableRequirementsMarkdown = detectableRequirementsMarkdown;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public String getDetectableLanguage() {
        return detectableLanguage;
    }

    public String getDetectableForge() {
        return detectableForge;
    }

    public String getDetectableRequirementsMarkdown() {
        return detectableRequirementsMarkdown;
    }
}
