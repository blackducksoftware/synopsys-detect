/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.vendr.parse;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class VndrParser {
    private final ExternalIdFactory externalIdFactory;

    public VndrParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseVendorConf(final List<String> vendorConfContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        // TODO test against moby
        vendorConfContents.forEach(line -> {
            if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
                final String[] parts = line.split(" ");

                final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, parts[0], parts[1]);
                final Dependency dependency = new Dependency(parts[0], parts[1], dependencyExternalId);
                graph.addChildToRoot(dependency);
            }
        });

        return graph;
    }

}
