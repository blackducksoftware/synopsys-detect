/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.cpan.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class CpanListParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public CpanListParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parse(final List<String> cpanListText, final List<String> directDependenciesText) {
        final Map<String, String> nameVersionMap = createNameVersionMap(cpanListText);
        final List<String> directModuleNames = getDirectModuleNames(directDependenciesText);

        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        for (final String moduleName : directModuleNames) {
            final String version = nameVersionMap.get(moduleName);
            if (null != version) {
                final String name = moduleName.replace("::", "-");
                final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.CPAN, name, version);
                final Dependency dependency = new Dependency(name, version, externalId);
                graph.addChildToRoot(dependency);
            } else {
                logger.warn(String.format("Could node find resolved version for module: %s", moduleName));
            }
        }

        return graph;
    }

    public Map<String, String> createNameVersionMap(final List<String> listText) {
        final Map<String, String> nameVersionMap = new HashMap<>();

        for (final String line : listText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (StringUtils.countMatches(line, "\t") != 1 || line.trim().contains(" ")) {
                continue;
            }

            try {
                final String[] module = line.trim().split("\t");
                final String name = module[0].trim();
                final String version = module[1].trim();
                nameVersionMap.put(name, version);
            } catch (final IndexOutOfBoundsException indexOutOfBoundsException) {
                logger.debug(String.format("Failed to handle the following line:%s", line));
            }
        }

        return nameVersionMap;
    }

    public List<String> getDirectModuleNames(final List<String> directDependenciesText) {
        final List<String> modules = new ArrayList<>();
        for (final String line : directDependenciesText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            if (line.contains("-->") || (line.contains(" ... ") && line.contains("Configuring"))) {
                continue;
            }
            modules.add(line.split("~")[0].trim());
        }

        return modules;
    }

}
