/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.maven.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class ScopedDependency extends Dependency {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final String scope;

    public ScopedDependency(final String name, final String version, final ExternalId externalId, final String scope) {
        super(name, version, externalId);
        if (scope == null) {
            logger.warn(String.format("The scope for component %s:%s:%s is missing, which might produce inaccurate results", externalId.getGroup(), externalId.getName(), externalId.getVersion()));
            this.scope = "";
        } else {
            this.scope = scope;
        }
    }
}
