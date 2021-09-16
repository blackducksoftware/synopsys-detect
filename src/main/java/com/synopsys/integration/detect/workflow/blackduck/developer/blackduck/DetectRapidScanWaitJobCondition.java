/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckResponseRequest;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.exception.IntegrationRestException;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.wait.WaitJobCondition;

public class DetectRapidScanWaitJobCondition implements WaitJobCondition {
    private final BlackDuckApiClient blackDuckApiClient;
    private final List<HttpUrl> remainingUrls;

    public DetectRapidScanWaitJobCondition(BlackDuckApiClient blackDuckApiClient, List<HttpUrl> resultUrl) {
        this.blackDuckApiClient = blackDuckApiClient;
        this.remainingUrls = new ArrayList<>();
        remainingUrls.addAll(resultUrl);
    }

    @Override
    public boolean isComplete() throws IntegrationException {
        if (remainingUrls.isEmpty())
            return true;

        List<HttpUrl> completed = new ArrayList<>();
        for (HttpUrl url : remainingUrls) {
            if (isComplete(url)) {
                completed.add(url);
            }
        }

        remainingUrls.removeAll(completed);
        return remainingUrls.isEmpty();
    }

    private boolean isComplete(HttpUrl url) throws IntegrationException {
        BlackDuckResponseRequest request = new DetectRapidScanRequestBuilder()
                                               .createResponseRequest(url);
        try (Response response = blackDuckApiClient.execute(request)) {
            return response.isStatusCodeSuccess();
        } catch (IntegrationRestException ex) {
            if (HttpStatus.SC_NOT_FOUND == ex.getHttpStatusCode()) {
                return false;
            } else {
                throw ex;
            }
        } catch (IOException ex) {
            throw new BlackDuckIntegrationException(ex.getMessage(), ex);
        }
    }

}