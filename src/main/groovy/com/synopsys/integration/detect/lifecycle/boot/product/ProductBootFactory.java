/**
 * synopsys-detect
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
package com.synopsys.integration.detect.lifecycle.boot.product;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.help.DetectOptionManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.phonehome.OnlinePhoneHomeManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class ProductBootFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectConfiguration detectConfiguration;
    private final DetectInfo detectInfo;
    private final EventSystem eventSystem;
    private final DetectOptionManager detectOptionManager;

    public ProductBootFactory(DetectConfiguration detectConfiguration, DetectInfo detectInfo, EventSystem eventSystem, final DetectOptionManager detectOptionManager) {
        this.detectConfiguration = detectConfiguration;
        this.detectInfo = detectInfo;
        this.eventSystem = eventSystem;
        this.detectOptionManager = detectOptionManager;
    }

    public PhoneHomeManager createPhoneHomeManager(BlackDuckServicesFactory blackDuckServicesFactory) {
        Map<String, String> additionalMetaData = detectConfiguration.getPhoneHomeProperties();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper = BlackDuckPhoneHomeHelper.createAsynchronousPhoneHomeHelper(blackDuckServicesFactory, executorService);
        PhoneHomeManager phoneHomeManager = new OnlinePhoneHomeManager(additionalMetaData, detectInfo, eventSystem, blackDuckPhoneHomeHelper);
        return phoneHomeManager;
    }

    public BlackDuckServerConfig createBlackDuckServerConfig() throws DetectUserFriendlyException {
        return detectOptionManager.createBlackduckServerConfig();
    }
}
