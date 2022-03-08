package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.Optional;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class BlackDuckRunData {
    private final PhoneHomeManager phoneHomeManager;
    private final BlackDuckServerConfig blackDuckServerConfig;
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final BlackduckScanMode scanMode;

    protected BlackDuckRunData(
        PhoneHomeManager phoneHomeManager,
        BlackDuckServerConfig blackDuckServerConfig,
        BlackDuckServicesFactory blackDuckServicesFactory,
        BlackduckScanMode scanMode
    ) {
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.scanMode = scanMode;
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
        return new BlackDuckRunData(null, null, null, null);
    }

    public static BlackDuckRunData online(
        BlackduckScanMode scanMode,
        BlackDuckServicesFactory blackDuckServicesFactory,
        PhoneHomeManager phoneHomeManager,
        BlackDuckServerConfig blackDuckServerConfig
    ) {
        return new BlackDuckRunData(phoneHomeManager, blackDuckServerConfig, blackDuckServicesFactory, scanMode);
    }

    public static BlackDuckRunData onlineNoPhoneHome(BlackduckScanMode scanMode, BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckServerConfig blackDuckServerConfig) {
        return new BlackDuckRunData(null, blackDuckServerConfig, blackDuckServicesFactory, scanMode);
    }

    public Boolean isRapid() {
        return scanMode == BlackduckScanMode.RAPID;
    }

    public BlackduckScanMode getScanMode() {
        return scanMode;
    }
}
