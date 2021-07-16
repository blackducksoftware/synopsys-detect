package com.synopsys.integration.detect.junitextensions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.util.ReflectionUtils;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfig;
import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.builder.BuilderStatus;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.SilentIntLogger;

public class BlackDuckParametersExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return parameterContext.isAnnotated(BlackDuck.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        BlackDuckServerConfigBuilder blackDuckServerConfigBuilder = new BlackDuckServerConfigBuilder();
        blackDuckServerConfigBuilder.setProperties(System.getenv().entrySet());

        BuilderStatus builderStatus = blackDuckServerConfigBuilder.validateAndGetBuilderStatus();
        if (!builderStatus.isValid()) {
            throw new ParameterResolutionException("A valid Black Duck configuration is required:\n" + builderStatus.getFullErrorMessage("\n"));
        }

        BlackDuckServerConfig blackDuckServerConfig = blackDuckServerConfigBuilder.build();
        if (!blackDuckServerConfig.canConnect()) {
            throw new ParameterResolutionException(String.format("Unable to connect to %s.", blackDuckServerConfig.getBlackDuckUrl().string()));
        }

        Object testInstance = extensionContext.getTestInstance().get();
        Function<IntLogger, BlackDuckServicesFactory> createFactory = blackDuckServerConfig::createBlackDuckServicesFactory;
        return FieldSupport.useField(testInstance, "blackDuckLogger", createFactory, new SilentIntLogger());
    }

}
