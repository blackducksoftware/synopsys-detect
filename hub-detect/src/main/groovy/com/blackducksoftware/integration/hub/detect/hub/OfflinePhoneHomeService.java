/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.hub;

import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.enums.ProductIdEnum;
import com.blackducksoftware.integration.util.IntEnvironmentVariables;

public class OfflinePhoneHomeService extends PhoneHomeService {

    public OfflinePhoneHomeService(IntLogger logger, PhoneHomeClient phoneHomeClient,
            IntEnvironmentVariables intEnvironmentVariables) {
        super(null, logger, phoneHomeClient, null, intEnvironmentVariables);
    }

    @Override
    public PhoneHomeRequestBody.Builder createInitialPhoneHomeRequestBodyBuilder() {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBodyBuilder.setCustomerId(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        phoneHomeRequestBodyBuilder.setHostName(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        phoneHomeRequestBodyBuilder.setProductId(ProductIdEnum.HUB);
        phoneHomeRequestBodyBuilder.setProductVersion(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        return phoneHomeRequestBodyBuilder;
    }

}
