/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.configuration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.Gson;
import com.synopsys.integration.builder.BuilderProperties;
import com.synopsys.integration.builder.BuilderPropertyKey;
import com.synopsys.integration.builder.BuilderStatus;
import com.synopsys.integration.builder.IntegrationBuilder;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.credentials.Credentials;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;
import com.synopsys.integration.rest.support.AuthenticationSupport;

public class PolarisServerConfigBuilder extends IntegrationBuilder<PolarisServerConfig> {
    public static final String SWIP_CONFIG_DIRECTORY_DEFAULT = ".swip";
    public static final String POLARIS_CONFIG_DIRECTORY_DEFAULT = ".polaris";
    public static final String POLARIS_ACCESS_TOKEN_FILENAME_DEFAULT = ".access_token";

    public static final BuilderPropertyKey URL_KEY = new BuilderPropertyKey("POLARIS_SERVER_URL");
    public static final BuilderPropertyKey ACCESS_TOKEN_KEY = new BuilderPropertyKey("POLARIS_ACCESS_TOKEN");
    public static final BuilderPropertyKey TRUST_CERT_KEY = new BuilderPropertyKey("POLARIS_TRUST_CERT");
    public static final BuilderPropertyKey TIMEOUT_KEY = new BuilderPropertyKey("POLARIS_TIMEOUT");
    public static final BuilderPropertyKey POLARIS_HOME_KEY = new BuilderPropertyKey("POLARIS_HOME");
    public static final BuilderPropertyKey ACCESS_TOKEN_FILE_PATH_KEY = new BuilderPropertyKey("POLARIS_ACCESS_TOKEN_FILE");
    public static final BuilderPropertyKey USER_HOME_KEY = new BuilderPropertyKey("USER_HOME");
    public static final BuilderPropertyKey PROXY_HOST_KEY = new BuilderPropertyKey("POLARIS_PROXY_HOST");
    public static final BuilderPropertyKey PROXY_PORT_KEY = new BuilderPropertyKey("POLARIS_PROXY_PORT");
    public static final BuilderPropertyKey PROXY_USERNAME_KEY = new BuilderPropertyKey("POLARIS_PROXY_USERNAME");
    public static final BuilderPropertyKey PROXY_PASSWORD_KEY = new BuilderPropertyKey("POLARIS_PROXY_PASSWORD");
    public static final BuilderPropertyKey PROXY_NTLM_DOMAIN_KEY = new BuilderPropertyKey("POLARIS_PROXY_NTLM_DOMAIN");
    public static final BuilderPropertyKey PROXY_NTLM_WORKSTATION_KEY = new BuilderPropertyKey("POLARIS_PROXY_NTLM_WORKSTATION");

    public static final int DEFAULT_TIMEOUT_SECONDS = 120;

    private final BuilderProperties builderProperties;
    private IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
    private Gson gson = new Gson();
    private AuthenticationSupport authenticationSupport = new AuthenticationSupport();

    public PolarisServerConfigBuilder() {
        Set<BuilderPropertyKey> propertyKeys = new HashSet<>();
        propertyKeys.add(URL_KEY);
        propertyKeys.add(ACCESS_TOKEN_KEY);
        propertyKeys.add(TIMEOUT_KEY);
        propertyKeys.add(PROXY_HOST_KEY);
        propertyKeys.add(PROXY_PORT_KEY);
        propertyKeys.add(PROXY_USERNAME_KEY);
        propertyKeys.add(PROXY_PASSWORD_KEY);
        propertyKeys.add(PROXY_NTLM_DOMAIN_KEY);
        propertyKeys.add(PROXY_NTLM_WORKSTATION_KEY);
        propertyKeys.add(TRUST_CERT_KEY);
        builderProperties = new BuilderProperties(propertyKeys);

        builderProperties.set(TIMEOUT_KEY, Integer.toString(PolarisServerConfigBuilder.DEFAULT_TIMEOUT_SECONDS));
    }

    @Override
    protected PolarisServerConfig buildWithoutValidation() {
        HttpUrl polarisURL = null;
        try {
            polarisURL = new HttpUrl(getUrl());
        } catch (IntegrationException ignored) {
        }

        return new PolarisServerConfig(polarisURL, getTimeoutInSeconds(), getAccessToken(), getProxyInfo(), isTrustCert(), gson, authenticationSupport);
    }

    private ProxyInfo getProxyInfo() {
        if (StringUtils.isBlank(getProxyHost())) {
            return ProxyInfo.NO_PROXY_INFO;
        }

        CredentialsBuilder credentialsBuilder = Credentials.newBuilder();
        credentialsBuilder.setUsernameAndPassword(getProxyUsername(), getProxyPassword());
        Credentials proxyCredentials = credentialsBuilder.build();

        ProxyInfoBuilder proxyInfoBuilder = ProxyInfo.newBuilder();
        proxyInfoBuilder.setHost(getProxyHost());
        proxyInfoBuilder.setPort(getProxyPort());
        proxyInfoBuilder.setCredentials(proxyCredentials);
        proxyInfoBuilder.setNtlmDomain(getProxyNtlmDomain());
        proxyInfoBuilder.setNtlmWorkstation(getProxyNtlmWorkstation());

        return proxyInfoBuilder.build();
    }

