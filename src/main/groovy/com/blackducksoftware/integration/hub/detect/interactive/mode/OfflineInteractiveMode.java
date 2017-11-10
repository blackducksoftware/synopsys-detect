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
package com.blackducksoftware.integration.hub.detect.interactive.mode;

import org.springframework.stereotype.Component;

@Component
public class OfflineInteractiveMode extends InteractiveMode {
    @Override
    public void interact() {
        printWelcome();

        final Boolean scan = askYesOrNo("Would you like run a CLI scan?");
        if (!scan) {
            setProperty("hubSignatureScannerDisabled", "true");
        }

        if (scan) {
            final Boolean customScanner = askYesOrNo("Would you like to provide a custom scanner?");
            if (customScanner) {
                final Boolean downloadCustomScanner = askYesOrNo("Would you like to download the custom scanner?");
                if (downloadCustomScanner) {
                    setPropertyFromQuestion("hubSignatureScannerHostUrl", "What is the scanner host url?");
                } else {
                    setPropertyFromQuestion("hubSignatureScannerOfflineLocalPath", "What is the location of your offline scanner?");
                }
            }
        }

        performStandardOutflow();
    }

}
