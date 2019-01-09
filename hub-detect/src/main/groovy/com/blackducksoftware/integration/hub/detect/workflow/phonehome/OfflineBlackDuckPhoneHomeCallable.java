/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.workflow.phonehome;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.phonehome.PhoneHomeCallable;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;
import com.synopsys.integration.phonehome.enums.ProductIdEnum;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class OfflineBlackDuckPhoneHomeCallable extends PhoneHomeCallable {

    private final Map<String, String> metadata = new HashMap<>();

    private final String artifactVersion;
    private final String artifactId;

    public OfflineBlackDuckPhoneHomeCallable(final IntLogger logger, final PhoneHomeClient client, final String artifactId, final String artifactVersion, final IntEnvironmentVariables intEnvironmentVariables) {
        super(logger, client, null, artifactId, artifactVersion, intEnvironmentVariables);
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
    }

    @Override
    public PhoneHomeRequestBody createPhoneHomeRequestBody() {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = createPhoneHomeRequestBodyBuilder();
        phoneHomeRequestBodyBuilder.setArtifactId(artifactId);
        phoneHomeRequestBodyBuilder.setArtifactVersion(artifactVersion);
        return phoneHomeRequestBodyBuilder.build();
    }

    @Override
    public PhoneHomeRequestBody.Builder createPhoneHomeRequestBodyBuilder() {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBodyBuilder.setCustomerId(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        phoneHomeRequestBodyBuilder.setProductId(ProductIdEnum.HUB);
        phoneHomeRequestBodyBuilder.setProductVersion(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        phoneHomeRequestBodyBuilder.setHostName(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        metadata.entrySet().forEach(it -> phoneHomeRequestBodyBuilder.addToMetaData(it.getKey(), it.getValue()));
        return phoneHomeRequestBodyBuilder;
    }

    public void addMetaData(final String key, final String value) {
        metadata.put(key, value);
    }
}
