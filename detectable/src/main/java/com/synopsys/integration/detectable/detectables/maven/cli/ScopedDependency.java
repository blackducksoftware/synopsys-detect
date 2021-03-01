/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
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
