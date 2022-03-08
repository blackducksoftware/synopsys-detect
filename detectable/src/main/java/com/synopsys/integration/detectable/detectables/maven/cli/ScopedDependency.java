package com.synopsys.integration.detectable.detectables.maven.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class ScopedDependency extends Dependency {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public final String scope;

    public ScopedDependency(String name, String version, ExternalId externalId, String scope) {
        super(name, version, externalId);
        if (scope == null) {
            logger.warn(String.format(
                "The scope for component %s:%s:%s is missing, which might produce inaccurate results",
                externalId.getGroup(),
                externalId.getName(),
                externalId.getVersion()
            ));
            this.scope = "";
        } else {
            this.scope = scope;
        }
    }
}
