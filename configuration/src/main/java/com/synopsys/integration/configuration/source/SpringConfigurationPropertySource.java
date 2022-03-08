package com.synopsys.integration.configuration.source;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.ConfigurablePropertyResolver;

import com.synopsys.integration.common.util.Bds;

public class SpringConfigurationPropertySource implements PropertySource {
    private final String name;
    private final IterableConfigurationPropertySource propertySource;
    private final ConfigurablePropertyResolver configurablePropertyResolver;

    public SpringConfigurationPropertySource(String name, IterableConfigurationPropertySource propertySource, ConfigurablePropertyResolver configurablePropertyResolver) {
        this.name = name;
        this.propertySource = propertySource;
        this.configurablePropertyResolver = configurablePropertyResolver;
    }

    public static List<SpringConfigurationPropertySource> fromConfigurableEnvironmentSafely(
        ConfigurableEnvironment configurableEnvironment,
        BiConsumer<String, Exception> unknownConsumer
    ) {
        try {
            return new ArrayList<>(fromConfigurableEnvironment(configurableEnvironment, false));
        } catch (RuntimeException e) {
            unknownConsumer.accept("An unknown property source was found, will ignore unknown property source and proceed.", e);
            return new ArrayList<>(fromConfigurableEnvironment(configurableEnvironment, true));
        }
    }

    // TODO - this should return an Optional
    public static List<SpringConfigurationPropertySource> fromConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment, boolean ignoreUnknown) {
        List<ConfigurationPropertySource> sources = Bds.listOf(ConfigurationPropertySources.get(configurableEnvironment));
        return Bds.of(sources).map(it -> {
            if (IterableConfigurationPropertySource.class.isAssignableFrom(it.getClass())) {
                return getPropertySource(configurableEnvironment, ignoreUnknown, it);
            } else if (RandomValuePropertySource.class.isAssignableFrom(it.getUnderlyingSource().getClass())) {
                //We know an underlying random source can't be iterated but we don't care. It can't give a list of known keys.
                return null;
            } else {
                if (ignoreUnknown) {
                    return null;
                } else {
                    throw new RuntimeException(
                        new UnknownSpringConfigurationException(
                            "Unknown spring configuration type. We may be unable to find property information from it correctly. Likely a new configuration property source should be tested against. "
                        ));
                }
            }
        }).filterNotNull().toList();

    }

    // TODO - this should return an Optional
    private static SpringConfigurationPropertySource getPropertySource(
        ConfigurableEnvironment configurableEnvironment,
        boolean ignoreUnknown,
        ConfigurationPropertySource configurationPropertySource
    ) {
        Object underlying = configurationPropertySource.getUnderlyingSource();
        if (org.springframework.core.env.PropertySource.class.isAssignableFrom(underlying.getClass())) {
            org.springframework.core.env.PropertySource springSource = (org.springframework.core.env.PropertySource) underlying;
            return new SpringConfigurationPropertySource(springSource.getName(), (IterableConfigurationPropertySource) configurationPropertySource, configurableEnvironment);
        } else {
            if (ignoreUnknown) {
                return null;
            } else {
                throw new RuntimeException(
                    new UnknownSpringConfigurationException(
                        "Unknown underlying spring configuration source. We may be unable to determine where a property originated. Likely a new property source type should be tested against."));
            }
        }
    }

    @NotNull
    private Optional<ConfigurationPropertyName> toConfigurationName(String key) {
        try {
            return Optional.of(ConfigurationPropertyName.of(key));
        } catch (InvalidConfigurationPropertyNameException e) {
            return Optional.empty();
        }
    }

    private Optional<ConfigurationProperty> toConfigurationProperty(String key) {
        return toConfigurationName(key).map(name -> propertySource.getConfigurationProperty(name));
    }

    @Override
    public Boolean hasKey(String key) {
        Optional<ConfigurationPropertyName> configurationPropertyName = toConfigurationName(key);
        return configurationPropertyName.filter(propertyName -> propertySource.getConfigurationProperty(propertyName) != null).isPresent();
    }

    @Override
    public Set<String> getKeys() {
        return Bds.of(propertySource).map(ConfigurationPropertyName::toString).toSet();
    }

    @Override
    //Spring resolves configuration properties using a configurable property resolver (resolves --prop=${VAR} replaces VAR from an environment variable of the same name.)
    //The value provided from the configurable property itself or the property source is not resolved, so we have to get the final value from a resolver.
    //Theoretically, this resolver should be bound to the property source, but this current method at least gets the final resolved value. - jp
    public String getValue(String key) {
        return toConfigurationProperty(key)
            .map(ConfigurationProperty::getName)
            .map(ConfigurationPropertyName::toString)
            .map(configurablePropertyResolver::getProperty)
            .map(Object::toString)
            .orElse(null);
    }

    @Override
    public String getOrigin(String key) {
        return toConfigurationProperty(key).map(ConfigurationProperty::getOrigin).map(Object::toString).orElse(null);
    }

    @Override
    public String getName() {
        return name;
    }
}