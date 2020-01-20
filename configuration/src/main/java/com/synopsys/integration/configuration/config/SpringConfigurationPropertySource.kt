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
package com.synopsys.integration.configuration.config

import org.springframework.boot.context.properties.source.ConfigurationPropertyName
import org.springframework.boot.context.properties.source.ConfigurationPropertySource
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource

class SpringConfigurationPropertySource(private val name: String, private val propertySource: ConfigurationPropertySource) : PropertySource {
    companion object {
        fun fromConfigurableEnvironment(configurableEnvironment: ConfigurableEnvironment): List<SpringConfigurationPropertySource> {
            val sources = ConfigurationPropertySources.get(configurableEnvironment).toList()
            return sources.map {
                val underlying = it.underlyingSource
                if (underlying is org.springframework.core.env.PropertySource<*>) {
                    SpringConfigurationPropertySource(underlying.name, it)
                } else {
                    SpringConfigurationPropertySource("unknown", it)
                }
            }
        }
    }

    override fun hasKey(key: String): Boolean {
        return null != propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key))
    }

    override fun getValue(key: String): String? {
        val property = propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key))
        if (property != null) {
            return property.value.toString()
        }
        return null
    }

    override fun getName(): String = name

    //A basic 'Spring Property Source' does not have the concept of Origin. Only Spring Configuration Property Sources do.
    override fun getOrigin(key: String): String? {
        val property = propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key))
        if (property != null) {
            val origin = property.origin
            if (origin == null) {
                return origin.toString()
            }
        }
        return getName()
    }

    override fun getKeys(): Set<String> {
        return if (propertySource is EnumerablePropertySource<*>) {
            propertySource.propertyNames.toSet()
        } else {
            emptySet()
        }
    }
}