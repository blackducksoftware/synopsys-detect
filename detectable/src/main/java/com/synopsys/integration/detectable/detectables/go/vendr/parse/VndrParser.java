package com.synopsys.integration.detectable.detectables.go.vendr.parse;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class VndrParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public VndrParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseVendorConf(List<String> vendorConfContents) {
        DependencyGraph graph = new BasicDependencyGraph();

        vendorConfContents.stream()
            .filter(this::shouldIncludeLine)
            .map(this::parseLineToDependency)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(graph::addChildToRoot);

        return graph;
    }

    private Optional<Dependency> parseLineToDependency(String line) {
        // Using StringUtils:split can handle multiple whitespace characters. See IDETECT-2722.
        // Using null as the separator will make StringUtils check for any whitespace. IE tabs/multiple-spaces
        String[] parts = StringUtils.split(line, null);
        if (parts.length < 2) {
            logger.error("Failed to parse vendor.conf line. Excluding from BOM. Line: {}", line);
            return Optional.empty();
        }
        ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, parts[0], parts[1]);
        Dependency dependency = new Dependency(parts[0], parts[1], dependencyExternalId);
        return Optional.of(dependency);
    }

    private boolean shouldIncludeLine(String line) {
        return StringUtils.isNotBlank(line) && !line.startsWith("#");
    }

}
