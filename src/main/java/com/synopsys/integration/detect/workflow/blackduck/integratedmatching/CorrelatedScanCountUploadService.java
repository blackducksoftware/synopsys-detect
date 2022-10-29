package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.blackduck.api.core.BlackDuckPath;
import com.synopsys.integration.blackduck.api.core.BlackDuckResponse;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.DataService;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.body.BodyContentConverter;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.Stringable;

public class CorrelatedScanCountUploadService extends DataService {
    private final static String CORRELATED_SCAN_COUNT_ENDPOINT_FORMAT_STRING = "/api/scan-correlations/%s/counts";
    public static final String CONTENT_TYPE_HEADER_KEY = "Content-Type";
    public static final String CONTENT_TYPE_HEADER_VALUE = "application/vnd.blackducksoftware.scan-5+json";
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

    public Response uploadCorrelatedScanCounts(String correlationId, Map<DetectTool, Integer> countsByTool) throws IntegrationException {
        String urlString = String.format(CORRELATED_SCAN_COUNT_ENDPOINT_FORMAT_STRING, correlationId);
        BlackDuckPath<BlackDuckResponse> blackDuckPath = new BlackDuckPath<>(urlString, BlackDuckResponse.class, false);
        HttpUrl url = apiDiscovery.metaSingleResponse(blackDuckPath).getUrl();

        // TODO factor this conversion out so it's testable; does not belong here
        int packageManagerScanCount = countsByTool.getOrDefault(DetectTool.DETECTOR, 0)
            + countsByTool.getOrDefault(DetectTool.BAZEL, 0)
            + countsByTool.getOrDefault(DetectTool.DOCKER, 0);
        int signatureScanCount = countsByTool.getOrDefault(DetectTool.SIGNATURE_SCAN, 0);
        int binaryScanCount = countsByTool.getOrDefault(DetectTool.BINARY_SCAN, 0);
        ScanCounts scanCounts = new ScanCounts(packageManagerScanCount, signatureScanCount, binaryScanCount);
        logger.info(String.format("Uploading scanCounts: %s", scanCounts));
        /////////////////////

        ScanCountsPayload scanCountsPayload = new ScanCountsPayload(scanCounts);
        JsonElement scanCountsElement = gson.toJsonTree(scanCountsPayload);

        // TODO figure out what to do about that deprecated constant
        BlackDuckResponseRequest request = new BlackDuckRequestBuilder()
            .addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
            .postObject(scanCountsElement, BodyContentConverter.DEFAULT)
            .buildBlackDuckResponseRequest(url);

        Response response = blackDuckApiClient.execute(request);
        logger.debug(String.format("uploadCorrelatedScanCounts(): Black Duck response status: %d", response.getStatusCode()));
        return response;
    }

    // TODO these belong elsewhere: separate classes in a model sub package of this package
    private class ScanCounts extends Stringable {
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
