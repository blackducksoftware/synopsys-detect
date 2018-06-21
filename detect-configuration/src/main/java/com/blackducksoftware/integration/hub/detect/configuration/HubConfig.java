/**
 * detect-configuration
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.configuration;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.rest.proxy.ProxyInfo;
import com.blackducksoftware.integration.rest.proxy.ProxyInfoBuilder;

public class HubConfig extends BaseConfig {
    private final Logger logger = LoggerFactory.getLogger(HubConfig.class);

    public void initialize(final ValueContainer valueContainer) {
        this.testConnection = valueContainer.getTestConnection();
        this.apiTimeout = valueContainer.getApiTimeout();
        this.hubUrl = valueContainer.getHubUrl();
        this.hubTimeout = valueContainer.getHubTimeout();
        this.hubUsername = valueContainer.getHubUsername();
        this.hubPassword = valueContainer.getHubPassword();
        this.hubApiToken = valueContainer.getHubApiToken();
        this.hubProxyHost = valueContainer.getHubProxyHost();
        this.hubProxyPort = valueContainer.getHubProxyPort();
        this.hubProxyUsername = valueContainer.getHubProxyUsername();
        this.hubProxyPassword = valueContainer.getHubProxyPassword();
        this.hubProxyNtlmDomain = valueContainer.getHubProxyNtlmDomain();
        this.hubProxyNtlmWorkstation = valueContainer.getHubProxyNtlmWorkstation();
        this.hubProxyIgnoredHosts = valueContainer.getHubProxyIgnoredHosts();
        this.hubTrustCertificate = valueContainer.getHubTrustCertificate();
        this.hubOfflineMode = valueContainer.getHubOfflineMode();
        this.disableWithoutHub = valueContainer.getDisableWithoutHub();
        this.codeLocationNameOverride = valueContainer.getCodeLocationNameOverride();
        this.projectName = valueContainer.getProjectName();
        this.projectDescription = valueContainer.getProjectDescription();
        this.projectVersionName = valueContainer.getProjectVersionName();
        this.projectVersionNotes = valueContainer.getProjectVersionNotes();
        this.projectTier = valueContainer.getProjectTier();
        this.projectCodeLocationPrefix = valueContainer.getProjectCodeLocationPrefix();
        this.projectCodeLocationSuffix = valueContainer.getProjectCodeLocationSuffix();
        this.projectCodeLocationUnmap = valueContainer.getProjectCodeLocationUnmap();
        this.projectLevelMatchAdjustments = valueContainer.getProjectLevelMatchAdjustments();
        this.projectVersionPhase = valueContainer.getProjectVersionPhase();
        this.projectVersionDistribution = valueContainer.getProjectVersionDistribution();
        this.projectVersionUpdate = valueContainer.getProjectVersionUpdate();
        this.policyCheckFailOnSeverities = valueContainer.getPolicyCheckFailOnSeverities();
        this.riskReportPdf = valueContainer.getRiskReportPdf();
        this.riskReportPdfOutputDirectory = valueContainer.getRiskReportPdfOutputDirectory();
        this.noticesReport = valueContainer.getNoticesReport();
        this.noticesReportOutputDirectory = valueContainer.getNoticesReportOutputDirectory();
    }

    public ProxyInfo getHubProxyInfo() throws DetectUserFriendlyException {
        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        proxyInfoBuilder.setHost(hubProxyHost);
        proxyInfoBuilder.setPort(hubProxyPort);
        proxyInfoBuilder.setUsername(hubProxyUsername);
        proxyInfoBuilder.setPassword(hubProxyPassword);
        proxyInfoBuilder.setIgnoredProxyHosts(hubProxyIgnoredHosts);
        proxyInfoBuilder.setNtlmDomain(hubProxyNtlmDomain);
        proxyInfoBuilder.setNtlmWorkstation(hubProxyNtlmWorkstation);
        ProxyInfo proxyInfo = ProxyInfo.NO_PROXY_INFO;
        try {
            proxyInfo = proxyInfoBuilder.build();
        } catch (final IllegalStateException e) {
            throw new DetectUserFriendlyException(String.format("Your proxy configuration is not valid: %s", e.getMessage()), e, ExitCodeType.FAILURE_PROXY_CONNECTIVITY);
        }
        return proxyInfo;
    }

    public UnauthenticatedRestConnection createUnauthenticatedRestConnection(final String url) throws DetectUserFriendlyException {
        final UnauthenticatedRestConnectionBuilder restConnectionBuilder = new UnauthenticatedRestConnectionBuilder();
        restConnectionBuilder.setBaseUrl(url);
        restConnectionBuilder.setTimeout(getHubTimeout());
        restConnectionBuilder.applyProxyInfo(getHubProxyInfo());
        restConnectionBuilder.setLogger(new Slf4jIntLogger(logger));
        restConnectionBuilder.setAlwaysTrustServerCertificate(getHubTrustCertificate());

        return restConnectionBuilder.build();
    }

    // properties start

    private Boolean testConnection;

    private Long apiTimeout;

    private String hubUrl;

    private Integer hubTimeout;

    private String hubUsername;

    private String hubPassword;

    private String hubApiToken;

    private String hubProxyHost;

    private String hubProxyPort;

    private String hubProxyUsername;

    private String hubProxyPassword;

    private String hubProxyNtlmDomain;

    private String hubProxyNtlmWorkstation;

    private String hubProxyIgnoredHosts;

    private Boolean hubTrustCertificate;

    private Boolean hubOfflineMode;

    private Boolean disableWithoutHub;

    private String codeLocationNameOverride;

    private String projectName;

    private String projectDescription;

    private String projectVersionName;

    private String projectVersionNotes;

    private Integer projectTier;

    private String projectCodeLocationPrefix;

    private String projectCodeLocationSuffix;

    private Boolean projectCodeLocationUnmap;

    private String projectLevelMatchAdjustments;

    private String projectVersionPhase;

    private String projectVersionDistribution;

    private Boolean projectVersionUpdate;

    private String policyCheckFailOnSeverities;

    private Boolean riskReportPdf;

    private String riskReportPdfOutputDirectory;

    private Boolean noticesReport;

    private String noticesReportOutputDirectory;

    public boolean getTestConnection() {
        return BooleanUtils.toBoolean(testConnection);
    }

    public long getApiTimeout() {
        return convertLong(apiTimeout);
    }

    public String getHubUrl() {
        return hubUrl;
    }

    public int getHubTimeout() {
        return convertInt(hubTimeout);
    }

    public String getHubUsername() {
        return hubUsername;
    }

    public String getHubPassword() {
        return hubPassword;
    }

    public String getHubApiToken() {
        return hubApiToken;
    }

    public String getHubProxyHost() {
        return hubProxyHost;
    }

    public String getHubProxyPort() {
        return hubProxyPort;
    }

    public String getHubProxyUsername() {
        return hubProxyUsername;
    }

    public String getHubProxyPassword() {
        return hubProxyPassword;
    }

    public String getHubProxyIgnoredHosts() {
        return hubProxyIgnoredHosts;
    }

    public String getHubProxyNtlmDomain() {
        return hubProxyNtlmDomain;
    }

    public String getHubProxyNtlmWorkstation() {
        return hubProxyNtlmWorkstation;
    }

    public boolean getHubOfflineMode() {
        return BooleanUtils.toBoolean(hubOfflineMode);
    }

    public boolean getDisableWithoutHub() {
        return BooleanUtils.toBoolean(disableWithoutHub);
    }

    public boolean getHubTrustCertificate() {
        return BooleanUtils.toBoolean(hubTrustCertificate);
    }

    public String getProjectName() {
        return projectName == null ? null : projectName.trim();
    }

    public String getCodeLocationNameOverride() {
        return codeLocationNameOverride == null ? null : codeLocationNameOverride.trim();
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getProjectVersionName() {
        return projectVersionName == null ? null : projectVersionName.trim();
    }

    public String getProjectVersionNotes() {
        return projectVersionNotes;
    }

    // we want to perserve the possibility of a null tier
    public Integer getProjectTier() {
        return projectTier;
    }

    public String getProjectCodeLocationPrefix() {
        return projectCodeLocationPrefix == null ? null : projectCodeLocationPrefix.trim();
    }

    public String getProjectCodeLocationSuffix() {
        return projectCodeLocationSuffix == null ? null : projectCodeLocationSuffix.trim();
    }

    public boolean getProjectCodeLocationUnmap() {
        return BooleanUtils.toBoolean(projectCodeLocationUnmap);
    }

    public boolean getProjectLevelMatchAdjustments() {
        return BooleanUtils.toBoolean(projectLevelMatchAdjustments);
    }

    public String getProjectVersionPhase() {
        return projectVersionPhase == null ? null : projectVersionPhase.trim();
    }

    public String getProjectVersionDistribution() {
        return projectVersionDistribution == null ? null : projectVersionDistribution.trim();
    }

    public boolean getProjectVersionUpdate() {
        return BooleanUtils.toBoolean(projectVersionUpdate);
    }

    public String getPolicyCheckFailOnSeverities() {
        return policyCheckFailOnSeverities;
    }

    public Boolean getRiskReportPdf() {
        return riskReportPdf;
    }

    public String getRiskReportPdfOutputDirectory() {
        return riskReportPdfOutputDirectory;
    }

    public Boolean getNoticesReport() {
        return noticesReport;
    }

    public String getNoticesReportOutputDirectory() {
        return noticesReportOutputDirectory;
    }

    // properties end
}
