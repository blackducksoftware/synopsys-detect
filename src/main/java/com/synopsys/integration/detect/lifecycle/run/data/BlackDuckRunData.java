package com.synopsys.integration.detect.lifecycle.run.data;

import java.util.Optional;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.version.BlackDuckVersion;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityResult;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionParser;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class BlackDuckRunData {
    private final PhoneHomeManager phoneHomeManager;
    private final BlackDuckServerConfig blackDuckServerConfig;
    private final BlackDuckServicesFactory blackDuckServicesFactory;
    private final BlackduckScanMode scanMode;
    private final boolean waitAtScanLevel;
    private final BlackDuckVersion blackDuckServerVersion;

    protected BlackDuckRunData(
        PhoneHomeManager phoneHomeManager,
        BlackDuckConnectivityResult blackDuckConnectivityResult,
        BlackDuckServicesFactory blackDuckServicesFactory,
        BlackduckScanMode scanMode,
        boolean waitAtScanLevel
    ) {
        this.phoneHomeManager = phoneHomeManager;
        this.blackDuckServerConfig = blackDuckConnectivityResult.getBlackDuckServerConfig();
        this.blackDuckServicesFactory = blackDuckServicesFactory;
        this.scanMode = scanMode;
        this.waitAtScanLevel = waitAtScanLevel;
        
        BlackDuckVersionParser parser = new BlackDuckVersionParser();
        Optional<BlackDuckVersion> blackDuckServerVersion = parser.parse(blackDuckConnectivityResult.getContactedServerVersion());
        this.blackDuckServerVersion = blackDuckServerVersion.isPresent() ? blackDuckServerVersion.get() : null;
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
        return new BlackDuckRunData(null, null, null, null, false);
    }

    public static BlackDuckRunData online(
        BlackduckScanMode scanMode,
        BlackDuckServicesFactory blackDuckServicesFactory,
        PhoneHomeManager phoneHomeManager,
        BlackDuckConnectivityResult blackDuckConnectivityResult,
        boolean waitAtScanLevel
    ) {
        return new BlackDuckRunData(phoneHomeManager, blackDuckConnectivityResult, blackDuckServicesFactory, scanMode, waitAtScanLevel);
    }

    public static BlackDuckRunData onlineNoPhoneHome(BlackduckScanMode scanMode, BlackDuckServicesFactory blackDuckServicesFactory, BlackDuckConnectivityResult blackDuckConnectivityResult, boolean waitAtScanLevel) {
        return new BlackDuckRunData(null, blackDuckConnectivityResult, blackDuckServicesFactory, scanMode, waitAtScanLevel);
    }

    public Boolean isNonPersistent() {
        return (scanMode == BlackduckScanMode.STATELESS || scanMode == BlackduckScanMode.RAPID);
    }

    public BlackduckScanMode getScanMode() {
        return scanMode;
    }
    
    public boolean shouldWaitAtScanLevel() {
        return waitAtScanLevel;
    }

    public BlackDuckVersion getBlackDuckServerVersion() {
        return blackDuckServerVersion;
    }
}
