package com.blackducksoftware.integration.hub.detect.hub

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.HubSupportHelper
import com.blackducksoftware.integration.hub.builder.HubScanConfigBuilder
import com.blackducksoftware.integration.hub.cli.SimpleScanService
import com.blackducksoftware.integration.hub.global.HubCredentials
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.scan.HubScanConfig
import com.blackducksoftware.integration.log.Slf4jIntLogger
import com.blackducksoftware.integration.util.CIEnvironmentVariables
import com.google.gson.Gson

@Component
class OfflineScanner {
    private static final Logger logger = LoggerFactory.getLogger(OfflineScanner.class)

    @Autowired
    Gson gson

    void offlineScan(HubScanConfig hubScanConfig) {
        def intLogger = new Slf4jIntLogger(logger)

        def offlineCredentials = createOfflineCredentials()

        def hubServerConfig = new HubServerConfig(new URL('http://www.blackducksoftware.com'), 0, offlineCredentials, null, true)

        def hubSupportHelper = new HubSupportHelper()
        hubSupportHelper.setHub3_7Support()
        hubSupportHelper.setHasBeenChecked(true)

        def ciEnvironmentVariables = new CIEnvironmentVariables()
        ciEnvironmentVariables.putAll(System.getenv())

        def hubScanConfigBuilder = createHubScanConfigBuilder()
        hubScanConfig.scanTargetPaths.each { hubScanConfigBuilder.addScanTargetPath(it) }

        HubScanConfig hubScanConfigToUse = hubScanConfigBuilder.build()

        def simpleScanService = new SimpleScanService(intLogger, gson, hubServerConfig, hubSupportHelper, ciEnvironmentVariables, hubScanConfigToUse, null, null)
        simpleScanService.setupAndExecuteScan()
    }

    public HubCredentials createOfflineCredentials() {
        def credentials = new HubCredentials('', 'notblank')
        credentials
    }

    private HubScanConfigBuilder createHubScanConfigBuilder() {
        HubScanConfigBuilder hubScanConfigBuilder = new HubScanConfigBuilder()
        hubScanConfigBuilder.scanMemory = '4096'
        //        hubScanConfigBuilder.toolsDir = new File('/Users/ekerwin/Documents/scanners/offline')
        hubScanConfigBuilder.toolsDir = new File('/Users/ekerwin/blackduck/signature_scanner/tools')
        hubScanConfigBuilder.workingDirectory = new File('/Users/ekerwin/Documents/working/offline')
        hubScanConfigBuilder.cleanupLogsOnSuccess = false
        hubScanConfigBuilder.dryRun = true

        hubScanConfigBuilder
    }
}
