/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.polaris.common.api.PolarisComponent;
import com.synopsys.integration.polaris.common.api.PolarisResource;
import com.synopsys.integration.polaris.common.api.PolarisResourceSparse;
import com.synopsys.integration.polaris.common.api.PolarisResources;
import com.synopsys.integration.polaris.common.api.PolarisResourcesPagination;
import com.synopsys.integration.polaris.common.api.PolarisResponse;
import com.synopsys.integration.polaris.common.request.PolarisPagedRequestWrapper;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class PolarisService {
    public static final String PROJECT_ID = "project-id";
    public static final String BRANCH_ID = "branch-id";

    public static final String COMMON_API_SPEC = "/api/common/v0";
    public static final String PROJECT_API_SPEC = COMMON_API_SPEC + "/projects";
    public static final String BRANCHES_API_SPEC = COMMON_API_SPEC + "/branches";

    public static final String QUERY_API_SPEC = "/api/query/v0";
    public static final String ISSUES_API_SPEC = QUERY_API_SPEC + "/issues";
    private final AccessTokenPolarisHttpClient polarisHttpClient;
    private final PolarisJsonTransformer polarisJsonTransformer;
    private final int defaultPageSize;

    public PolarisService(final AccessTokenPolarisHttpClient polarisHttpClient, final PolarisJsonTransformer polarisJsonTransformer, final int defaultPageSize) {
        this.polarisHttpClient = polarisHttpClient;
        this.polarisJsonTransformer = polarisJsonTransformer;
        this.defaultPageSize = defaultPageSize;
    }

    public static final String GET_ISSUE_API_SPEC(final String issueKey) {
        return ISSUES_API_SPEC + "/" + issueKey;
    }

    public <R extends PolarisResource> Optional<R> getResourceFromPopulated(final PolarisResponse populatedResources, final PolarisResourceSparse sparseResourceData, final Class<R> resourceClass) {
        final String id = StringUtils.defaultString(sparseResourceData.getId());
        final String type = StringUtils.defaultString(sparseResourceData.getType());
        for (final PolarisResource includedResource : populatedResources.getIncluded()) {
            if (type.equals(includedResource.getType()) && id.equals(includedResource.getId())) {
                try {
                    final R fullyTypedResource = polarisJsonTransformer.getResponseAs(includedResource.getJson(), resourceClass);
                    return Optional.of(fullyTypedResource);
                } catch (final IntegrationException e) {
                    break;
                }
            }
        }
        return Optional.empty();
    }

    public <R extends PolarisComponent> R get(final Type returnType, final Request request) throws IntegrationException {
        try (final Response response = polarisHttpClient.execute(request)) {
            response.throwExceptionForError();

            return polarisJsonTransformer.getResponse(response, returnType);
        } catch (final IOException e) {
            throw new IntegrationException(e);
        }
    }

    /* TODO: Refactor this implementation. The following should compile, but doesn't. --rotte APR 2020
public <R extends PolarisResource> Optional<R> getFirstResponse2(final Request request, final Type resourcesType) throws IntegrationException {
    return getAllResponses(request, resourcesType)
               .stream()
               .findFirst();
}
 */
    public <R extends PolarisResource> Optional<R> getFirstResponse(final Request request, final Type resourcesType) throws IntegrationException {
        try (final Response response = polarisHttpClient.execute(request)) {
            response.throwExceptionForError();
            final PolarisResources<R> wrappedResponse = polarisJsonTransformer.getResponse(response, resourcesType);
            if (wrappedResponse != null) {
                final List<R> data = wrappedResponse.getData();
                if (null != data && !data.isEmpty()) {
                    return Optional.ofNullable(data.get(0));
                }
            }
            return Optional.empty();
        } catch (final IOException e) {
            throw new IntegrationException(e);
        }
    }

    public <R extends PolarisResource> List<R> getAllResponses(final Request request, final Type resourcesType) throws IntegrationException {
        try (final Response response = polarisHttpClient.execute(request)) {
            response.throwExceptionForError();
            final PolarisResources<R> wrappedResponse = polarisJsonTransformer.getResponse(response, resourcesType);
            if (wrappedResponse != null && wrappedResponse.getData() != null) {
                return wrappedResponse.getData();
            }
            return Collections.emptyList();
        } catch (final IOException e) {
            throw new IntegrationException(e);
        }
    }

    public <R extends PolarisResource> List<R> getAllResponses(final PolarisPagedRequestWrapper polarisPagedRequestWrapper) throws IntegrationException {
        return getAllResponses(polarisPagedRequestWrapper, defaultPageSize);
    }

    public <R extends PolarisResource, W extends PolarisResources<R>> List<R> getAllResponses(final PolarisPagedRequestWrapper polarisPagedRequestWrapper, final int pageSize) throws IntegrationException {
        final W populatedResponse = getPopulatedResponse(polarisPagedRequestWrapper, pageSize);

        if (populatedResponse == null) {
            return Collections.emptyList();
        }

        return populatedResponse.getData();
    }

    public <R extends PolarisResource, W extends PolarisResources<R>> W getPopulatedResponse(final PolarisPagedRequestWrapper polarisPagedRequestWrapper) throws IntegrationException {
        return getPopulatedResponse(polarisPagedRequestWrapper, defaultPageSize);
    }

    // TODO: Cognitive complexity should be reduced even more here --rotte APR 2020
    public <R extends PolarisResource, W extends PolarisResources<R>> W getPopulatedResponse(final PolarisPagedRequestWrapper polarisPagedRequestWrapper, final int pageSize) throws IntegrationException {
        W populatedResources = null;
        final List<R> allData = new ArrayList<>();
        final List<PolarisResourceSparse> allIncluded = new ArrayList<>();

        Integer totalExpected = null;
        int offset = 0;
        boolean totalExpectedHasNotBeenSet = true;
        boolean thisPageHadData;
        boolean isMoreData = true;
        do {
            final W wrappedResponse = executePagedRequest(polarisPagedRequestWrapper, offset, pageSize);
            if (wrappedResponse == null) {
                break;
            }

            if (null == populatedResources) {
                populatedResources = wrappedResponse;
            }

            if (totalExpectedHasNotBeenSet) {
                final PolarisResourcesPagination meta = wrappedResponse.getMeta();
                totalExpected = Optional.ofNullable(meta)
                                    .map(PolarisResourcesPagination::getTotal)
                                    .map(BigDecimal::intValue)
                                    .orElse(null);
                totalExpectedHasNotBeenSet = false;
            }

            final List<R> data = Optional.ofNullable(wrappedResponse.getData()).orElse(Collections.emptyList());
            allData.addAll(data);

            final List<PolarisResourceSparse> included = Optional.ofNullable(wrappedResponse.getIncluded()).orElse(Collections.emptyList());
            allIncluded.addAll(included);

            if (totalExpected != null) {
                isMoreData = totalExpected > allData.size();
            }
            thisPageHadData = !data.isEmpty();
            offset += pageSize;
        } while (isMoreData && thisPageHadData);

        // If wrappedResponse is null, populatedResources could be null -- rotte APR 2020
        if (populatedResources != null) {
            populatedResources.setData(new ArrayList<>(allData));
            populatedResources.setIncluded(new ArrayList<>(allIncluded));
        }
        return populatedResources;
    }

    private <R extends PolarisResource, W extends PolarisResources<R>> W executePagedRequest(final PolarisPagedRequestWrapper polarisPagedRequestWrapper, final int offset, final int limit) throws IntegrationException {
        final Request pagedRequest = polarisPagedRequestWrapper.getRequestCreator().apply(limit, offset);
        try (final Response response = polarisHttpClient.execute(pagedRequest)) {
            response.throwExceptionForError();
            return polarisJsonTransformer.getResponse(response, polarisPagedRequestWrapper.getResponseType());
        } catch (final IOException e) {
            throw new IntegrationException("Problem handling request", e);
        }
    }

}
