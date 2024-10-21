package com.blackduck.integration.detect.junitextensions;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.blackduck.integration.blackduck.configuration.BlackDuckServerConfig;
import com.blackduck.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.blackduck.integration.blackduck.configuration.BlackDuckServerConfigKeys;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.builder.BuilderStatus;
import com.blackduck.integration.log.IntLogger;
import com.blackduck.integration.log.SilentIntLogger;

public class BlackDuckParametersExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(BlackDuck.class) && parameterContext.getParameter().getType().isAssignableFrom(BlackDuckServicesFactory.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = new BlackDuckServerConfigBuilder(BlackDuckServerConfigKeys.KEYS.apiToken);
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());

        BuilderStatus builderStatus = blackDuckServerConfigBuilder.validateAndGetBuilderStatus();
        if (!builderStatus.isValid()) {
            throw new ParameterResolutionException("A valid Black Duck configuration is required:\n" + builderStatus.getFullErrorMessage("\n"));
        }

        BlackDuckServerConfig blackDuckServerConfig = blackDuckServerConfigBuilder.build();
        if (!blackDuckServerConfig.canConnect()) {
            throw new ParameterResolutionException(String.format("Unable to connect to %s.", blackDuckServerConfig.getBlackDuckUrl().string()));
        }

        IntLogger logger = FieldSupport.getValueOrDefault(extensionContext, "blackDuckLogger", new SilentIntLogger());
        return blackDuckServerConfig.createBlackDuckServicesFactory(logger);
    }

}
