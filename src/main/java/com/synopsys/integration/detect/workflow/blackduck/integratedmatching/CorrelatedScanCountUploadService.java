package com.synopsys.integration.detect.workflow.blackduck.integratedmatching;

import java.util.Map;

import org.apache.http.entity.ContentType;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
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

public class CorrelatedScanCountUploadService extends DataService {
    // TODO: This feature is being developed before the Black Duck endpoint is released, so this code
    // can't adhere to the standard pattern yet.
    // Once this endpoint/payload is added to the Black Duck swagger doc, and blackduck-common-api is
    // regenerated from it, this service can move to blackduck-common, and these details can be sourced
    // from blackduck-common-api, as with other services/endpoints.
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
        HttpUrl url = buildEndpointUrl(correlationId);

        ScanCountsPayload scanCountsPayload = ScanCountsPayload.createFromCountsByTool(countsByTool);
        logger.info(String.format("Uploading scanCountsPayload: %s", scanCountsPayload));
        JsonElement scanCountsElement = gson.toJsonTree(scanCountsPayload);

        BlackDuckResponseRequest request = new BlackDuckRequestBuilder()
            .addHeader(CONTENT_TYPE_HEADER_KEY, CONTENT_TYPE_HEADER_VALUE)
            .postObject(scanCountsElement, ContentType.APPLICATION_JSON)
            .buildBlackDuckResponseRequest(url);

        Response response = blackDuckApiClient.execute(request);
        logger.debug(String.format("uploadCorrelatedScanCounts(): Black Duck response status: %d", response.getStatusCode()));
        return response;
    }

    private HttpUrl buildEndpointUrl(final String correlationId) {
        String urlString = String.format(CORRELATED_SCAN_COUNT_ENDPOINT_FORMAT_STRING, correlationId);
        BlackDuckPath<BlackDuckResponse> blackDuckPath = new BlackDuckPath<>(urlString, BlackDuckResponse.class, false);
        return apiDiscovery.metaSingleResponse(blackDuckPath).getUrl();
    }
}
