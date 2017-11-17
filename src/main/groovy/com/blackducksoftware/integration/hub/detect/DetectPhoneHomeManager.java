package com.blackducksoftware.integration.hub.detect;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeResponse;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;

@Component
public class DetectPhoneHomeManager {
    private PhoneHomeResponse phoneHomeResponse;

    public void startPhoneHome(final PhoneHomeDataService phoneHomeDataService, final PhoneHomeRequestBody phoneHomeRequestBody) {
        phoneHomeResponse = phoneHomeDataService.startPhoneHome(phoneHomeRequestBody);
    }

    public void endPhoneHome() {
        if (phoneHomeResponse != null) {
            phoneHomeResponse.endPhoneHome();
        }
    }

    public PhoneHomeResponse getPhoneHomeResponse() {
        return phoneHomeResponse;
    }

}
