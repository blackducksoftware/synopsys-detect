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
package com.synopsys.integration.configuration.help

import com.synopsys.integration.configuration.config.PropertyConfiguration
import com.synopsys.integration.configuration.property.Property
import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.TypedProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty
import org.apache.commons.lang3.StringUtils
import java.util.function.Consumer

//The idea is that this is here to help you log information about a particular property configuration with particular things you want to express.
//  For example you may want to log deprecation warning when a particular property is set.
//  For example you may want to THROW when a particular deprecated property is set.
//  For example you may want to log when an invalid value is set.
//  For example you may want to THROW when an invalid value is set.
//  For example you may want to log help about specific properties.
//  For example you may want to search properties by key and log help.

//Maybe split into 'ValueContext' and a 'HelpContext' 
class PropertyConfigurationHelpContext(private val propertyConfiguration: PropertyConfiguration) {

    fun printCurrentValues(logger: Consumer<String>, knownProperties: List<Property>, additionalNotes: Map<String, String>) {
        logger.accept("")
        logger.accept("Current property values:")
        logger.accept("--property = value [notes]")
        logger.accept(StringUtils.repeat("-", 60))

        val sortedProperties = knownProperties.sortedBy { property -> property.key }

        for (property in sortedProperties) {
            if (!propertyConfiguration.wasKeyProvided(property.key)) {
                continue
            }

            val value: String = when (property) {
                is ValuedProperty<*> -> propertyConfiguration.getValueOrDefault(property).toString()
                is NullableProperty<*> -> propertyConfiguration.getValueOrEmpty(property).map { it.toString() }.orElse(null)
                else -> propertyConfiguration.getRaw(property).orElse(null)
            }

            val containsPassword = property.key.toLowerCase().contains("password") || property.key.toLowerCase().contains("api.token") || property.key.toLowerCase().contains("access.token")
            val maskedValue = if (containsPassword) {
                StringUtils.repeat('*', value?.length ?: 0)
            } else {
                value ?: ""
            }

            val sourceName = propertyConfiguration.getPropertySource(property) ?: "unknown"
            val sourceDisplayName = Companion.sourceDisplayNames.getOrDefault(sourceName, sourceName)

            val notes = additionalNotes[property.key] ?: ""

            logger.accept(property.key + " = " + maskedValue + " [" + sourceDisplayName + "] " + notes)
        }

        logger.accept(StringUtils.repeat("-", 60))
        logger.accept("")
    }

    fun printPropertyErrors(logger: Consumer<String>, knownProperties: List<Property>, errors: Map<String, List<String>>) {
        val sortedProperties = knownProperties.sortedBy { property -> property.key }

        sortedProperties
                .filter { errors.containsKey(it.key) }
                .forEach { property ->
                    errors[property.key]?.let { errorMessages ->
                        logger.accept(StringUtils.repeat("=", 60))
                        val header = when (val size = errorMessages.size) {
                            1 -> "ERROR (1)"
                            else -> "ERRORS ($size)"
                        }
                        logger.accept(header)
                        errorMessages.forEach { errorMessage -> logger.accept(property.key + ": " + errorMessage) }
                    }
                }
    }

    fun findPropertyParseErrors(knownProperties: List<Property>): Map<String, List<String>> {
        val exceptions = mutableMapOf<String, List<String>>()
        val sortedProperties = knownProperties.sortedBy { property -> property.key }
        for (property in sortedProperties) {
            if (property is TypedProperty<*>) {
                val exception = propertyConfiguration.getPropertyException(property);
                if (exception.isPresent) {
                    exceptions[property.key] = listOf(exception.get().message ?: exception.toString())
                }
            }
        }
        return exceptions;
    }

    companion object {
        val sourceDisplayNames = mapOf(
                "configurationProperties" to "cfg",
                "systemEnvironment" to "env",
                "commandLineArgs" to "cmd",
                "systemProperties" to "jvm"
        )
    }
}