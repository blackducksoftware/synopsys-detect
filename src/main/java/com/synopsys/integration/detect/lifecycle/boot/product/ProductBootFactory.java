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
package com.synopsys.integration.detect.lifecycle.boot.product;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConfigFactory;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.phonehome.OnlinePhoneHomeManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;
import com.synopsys.integration.log.SilentIntLogger;

public class ProductBootFactory {
    private final DetectInfo detectInfo;
    private final EventSystem eventSystem;
    private final DetectConfigurationFactory detectConfigurationFactory;

    public ProductBootFactory(DetectInfo detectInfo, EventSystem eventSystem, DetectConfigurationFactory detectConfigurationFactory) {
        this.detectInfo = detectInfo;
        this.eventSystem = eventSystem;
        this.detectConfigurationFactory = detectConfigurationFactory;
    }

    public PhoneHomeManager createPhoneHomeManager(BlackDuckServicesFactory blackDuckServicesFactory) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper = BlackDuckPhoneHomeHelper.createAsynchronousPhoneHomeHelper(blackDuckServicesFactory, executorService);
        PhoneHomeManager phoneHomeManager = new OnlinePhoneHomeManager(detectConfigurationFactory.createPhoneHomeOptions().getPassthrough(), detectInfo, eventSystem, blackDuckPhoneHomeHelper);
        return phoneHomeManager;
    }

    public BlackDuckServerConfig createBlackDuckServerConfig() throws DetectUserFriendlyException {
        BlackDuckConnectionDetails connectionDetails = detectConfigurationFactory.createBlackDuckConnectionDetails();
        BlackDuckConfigFactory blackDuckConfigFactory = new BlackDuckConfigFactory(detectInfo, connectionDetails);
        return blackDuckConfigFactory.createServerConfig(new SilentIntLogger());
    }
}
