package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;

public class VndrParser {
    public ExternalIdFactory externalIdFactory;

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
