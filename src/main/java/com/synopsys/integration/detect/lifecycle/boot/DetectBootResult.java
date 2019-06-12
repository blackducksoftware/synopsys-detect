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
package com.synopsys.integration.detect.lifecycle.boot;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;

public class DetectBootResult {
    public BootType bootType;
    public DetectConfiguration detectConfiguration; //Application needs this to make sure exit code behaves.
    public ProductRunData productRunData;

    public enum BootType {
        EXIT,
        RUN
    }

    public static DetectBootResult run(DetectConfiguration detectConfiguration, ProductRunData productRunData) {
        DetectBootResult result = new DetectBootResult();
        result.bootType = BootType.RUN;
        result.detectConfiguration = detectConfiguration;
        result.productRunData = productRunData;
        return result;
    }

    public static DetectBootResult exit(DetectConfiguration detectConfiguration) {
        DetectBootResult result = new DetectBootResult();
        result.bootType = BootType.EXIT;
        result.detectConfiguration = detectConfiguration;
        return result;
    }
}
