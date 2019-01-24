package com.blackducksoftware.integration.hub.detect.detector.maven;

import org.apache.commons.lang3.StringUtils;
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
            logger.warn(String.format("The scope for component %s:%s:%s is missing, which might produce inaccurate results", externalId.group, externalId.name, externalId.version));
            this.scope = "";
        } else {
            this.scope = scope;
        }
    }

    public boolean isInScope(final String targetScope) {
        if (StringUtils.isBlank(targetScope)) {
            return true;
        }
        if (scope.equals(targetScope)) {
            return true;
        }
        return false;
    }
}
