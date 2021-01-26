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
package com.synopsys.integration.detect.interactive;

import static com.synopsys.integration.detect.configuration.DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN;
import static com.synopsys.integration.detect.configuration.DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL;
import static com.synopsys.integration.detect.configuration.DetectProperties.DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH;

public class SignatureScannerDecisionBranch implements DecisionTree {
    public static final String SHOULD_UPLOAD_TO_BLACK_DUCK = "Would you like to upload signature scan results to the Black Duck server?";
    public static final String SHOULD_USE_CUSTOM_SCANNER = "Would you like to provide a custom signature scanner?";
    public static final String SHOULD_DOWNLOAD_CUSTOM_SCANNER = "Would you like to download the custom signature scanner?";
    public static final String SET_SCANNER_HOST_URL = "What is the signature scanner host url?";
    public static final String SET_SCANNER_OFFLINE_LOCAL_PATH = "What is the location of your offline signature scanner?";
    private final boolean connectedToBlackDuck;

    public SignatureScannerDecisionBranch(boolean connectedToBlackDuck) {
        this.connectedToBlackDuck = connectedToBlackDuck;
    }

    @Override
    public void traverse(InteractivePropertySourceBuilder propertySourceBuilder, InteractiveWriter writer) {
        if (connectedToBlackDuck) {
            Boolean upload = writer.askYesOrNo(SHOULD_UPLOAD_TO_BLACK_DUCK);
            if (!upload) {
                propertySourceBuilder.setProperty(DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, Boolean.TRUE.toString());
            }
        }

        Boolean customScanner = writer.askYesOrNo(SHOULD_USE_CUSTOM_SCANNER);
        if (customScanner) {
            Boolean downloadCustomScanner = writer.askYesOrNo(SHOULD_DOWNLOAD_CUSTOM_SCANNER);
            if (downloadCustomScanner) {
                propertySourceBuilder.setPropertyFromQuestion(DETECT_BLACKDUCK_SIGNATURE_SCANNER_HOST_URL, SET_SCANNER_HOST_URL);
            } else {
                propertySourceBuilder.setPropertyFromQuestion(DETECT_BLACKDUCK_SIGNATURE_SCANNER_OFFLINE_LOCAL_PATH, SET_SCANNER_OFFLINE_LOCAL_PATH);
            }
        }
    }
}
