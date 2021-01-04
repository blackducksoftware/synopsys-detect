/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
