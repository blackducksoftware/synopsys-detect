/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.polaris.common.request.PolarisRequestFactory;
import com.synopsys.integration.polaris.common.rest.AccessTokenPolarisHttpClient;
import com.synopsys.integration.rest.RestConstants;

public class PolarisServicesFactory {
    private final IntLogger logger;
    private final AccessTokenPolarisHttpClient httpClient;
    private final Gson gson;
    private final PolarisJsonTransformer polarisJsonTransformer;
    private int defaultPageSize;

    public PolarisServicesFactory(final IntLogger logger, final AccessTokenPolarisHttpClient httpClient, final Gson gson) {
        this.logger = logger;
        this.httpClient = httpClient;
        this.gson = gson;
        this.polarisJsonTransformer = new PolarisJsonTransformer(gson, logger);
        this.defaultPageSize = PolarisRequestFactory.DEFAULT_LIMIT;
    }

    public static Gson createDefaultGson() {
        return PolarisServicesFactory.createDefaultGsonBuilder().create();
    }

    public static GsonBuilder createDefaultGsonBuilder() {
        return new GsonBuilder().setDateFormat(RestConstants.JSON_DATE_FORMAT);
    }

    public PolarisService createPolarisService() {
        return new PolarisService(httpClient, polarisJsonTransformer, defaultPageSize);
    }

    public BranchService createBranchService() {
        return new BranchService(httpClient, createPolarisService());
    }

    public IssueService createIssueService() {
        return new IssueService(httpClient, createPolarisService());
    }

    public JobService createJobService() {
        return new JobService(httpClient, createPolarisService());
    }

    public ProjectService createProjectService() {
        return new ProjectService(httpClient, createPolarisService());
    }

    public AuthService createAuthService() {
        return new AuthService(httpClient, createPolarisService());
    }

    public RoleAssignmentService createRoleAssignmentService() {
        return new RoleAssignmentService(httpClient, createPolarisService(), createAuthService());
    }

    public UserService createUserService() {
        return new UserService(createAuthService());
    }

    public GroupService createGroupService() {
        return new GroupService(createAuthService());
    }

    public CountService createCountService() {
        return new CountService(createPolarisService());
    }

    public ContextsService createContextsService() {
        return new ContextsService(createPolarisService(), httpClient);
    }

    public IntLogger getLogger() {
        return logger;
    }

    public Gson getGson() {
        return gson;
    }

    public AccessTokenPolarisHttpClient getHttpClient() {
        return httpClient;
    }

    public void setDefaultPageSize(final int defaultPageSize) {
        if (defaultPageSize >= 0) {
            this.defaultPageSize = defaultPageSize;
        }
    }

}
