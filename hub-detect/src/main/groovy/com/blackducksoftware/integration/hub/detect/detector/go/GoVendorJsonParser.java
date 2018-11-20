package com.blackducksoftware.integration.hub.detect.detector.go;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.hub.bdio.model.Forge;
import com.synopsys.integration.hub.bdio.model.dependency.Dependency;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalId;
import com.synopsys.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class GoVendorJsonParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ExternalIdFactory externalIdFactory;

    public GoVendorJsonParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseVendorJson(final Gson gson, final String vendorJsonContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        GoVendorJsonData vendorJsonData = gson.fromJson(vendorJsonContents, GoVendorJsonData.class);
        logger.info(String.format("vendorJsonData: %s", vendorJsonData));
        for (GoVendorJsonPackageData pkg : vendorJsonData.getPackages()) {
            final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, pkg.getPath(), pkg.getRevision());
            final Dependency dependency = new Dependency(pkg.getPath(), pkg.getRevision(), dependencyExternalId);
            logger.info(String.format("dependency: %s", dependency.externalId.toString()));
            graph.addChildToRoot(dependency);
        }
        return graph;
    }
}
