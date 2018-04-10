package com.blackducksoftware.integration.hub.detect.hub;

import com.blackducksoftware.integration.hub.service.PhoneHomeService;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBodyBuilder;
import com.blackducksoftware.integration.phonehome.enums.BlackDuckName;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;

public class OfflinePhoneHomeService extends PhoneHomeService {

    public OfflinePhoneHomeService(PhoneHomeClient phoneHomeClient,
            CIEnvironmentVariables ciEnvironmentVariables) {
        super(null, phoneHomeClient, null, ciEnvironmentVariables);
    }

    @Override
    public PhoneHomeRequestBodyBuilder createInitialPhoneHomeRequestBodyBuilder() {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = new PhoneHomeRequestBodyBuilder();
        phoneHomeRequestBodyBuilder.setRegistrationId("OFFLINE");
        phoneHomeRequestBodyBuilder.setHostName("OFFLINE");
        phoneHomeRequestBodyBuilder.setBlackDuckName(BlackDuckName.HUB);
        phoneHomeRequestBodyBuilder.setBlackDuckVersion("N/A");
        phoneHomeRequestBodyBuilder.setSource(PhoneHomeSource.INTEGRATIONS);
        return phoneHomeRequestBodyBuilder;
    }

}
