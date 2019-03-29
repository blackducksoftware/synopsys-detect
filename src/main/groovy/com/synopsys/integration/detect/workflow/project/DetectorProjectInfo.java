/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.workflow.project;

import com.synopsys.integration.detector.base.DetectorType;
import com.synopsys.integration.util.NameVersion;

public class DetectorProjectInfo {
    private final DetectorType detectorType;
    private final int depth;
    private final NameVersion nameVersion;

    public DetectorProjectInfo(final DetectorType detectorType, final int depth, final NameVersion nameVersion) {
        this.detectorType = detectorType;
        this.nameVersion = nameVersion;
        this.depth = depth;
    }

    public DetectorType getDetectorType() {
        return detectorType;
    }

    public int getDepth() {
        return depth;
    }

    public NameVersion getNameVersion() {
        return nameVersion;
    }

}
