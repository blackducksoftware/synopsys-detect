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
package com.synopsys.integration.configuration.property

/**
 * This is the most basic property.
 * It has no type information and a value cannot be retrieved for it (without a subclass).
 **/
abstract class Property(val key: String) {
    var name: String? = null
    var fromVersion: String? = null
    var propertyHelpInfo: PropertyHelpInfo? = null
    var propertyGroupInfo: PropertyGroupInfo? = null
    var category: Category? = null
    var propertyDeprecationInfo: PropertyDeprecationInfo? = null

    fun info(name: String, fromVersion: String): Property {
        this.name = name
        this.fromVersion = fromVersion
        return this
    }

    fun help(short: String, long: String? = null): Property {
        this.propertyHelpInfo = PropertyHelpInfo(short, long)
        return this
    }

    fun groups(primaryGroup: Group, vararg additionalGroups: Group): Property {
        this.propertyGroupInfo = PropertyGroupInfo(primaryGroup, additionalGroups.toList())
        return this
    }

    fun category(category: Category): Property {
        this.category = category
        return this
    }

    fun deprecated(description: String, failInVersion: ProductMajorVersion, removeInVersion: ProductMajorVersion): Property {
        this.propertyDeprecationInfo = PropertyDeprecationInfo(description, failInVersion, removeInVersion)
        return this
    }

    open fun isCaseSensitive(): Boolean = false
    open fun isOnlyExampleValues(): Boolean = false
    open fun listExampleValues(): List<String>? = emptyList()
    open fun describeType(): String? = null
    open fun describeDefault(): String? = null
    open fun isCommaSeparated(): Boolean = false
}

data class PropertyHelpInfo(val short: String, val long: String?)
data class PropertyGroupInfo(val primaryGroup: Group, val additionalGroups: List<Group>)
data class PropertyDeprecationInfo(val description: String, val failInVersion: ProductMajorVersion, val removeInVersion: ProductMajorVersion) {
    fun getDeprecationText(): String = "$description It will cause failure in ${failInVersion.getDisplayValue()} and be removed in ${removeInVersion.getDisplayValue()}.";
}

