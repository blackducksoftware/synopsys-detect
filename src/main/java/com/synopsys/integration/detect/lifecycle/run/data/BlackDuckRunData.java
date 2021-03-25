/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.Optional;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class BlackDuckRunData {
    private final PhoneHomeManager phoneHomeManager;
    private final BlackDuckServerConfig blackDuckServerConfig;
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final Boolean isRapid;

    protected BlackDuckRunData(PhoneHomeManager phoneHomeManager, BlackDuckServerConfig blackDuckServerConfig, BlackDuckServicesFactory blackDuckServicesFactory, final Boolean isRapid) {
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.isRapid = isRapid;
    }

    public boolean isOnline() {
        return blackDuckServerConfig != null && blackDuckServicesFactory != null;
    }

    public Optional<PhoneHomeManager> getPhoneHomeManager() {
        return Optional.ofNullable(phoneHomeManager);
    }

    public BlackDuckServerConfig getBlackDuckServerConfig() {
        return blackDuckServerConfig;
    }

    public BlackDuckServicesFactory getBlackDuckServicesFactory() {
        return blackDuckServicesFactory;
    }

    public static BlackDuckRunData offline() {
        return new BlackDuckRunData(null, null, null, false);
    }

    public static BlackDuckRunData online(boolean isRapid, BlackDuckServicesFactory blackDuckServicesFactory, PhoneHomeManager phoneHomeManager, BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckRunData(phoneHomeManager, blackDuckServerConfig, blackDuckServicesFactory, isRapid);
    }

    public static BlackDuckRunData onlineNoPhoneHome(boolean isRapid, BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckRunData(null, blackDuckServerConfig, blackDuckServicesFactory, isRapid);
    }

    public Boolean isRapid() {
        return isRapid;
    }
}
