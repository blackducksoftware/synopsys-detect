package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.phonehome.PhoneHomeCallable;
import com.synopsys.integration.phonehome.PhoneHomeClient;
import com.synopsys.integration.phonehome.PhoneHomeRequestBody;
import com.synopsys.integration.phonehome.enums.ProductIdEnum;
import com.synopsys.integration.util.IntEnvironmentVariables;

public class OfflineBlackDuckPhoneHomeCallable extends PhoneHomeCallable {

    private final Map<String, String> metadata = new HashMap<>();

    private final String artifactVersion;
    private final String artifactId;

    public OfflineBlackDuckPhoneHomeCallable(final IntLogger logger, final PhoneHomeClient client, final String artifactId, final String artifactVersion, final IntEnvironmentVariables intEnvironmentVariables) {
        super(logger, client, null, artifactId, artifactVersion, intEnvironmentVariables);
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
    }

    @Override
    public PhoneHomeRequestBody createPhoneHomeRequestBody() {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = createPhoneHomeRequestBodyBuilder();
        phoneHomeRequestBodyBuilder.setArtifactId(artifactId);
        phoneHomeRequestBodyBuilder.setArtifactVersion(artifactVersion);
        return phoneHomeRequestBodyBuilder.build();
    }

    @Override
    public PhoneHomeRequestBody.Builder createPhoneHomeRequestBodyBuilder() {
        final PhoneHomeRequestBody.Builder phoneHomeRequestBodyBuilder = new PhoneHomeRequestBody.Builder();
        phoneHomeRequestBodyBuilder.setCustomerId(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        phoneHomeRequestBodyBuilder.setProductId(ProductIdEnum.HUB);
        phoneHomeRequestBodyBuilder.setProductVersion(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        phoneHomeRequestBodyBuilder.setHostName(PhoneHomeRequestBody.Builder.UNKNOWN_ID);
        metadata.entrySet().forEach(it -> phoneHomeRequestBodyBuilder.addToMetaData(it.getKey(), it.getValue()));
        return phoneHomeRequestBodyBuilder;
    }

    public void addMetaData(final String key, final String value) {
        metadata.put(key, value);
    }
}
