/**
 * detector
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detector.base;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detector.exception.DetectableDetectorException;
import com.synopsys.integration.detector.exception.DetectorException;
import com.synopsys.integration.detector.result.DetectableDetectorResult;
import com.synopsys.integration.detector.result.DetectorResult;

public abstract class Detector {
    protected DetectorEnvironment environment;
    private final DetectorType detectorType;
    private Detectable detectable;

    public Detector(final DetectorEnvironment environment, final DetectorType detectorType, Detectable detectable) {
        this.environment = environment;
        this.detectorType = detectorType;
    }

    public DetectorResult applicable(){
        return new DetectableDetectorResult(detectable.applicable());
    }

    public DetectorResult extractable() throws DetectorException {
        try {
            return new DetectableDetectorResult(detectable.extractable());
        } catch (DetectableException e){
            throw new DetectableDetectorException(e);
        }

    }

    public Extraction extract(ExtractionEnvironment extractionEnvironment){
        return detectable.extract(extractionEnvironment);
    }

    public String getName() {
        return detectable.getName();
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    public String getDescriptiveName() {
        return String.format("%s - %s", getDetectorType().toString(), getName());
    }

    public boolean isSame(Detector detector) {
        return this.getClass().equals(detector.getClass());
    }

}
