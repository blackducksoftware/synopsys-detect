package com.blackducksoftware.integration.hub.detect.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.log.Slf4jIntLogger;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnection;
import com.blackducksoftware.integration.rest.connection.UnauthenticatedRestConnectionBuilder;
import com.blackducksoftware.integration.rest.proxy.ProxyInfo;
import com.blackducksoftware.integration.rest.proxy.ProxyInfoBuilder;

public class DetectConfigurationUtility {
    private final DetectConfiguration detectConfiguration;
    private final Logger logger = LoggerFactory.getLogger(DetectConfiguration.class);

    public DetectConfigurationUtility(final DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public ProxyInfo getHubProxyInfo() throws DetectUserFriendlyException {
        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        proxyInfoBuilder.setHost(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_HOST));
        proxyInfoBuilder.setPort(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PORT));
        proxyInfoBuilder.setUsername(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_USERNAME));
        proxyInfoBuilder.setPassword(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_PASSWORD));
        proxyInfoBuilder.setIgnoredProxyHosts(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_IGNORED_HOSTS));
        proxyInfoBuilder.setNtlmDomain(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_DOMAIN));
        proxyInfoBuilder.setNtlmWorkstation(detectConfiguration.getProperty(DetectProperty.BLACKDUCK_PROXY_NTLM_WORKSTATION));
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
        restConnectionBuilder.setTimeout(detectConfiguration.getIntegerProperty(DetectProperty.BLACKDUCK_TIMEOUT));
        restConnectionBuilder.applyProxyInfo(getHubProxyInfo());
        restConnectionBuilder.setLogger(new Slf4jIntLogger(logger));
        restConnectionBuilder.setAlwaysTrustServerCertificate(detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_TRUST_CERT));

        return restConnectionBuilder.build();
    }
}
