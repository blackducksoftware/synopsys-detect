package com.synopsys.integration.polaris.common.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;
import com.synopsys.integration.polaris.common.api.auth.model.Context;
import com.synopsys.integration.polaris.common.api.auth.model.ContextAttributes;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;
import com.synopsys.integration.polaris.common.configuration.PolarisServerConfigBuilder;

public class ContextsServiceIT {
    private ContextsService contextsService;

    @BeforeEach
    public void createContextsService() {
        final PolarisServerConfigBuilder polarisServerConfigBuilder = PolarisServerConfig.newBuilder();
        polarisServerConfigBuilder.setUrl(System.getenv("POLARIS_URL"));
        polarisServerConfigBuilder.setAccessToken(System.getenv("POLARIS_ACCESS_TOKEN"));
        polarisServerConfigBuilder.setGson(new Gson());

        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getUrl()));
        assumeTrue(StringUtils.isNotBlank(polarisServerConfigBuilder.getAccessToken()));

        final PolarisServerConfig polarisServerConfig = polarisServerConfigBuilder.build();
        final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        final PolarisServicesFactory polarisServicesFactory = polarisServerConfig.createPolarisServicesFactory(logger);

        contextsService = polarisServicesFactory.createContextsService();
    }

    @Test
    public void testGetCurrentContext() {
        try {
            final Optional<Context> currentContext = contextsService.getCurrentContext();
            if (currentContext.isPresent()) {
                assertTrue(currentContext.map(Context::getAttributes)
                               .map(ContextAttributes::getCurrent)
                               .orElse(Boolean.FALSE));
            } else {
                assertTrue(contextsService.getAllContexts()
                               .stream()
                               .map(Context::getAttributes)
                               .map(ContextAttributes::getCurrent)
                               .noneMatch(Boolean.TRUE::equals));
            }
        } catch (final IntegrationException e) {
            fail("ContextsService encountered an unexpected exception when retrieving all contexts:", e);
        }
    }
}
