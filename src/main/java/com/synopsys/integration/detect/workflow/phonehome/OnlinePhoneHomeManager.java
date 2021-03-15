/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.phonehome;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.phonehome.PhoneHomeResponse;

public class OnlinePhoneHomeManager extends PhoneHomeManager {
    private final BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper;

    public OnlinePhoneHomeManager(final Map<String, String> additionalMetaData, final DetectInfo detectInfo, final EventSystem eventSystem, final BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper) {
        super(additionalMetaData, detectInfo, eventSystem);
        this.blackDuckPhoneHomeHelper = blackDuckPhoneHomeHelper;
    }

    @Override
    public PhoneHomeResponse phoneHome(final Map<String, String> metadata, final String... artifactModules) {
        final Map<String, String> metaDataToSend = new HashMap<>();
        metaDataToSend.putAll(metadata);
        metaDataToSend.putAll(additionalMetaData);
        return blackDuckPhoneHomeHelper.handlePhoneHome("synopsys-detect", detectInfo.getDetectVersion(), metaDataToSend, artifactModules);
    }

}
