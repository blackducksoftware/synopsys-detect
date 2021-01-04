/**
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
package com.synopsys.integration.detect.workflow.project;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.Stringable;

public class DetectToolProjectInfo extends Stringable {
    private final DetectTool detectTool;
    private final NameVersion suggestedNameVersion;

    public DetectToolProjectInfo(final DetectTool detectTool, final NameVersion suggestedNameVersion) {
        this.detectTool = detectTool;
        this.suggestedNameVersion = suggestedNameVersion;
    }

    public DetectTool getDetectTool() {
        return detectTool;
    }

    public NameVersion getSuggestedNameVersion() {
        return suggestedNameVersion;
    }
}
