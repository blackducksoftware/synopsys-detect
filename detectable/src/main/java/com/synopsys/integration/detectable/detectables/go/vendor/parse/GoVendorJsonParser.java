/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.vendor.parse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.vendor.model.PackageData;
import com.synopsys.integration.detectable.detectables.go.vendor.model.VendorJson;

public class GoVendorJsonParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public GoVendorJsonParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseVendorJson(final Gson gson, final String vendorJsonContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final VendorJson vendorJsonData = gson.fromJson(vendorJsonContents, VendorJson.class);
        logger.trace(String.format("vendorJsonData: %s", vendorJsonData));
        for (final PackageData pkg : vendorJsonData.getPackages()) {
            if (StringUtils.isNotBlank(pkg.getPath()) && StringUtils.isNotBlank(pkg.getRevision())) {
                final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, pkg.getPath(), pkg.getRevision());
                final Dependency dependency = new Dependency(pkg.getPath(), pkg.getRevision(), dependencyExternalId);
                logger.trace(String.format("dependency: %s", dependency.getExternalId().toString()));
                graph.addChildToRoot(dependency);
            } else {
                logger.debug(String.format("Omitting package path:'%s', revision:'%s' (one or both of path, revision is/are missing)", pkg.getPath(), pkg.getRevision()));
            }
        }
        return graph;
    }
}