    @Override
    protected void validate(BuilderStatus builderStatus) {
        if (StringUtils.isBlank(getUrl())) {
            builderStatus.addErrorMessage("The Polaris url must be specified.");
        } else {
            try {
                URL blackDuckURL = new URL(getUrl());
                blackDuckURL.toURI();
            } catch (MalformedURLException | URISyntaxException e) {
                builderStatus.addErrorMessage(String.format("The provided Polaris url (%s) is not a valid URL.", getUrl()));
            }
        }

        PolarisAccessTokenResolver accessTokenResolver = new PolarisAccessTokenResolver(logger, builderStatus, getAccessToken(), getPolarisHome(), getAccessTokenFilePath(), getUserHome());
        Optional<String> optionalAccessToken = accessTokenResolver.resolveAccessToken();
        if (!optionalAccessToken.isPresent()) {
            builderStatus.addErrorMessage("An access token must be resolvable from one of the following (this is also the order of precedence):");
            builderStatus.addErrorMessage(" - set explicitly");
            builderStatus.addErrorMessage(" - set from property (POLARIS_ACCESS_TOKEN, SWIP_ACCESS_TOKEN)");
            builderStatus.addErrorMessage(" - found in a provided file path (POLARIS_ACCESS_TOKEN_FILE, SWIP_ACCESS_TOKEN_FILE)");
            builderStatus.addErrorMessage(" - found in the '.access_token' file in a Polaris home directory (POLARIS_HOME, SWIP_HOME, or defaults to USER_HOME/.swip or USER_HOME/.polaris, depending on your Polaris version.)");
        } else {
            setAccessToken(optionalAccessToken.get());
        }

        if (getTimeoutInSeconds() <= 0) {
            builderStatus.addErrorMessage("A timeout (in seconds) greater than zero must be specified.");
        }

        CredentialsBuilder proxyCredentialsBuilder = new CredentialsBuilder();
        proxyCredentialsBuilder.setUsername(getProxyUsername());
        proxyCredentialsBuilder.setPassword(getProxyPassword());
        BuilderStatus proxyCredentialsBuilderStatus = proxyCredentialsBuilder.validateAndGetBuilderStatus();
        if (!proxyCredentialsBuilderStatus.isValid()) {
            builderStatus.addErrorMessage("The proxy credentials were not valid.");
            builderStatus.addAllErrorMessages(proxyCredentialsBuilderStatus.getErrorMessages());
        } else {
            Credentials proxyCredentials = proxyCredentialsBuilder.build();
            ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
            proxyInfoBuilder.setCredentials(proxyCredentials);
            proxyInfoBuilder.setHost(getProxyHost());
            proxyInfoBuilder.setPort(getProxyPort());
            proxyInfoBuilder.setNtlmDomain(getProxyNtlmDomain());
            proxyInfoBuilder.setNtlmWorkstation(getProxyNtlmWorkstation());
            BuilderStatus proxyInfoBuilderStatus = proxyInfoBuilder.validateAndGetBuilderStatus();
            if (!proxyInfoBuilderStatus.isValid()) {
                builderStatus.addAllErrorMessages(proxyInfoBuilderStatus.getErrorMessages());
            }
        }
    }

    private BuilderPropertyKey resolveKey(String key) {
        String fixedKey = key.toUpperCase().replace(".", "_");
        return new BuilderPropertyKey(fixedKey);
    }

    public String get(final BuilderPropertyKey key) {
        BuilderPropertyKey resolvedKey = resolveKey(key.getKey());
        return builderProperties.get(resolvedKey);
    }

    public void set(final BuilderPropertyKey key, final String value) {
        BuilderPropertyKey resolvedKey = resolveKey(key.getKey());
        builderProperties.set(resolvedKey, value);
    }

    public void setProperty(final String key, final String value) {
        String resolvedKey = resolveKey(key).getKey();
        builderProperties.setProperty(resolvedKey, value);
    }

    public Set<BuilderPropertyKey> getKeys() {
        return new HashSet<>(builderProperties.getKeys());
    }

    public Set<String> getPropertyKeys() {
        return new HashSet<>(builderProperties.getPropertyKeys());
    }

    public Set<String> getEnvironmentVariableKeys() {
        return new HashSet<>(builderProperties.getEnvironmentVariableKeys());
    }

    public Map<BuilderPropertyKey, String> getProperties() {
        return builderProperties.getProperties();
    }

    public void setProperties(final Set<? extends Map.Entry<String, String>> propertyEntries) {
        propertyEntries.forEach(entry -> setProperty(entry.getKey(), entry.getValue()));
    }

    public IntLogger getLogger() {
        return logger;
    }

    public PolarisServerConfigBuilder setLogger(IntLogger logger) {
        if (null != logger) {
            this.logger = logger;
        }
        return this;
    }

