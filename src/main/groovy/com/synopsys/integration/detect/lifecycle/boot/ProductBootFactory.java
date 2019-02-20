package com.synopsys.integration.detect.lifecycle.boot;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.phonehome.BlackDuckPhoneHomeHelper;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.BootDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.PolarisDecision;
import com.synopsys.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.synopsys.integration.detect.lifecycle.run.data.PolarisRunData;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.workflow.BlackDuckConnectivityManager;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.phonehome.OnlinePhoneHomeManager;
import com.synopsys.integration.detect.workflow.phonehome.PhoneHomeManager;

public class ProductBootFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectConfiguration detectConfiguration;
    private final DetectInfo detectInfo;
    private final EventSystem eventSystem;

    public ProductBootFactory(DetectConfiguration detectConfiguration, DetectInfo detectInfo, EventSystem eventSystem) {
        this.detectConfiguration = detectConfiguration;
        this.detectInfo = detectInfo;
        this.eventSystem = eventSystem;
    }

    public PhoneHomeManager createPhoneHomeManager(BlackDuckServicesFactory blackDuckServicesFactory) {
        Map<String, String> additionalMetaData = detectConfiguration.getPhoneHomeProperties();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        BlackDuckPhoneHomeHelper blackDuckPhoneHomeHelper = BlackDuckPhoneHomeHelper.createAsynchronousPhoneHomeHelper(blackDuckServicesFactory, executorService);
        PhoneHomeManager phoneHomeManager = new OnlinePhoneHomeManager(additionalMetaData, detectInfo, eventSystem, blackDuckPhoneHomeHelper);
        return phoneHomeManager;
    }
}
