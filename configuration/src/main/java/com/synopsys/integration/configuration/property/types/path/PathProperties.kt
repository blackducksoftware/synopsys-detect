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
package com.synopsys.integration.configuration.property.types.path

import com.synopsys.integration.configuration.property.base.NullableProperty
import com.synopsys.integration.configuration.property.base.ValuedListProperty
import com.synopsys.integration.configuration.property.base.ValuedProperty

const val TYPE_DESCRIPTION = "Path"

class NullablePathProperty(key: String) : NullableProperty<PathValue>(key, PathValueParser()) {
    override fun listExampleValues(): List<String>? = listOf("/Users/Synopsys/my-project", "Unix Only: ~/my-project")
    override fun describeType(): String? = TYPE_DESCRIPTION
}

class PathProperty(key: String, default: PathValue) : ValuedProperty<PathValue>(key, PathValueParser(), default) {
    override fun listExampleValues(): List<String>? = listOf("/Users/Synopsys/my-project", "Unix Only: ~/my-project")
    override fun describeDefault(): String? = default.toString()
    override fun describeType(): String? = TYPE_DESCRIPTION
}

class PathListProperty(key: String, default: List<PathValue>) : ValuedListProperty<PathValue>(key, PathListValueParser(), default) {
    override fun describeType(): String? = TYPE_DESCRIPTION
}