    public Gson getGson() {
        return gson;
    }

    public PolarisServerConfigBuilder setGson(Gson gson) {
        if (null != gson) {
            this.gson = gson;
        }
        return this;
    }

    public AuthenticationSupport getAuthenticationSupport() {
        return authenticationSupport;
    }

    public PolarisServerConfigBuilder setAuthenticationSupport(AuthenticationSupport authenticationSupport) {
        if (null != authenticationSupport) {
            this.authenticationSupport = authenticationSupport;
        }
        return this;
    }

    public String getUrl() {
        return builderProperties.get(URL_KEY);
    }

    public PolarisServerConfigBuilder setUrl(String url) {
        builderProperties.set(URL_KEY, url);
        return this;
    }

    public String getAccessToken() {
        return builderProperties.get(ACCESS_TOKEN_KEY);
    }

    public PolarisServerConfigBuilder setAccessToken(String accessToken) {
        builderProperties.set(ACCESS_TOKEN_KEY, accessToken);
        return this;
    }

    public int getTimeoutInSeconds() {
        return NumberUtils.toInt(builderProperties.get(TIMEOUT_KEY), PolarisServerConfigBuilder.DEFAULT_TIMEOUT_SECONDS);
    }

    public PolarisServerConfigBuilder setTimeoutInSeconds(String timeout) {
        builderProperties.set(TIMEOUT_KEY, timeout);
        return this;
    }

    public PolarisServerConfigBuilder setTimeoutInSeconds(int timeout) {
        setTimeoutInSeconds(String.valueOf(timeout));
        return this;
    }

    public String getPolarisHome() {
        return builderProperties.get(POLARIS_HOME_KEY);
    }

    public PolarisServerConfigBuilder setPolarisHome(String polarisHome) {
        builderProperties.set(POLARIS_HOME_KEY, polarisHome);
        return this;
    }

    public String getAccessTokenFilePath() {
        return builderProperties.get(ACCESS_TOKEN_FILE_PATH_KEY);
    }

    public PolarisServerConfigBuilder setAccessTokenFilePath(String accessTokenFilePath) {
        builderProperties.set(ACCESS_TOKEN_FILE_PATH_KEY, accessTokenFilePath);
        return this;
    }

    public String getUserHome() {
        return builderProperties.get(USER_HOME_KEY);
    }

    public PolarisServerConfigBuilder setUserHome(String userHome) {
        builderProperties.set(USER_HOME_KEY, userHome);
        return this;
    }

    public String getProxyHost() {
        return builderProperties.get(PROXY_HOST_KEY);
    }

    public PolarisServerConfigBuilder setProxyHost(String proxyHost) {
        builderProperties.set(PROXY_HOST_KEY, proxyHost);
        return this;
    }

    public int getProxyPort() {
        return NumberUtils.toInt(builderProperties.get(PROXY_PORT_KEY), 0);
    }

    public PolarisServerConfigBuilder setProxyPort(String proxyPort) {
        builderProperties.set(PROXY_PORT_KEY, proxyPort);
        return this;
    }

    public PolarisServerConfigBuilder setProxyPort(int proxyPort) {
        setProxyPort(String.valueOf(proxyPort));
        return this;
    }

    public String getProxyUsername() {
        return builderProperties.get(PROXY_USERNAME_KEY);
    }

    public PolarisServerConfigBuilder setProxyUsername(String proxyUsername) {
        builderProperties.set(PROXY_USERNAME_KEY, proxyUsername);
        return this;
    }

    public String getProxyPassword() {
        return builderProperties.get(PROXY_PASSWORD_KEY);
    }

    public PolarisServerConfigBuilder setProxyPassword(String proxyPassword) {
        builderProperties.set(PROXY_PASSWORD_KEY, proxyPassword);
        return this;
    }

    public String getProxyNtlmDomain() {
        return builderProperties.get(PROXY_NTLM_DOMAIN_KEY);
    }

    public PolarisServerConfigBuilder setProxyNtlmDomain(String proxyNtlmDomain) {
        builderProperties.set(PROXY_NTLM_DOMAIN_KEY, proxyNtlmDomain);
        return this;
    }

    public String getProxyNtlmWorkstation() {
        return builderProperties.get(PROXY_NTLM_WORKSTATION_KEY);
    }

    public PolarisServerConfigBuilder setProxyNtlmWorkstation(String proxyNtlmWorkstation) {
        builderProperties.set(PROXY_NTLM_WORKSTATION_KEY, proxyNtlmWorkstation);
        return this;
    }

    public boolean isTrustCert() {
        return Boolean.parseBoolean(builderProperties.get(TRUST_CERT_KEY));
    }

    public PolarisServerConfigBuilder setTrustCert(String trustCert) {
        builderProperties.set(TRUST_CERT_KEY, trustCert);
        return this;
    }

    public PolarisServerConfigBuilder setTrustCert(boolean trustCert) {
        setTrustCert(String.valueOf(trustCert));
        return this;
    }

}
