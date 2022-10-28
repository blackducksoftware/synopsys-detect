package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonWriter;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.api.core.BlackDuckResponse;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.enumeration.ReportFormatType;
import com.synopsys.integration.blackduck.api.generated.enumeration.ReportType;
import com.synopsys.integration.blackduck.api.generated.view.DeveloperScansScanView;
import com.synopsys.integration.blackduck.api.generated.view.ProjectVersionView;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.DataService;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.detect.workflow.blackduck.report.service.ReportService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.BodyContentConverter;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.IntegrationEscapeUtil;

public class CorrelatedScanCountUploadService extends DataService {
    private final static String CORRELATED_SCAN_COUNT_ENDPOINT_FORMAT_STRING = "/api/scan-correlations/%s/counts";
    private final HttpUrl blackDuckBaseUrl;
    private final Gson gson;

    public CorrelatedScanCountUploadService(
        Gson gson,
        HttpUrl blackDuckBaseUrl,
        BlackDuckApiClient blackDuckApiClient,
        ApiDiscovery apiDiscovery,
        IntLogger logger
    ) {
        super(blackDuckApiClient, apiDiscovery, logger);
        this.gson = gson;
        this.blackDuckBaseUrl = blackDuckBaseUrl;
    }

    public Response uploadCorrelatedScanCounts(String correlationId) throws IntegrationException {
        String urlString = String.format(CORRELATED_SCAN_COUNT_ENDPOINT_FORMAT_STRING, correlationId);
        BlackDuckPath<BlackDuckResponse> blackDuckPath = new BlackDuckPath<>(urlString, BlackDuckResponse.class, false);
        HttpUrl url = apiDiscovery.metaSingleResponse(blackDuckPath).getUrl();

        ScanCountsPayload scanCountsPayload = new ScanCountsPayload(new ScanCounts(1, 2, 3));
        JsonElement scanCountsElement = gson.toJsonTree(scanCountsPayload);

        // 'Content-Type': "application/vnd.blackducksoftware.scan-5+json"
        BlackDuckResponseRequest request = new BlackDuckRequestBuilder()
            .addHeader("Content-Type", "application/vnd.blackducksoftware.scan-5+json")
            .postObject(scanCountsElement, BodyContentConverter.DEFAULT)
            .buildBlackDuckResponseRequest(url);

        Response response = blackDuckApiClient.execute(request);
        logger.debug(String.format("uploadCorrelatedScanCounts(): Black Duck response status: %d", response.getStatusCode()));
        return response;
    }

    private class ScanCounts {
        @SerializedName("PACKAGE_MANAGER")
        private int packageManager;

        @SerializedName("SIGNATURE")
        private int signature;

        @SerializedName("BINARY")
        private int binary;

        public ScanCounts(final int packageManager, final int signature, final int binary) {
            this.packageManager = packageManager;
            this.signature = signature;
            this.binary = binary;
        }

        public int getPackageManager() {
            return packageManager;
        }

        public int getSignature() {
            return signature;
        }

        public int getBinary() {
            return binary;
        }
    }

    private class ScanCountsPayload {
        private ScanCounts scanCounts;

        public ScanCountsPayload(final ScanCounts scanCounts) {
            this.scanCounts = scanCounts;
        }

        public ScanCounts getScanCounts() {
            return scanCounts;
        }
    }
}
