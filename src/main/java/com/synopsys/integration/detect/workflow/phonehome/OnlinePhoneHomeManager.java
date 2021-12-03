package com.synopsys.integration.detect.workflow.phonehome;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.phonehome.PhoneHomeResponse;

public class OnlinePhoneHomeManager extends PhoneHomeManager {
    private final BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper;

    public OnlinePhoneHomeManager(Map<String, String> additionalMetaData, DetectInfo detectInfo, EventSystem eventSystem, BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper) {
        super(additionalMetaData, detectInfo, eventSystem);
        this.blackDuckPhoneHomeHelper = blackDuckPhoneHomeHelper;
    }

    @Override
    public PhoneHomeResponse phoneHome(Map<String, String> metadata, String... artifactModules) {
        Map<String, String> metaDataToSend = new HashMap<>();
        metaDataToSend.putAll(metadata);
        metaDataToSend.putAll(additionalMetaData);
        return blackDuckPhoneHomeHelper.handlePhoneHome("synopsys-detect", detectInfo.getDetectVersion(), metaDataToSend, artifactModules);
    }

}
