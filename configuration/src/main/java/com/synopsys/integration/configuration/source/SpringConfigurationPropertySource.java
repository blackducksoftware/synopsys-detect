/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.configuration.source;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException;
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import com.synopsys.integration.configuration.util.Bds;

public class SpringConfigurationPropertySource implements PropertySource {
    private String name;
    private IterableConfigurationPropertySource propertySource;

    public SpringConfigurationPropertySource(final String name, final IterableConfigurationPropertySource propertySource) {
        this.name = name;
        this.propertySource = propertySource;
    }

    public static List<SpringConfigurationPropertySource> fromConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment) {
        return fromConfigurableEnvironment(configurableEnvironment, true);
    }

    public static List<SpringConfigurationPropertySource> fromConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment, boolean ignoreUnknown) {
        List<ConfigurationPropertySource> sources = Bds.listOf(ConfigurationPropertySources.get(configurableEnvironment));
        return Bds.of(sources).map(it -> {
            if (IterableConfigurationPropertySource.class.isAssignableFrom(it.getClass())) {
                Object underlying = it.getUnderlyingSource();
                if (org.springframework.core.env.PropertySource.class.isAssignableFrom(underlying.getClass())) {
                    org.springframework.core.env.PropertySource springSource = (org.springframework.core.env.PropertySource) underlying;
                    return new SpringConfigurationPropertySource(springSource.getName(), (IterableConfigurationPropertySource) it);
                } else {
                    if (ignoreUnknown) {
                        return null;
                    } else {
                        throw new RuntimeException(
                            new UnknownSpringConfigurationException("Unknown underlying spring configuration source. We may be unable to determine where a property originated. Likely a new property source type should be tested against."));
                    }
                }
            } else if (RandomValuePropertySource.class.isAssignableFrom(it.getClass())) {
                //We know an underlying random source can't be iterated but we don't care. It can't give a list of known keys.
                return null;
            } else {
                if (ignoreUnknown) {
                    return null;
                } else {
                    throw new RuntimeException(
                        new UnknownSpringConfigurationException("Unknown spring configuration type. We may be unable to find property information from it correctly. Likely a new configuration property source should be tested against. "));
                }
            }
        }).filterNotNull().toList();

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
    public Boolean hasKey(final String key) {
        Optional<ConfigurationPropertyName> configurationPropertyName = toConfigurationName(key);
        return configurationPropertyName.filter(propertyName -> propertySource.getConfigurationProperty(propertyName) != null).isPresent();
    }

    @Override
    public Set<String> getKeys() {
        return Bds.of(propertySource).map(ConfigurationPropertyName::toString).toSet();
    }

    @Override
    public String getValue(final String key) {
        return toConfigurationProperty(key).map(ConfigurationProperty::getValue).map(Object::toString).orElse(null);
    }

    @Override
    public String getOrigin(final String key) {
        return toConfigurationProperty(key).map(ConfigurationProperty::getOrigin).map(Object::toString).orElse(null);
    }

    @Override
    public String getName() {
        return name;
    }
}