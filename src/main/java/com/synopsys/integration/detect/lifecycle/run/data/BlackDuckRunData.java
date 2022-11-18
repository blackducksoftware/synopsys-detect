package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.Optional;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersion;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class BlackDuckRunData {
    private final PhoneHomeManager phoneHomeManager;
    private final BlackDuckServerConfig blackDuckServerConfig;
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final BlackduckScanMode scanMode;
    private final Optional<BlackDuckVersion> blackDuckServerVersion;

    protected BlackDuckRunData(
        PhoneHomeManager phoneHomeManager,
        BlackDuckServerConfig blackDuckServerConfig,
        BlackDuckServicesFactory blackDuckServicesFactory,
        BlackduckScanMode scanMode,
        Optional<BlackDuckVersion> blackDuckServerVersion
    ) {
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckServerConfig;
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.scanMode = scanMode;
        this.blackDuckServerVersion = blackDuckServerVersion;
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
        return new BlackDuckRunData(null, null, null, null, null);
    }

    public static BlackDuckRunData online(
        BlackduckScanMode scanMode,
        BlackDuckServicesFactory blackDuckServicesFactory,
        PhoneHomeManager phoneHomeManager,
        BlackDuckServerConfig blackDuckServerConfig,
        Optional<BlackDuckVersion> blackDuckServerVersion
    ) {
        return new BlackDuckRunData(phoneHomeManager, blackDuckServerConfig, blackDuckServicesFactory, scanMode, blackDuckServerVersion);
    }

    public static BlackDuckRunData onlineNoPhoneHome(BlackduckScanMode scanMode, BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckServerConfig blackDuckServerConfig, Optional<BlackDuckVersion> blackDuckServerVersion) {
        return new BlackDuckRunData(null, blackDuckServerConfig, blackDuckServicesFactory, scanMode, blackDuckServerVersion);
    }

    public Boolean isNonPersistent() {
        return (scanMode == BlackduckScanMode.EPHEMERAL || scanMode == BlackduckScanMode.RAPID);
    }

    public BlackduckScanMode getScanMode() {
        return scanMode;
    }
    
    public Optional<BlackDuckVersion> getBlackDuckServerVersion() {
        return blackDuckServerVersion;
    }
}
