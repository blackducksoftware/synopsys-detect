package com.synopsys.integration.detect.workflow.phonehome;

import com.google.gson.annotations.SerializedName;

public class PhoneHomeSecrets {
    @SerializedName("api_secret")
    private String apiSecret;
    @SerializedName("measurement_id")
    private String measurementId;

    public PhoneHomeSecrets(String apiSecret, String measurementId) {
        this.apiSecret = apiSecret;
        this.measurementId = measurementId;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getMeasurementId() {
        return measurementId;
    }
}
