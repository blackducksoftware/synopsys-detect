package com.synopsys.integration.detectable.detectables.cran.parse;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.graph.builder.LazyId;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PackratLockFileParser {
    private static final String PACKAGE_TOKEN = "Package";
    private static final String VERSION_TOKEN = "Version";
    private static final String REQUIRES_TOKEN = "Requires";
    private static final String INDENTATION_TOKEN = "    ";

    private final ExternalIdFactory externalIdFactory;

    public PackratLockFileParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseProjectDependencies(List<String> packratLockContents) throws MissingExternalIdException {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        LazyId currentParent = null;
        String name = null;
        boolean requiresSection = false;

        for (String line : packratLockContents) {
            if (StringUtils.isBlank(line)) {
                currentParent = null;
                name = null;
                requiresSection = false;
                continue;
            }

            if (!(line.startsWith(PACKAGE_TOKEN) || line.startsWith(VERSION_TOKEN) || line.startsWith(REQUIRES_TOKEN) || line.startsWith(INDENTATION_TOKEN))) {
                continue;
            }

            if (line.startsWith(PACKAGE_TOKEN)) {
                name = getValueFromLine(line);
                currentParent = LazyId.fromName(name);
                graphBuilder.setDependencyName(currentParent, name);
                graphBuilder.addChildToRoot(currentParent);
                requiresSection = false;
            } else if (line.startsWith(VERSION_TOKEN)) {
                String version = getValueFromLine(line);
                graphBuilder.setDependencyVersion(currentParent, version);
                LazyId realId = LazyId.fromNameAndVersion(name, version);
                ExternalId externalId = this.externalIdFactory.createNameVersionExternalId(Forge.CRAN, name, version);
                graphBuilder.setDependencyAsAlias(realId, currentParent);
                graphBuilder.setDependencyInfo(realId, name, version, externalId);
                currentParent = realId;
            } else if (line.startsWith(REQUIRES_TOKEN)) {
                requiresSection = true;

                String cleanLine = getValueFromLine(line);
                List<LazyId> children = getChildrenNames(cleanLine).stream()
                    .map(LazyId::fromName)
                    .collect(Collectors.toList());

                graphBuilder.addParentWithChildren(currentParent, children);
            } else if (requiresSection && line.startsWith(INDENTATION_TOKEN)) {
                List<LazyId> children = getChildrenNames(line).stream()
                    .map(LazyId::fromName)
                    .collect(Collectors.toList());

                graphBuilder.addParentWithChildren(currentParent, children);
            }
        }

        return graphBuilder.build();
    }

    private String getValueFromLine(String line) {
        int separatorIndex = line.indexOf(':');

        return line.substring(separatorIndex + 1).trim();
    }

    private List<String> getChildrenNames(String line) {
        String[] parts = line.split(",");

        return Arrays.stream(parts)
            .map(String::trim)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
    }
}
