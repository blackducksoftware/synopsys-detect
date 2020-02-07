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
package com.synopsys.integration.configuration.source

import org.springframework.boot.context.properties.source.ConfigurationPropertyName
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.boot.context.properties.source.InvalidConfigurationPropertyNameException
import org.springframework.boot.context.properties.source.IterableConfigurationPropertySource
import org.springframework.boot.env.RandomValuePropertySource
import org.springframework.core.env.ConfigurableEnvironment

class UnknownSpringConfiguration(msg: String) : Exception(msg)

class SpringConfigurationPropertySource(private val name: String, private val propertySource: IterableConfigurationPropertySource) : PropertySource {
    companion object {
        fun fromConfigurableEnvironmentSafely(configurableEnvironment: ConfigurableEnvironment): List<SpringConfigurationPropertySource> {
            return fromConfigurableEnvironment(configurableEnvironment, true);
        }

        @Throws(UnknownSpringConfiguration::class)
        fun fromConfigurableEnvironment(configurableEnvironment: ConfigurableEnvironment, ignoreUnknown: Boolean = true): List<SpringConfigurationPropertySource> {
            val sources = ConfigurationPropertySources.get(configurableEnvironment).toList()
            return sources.map {
                if (it is IterableConfigurationPropertySource) {
                    val underlying = it.underlyingSource
                    if (underlying is org.springframework.core.env.PropertySource<*>) {
                        SpringConfigurationPropertySource(underlying.name, it)
                    } else {
                        if (ignoreUnknown) null else throw UnknownSpringConfiguration("Unknown underlying spring configuration source. We may be unable to determine where a property originated. Likely a new property source type should be tested against.")
                    }
                } else if (it.underlyingSource is RandomValuePropertySource) {
                    //We know an underlying random source can't be iterated but we don't care. It can't give a list of known keys.
                    null
                } else {
                    if (ignoreUnknown) null else throw UnknownSpringConfiguration("Unknown spring configuration type. We may be unable to find property information from it correctly. Likely a new configuration property source should be tested against. ")
                }
            }.filterNotNull()
        }
    }

    private fun toConfigurationName(key: String): ConfigurationPropertyName? {
        return try {
            ConfigurationPropertyName.of(key)
        } catch (e: InvalidConfigurationPropertyNameException) {
            null
        }
    }

    override fun hasKey(key: String): Boolean {
        return toConfigurationName(key)?.let { propertySource.getConfigurationProperty(it) } != null
    }

    override fun getValue(key: String): String? {
        return toConfigurationName(key)?.let { propertySource.getConfigurationProperty(it)?.value?.toString() }
    }

    override fun getName(): String = name

    override fun getOrigin(key: String): String? {
        return toConfigurationName(key)?.let { propertySource.getConfigurationProperty(it)?.origin?.toString() }
    }

    override fun getKeys(): Set<String> {
        return propertySource.map { it.toString() }.toSet()
    }
}