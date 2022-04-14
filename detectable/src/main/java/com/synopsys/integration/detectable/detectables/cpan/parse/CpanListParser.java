package com.synopsys.integration.detectable.detectables.cpan.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class CpanListParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;

    public CpanListParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    //TODO: In a transformer
    public DependencyGraph parse(List<String> cpanListText, List<String> directDependenciesText) {
        Map<String, String> nameVersionMap = createNameVersionMap(cpanListText);
        List<String> directModuleNames = getDirectModuleNames(directDependenciesText); //Lazy builder.

        DependencyGraph graph = new BasicDependencyGraph();
        for (String moduleName : directModuleNames) {
            String version = nameVersionMap.get(moduleName);
            if (null != version) {
                String name = moduleName.replace("::", "-");
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.CPAN, name, version);
                Dependency dependency = new Dependency(name, version, externalId);
                graph.addChildToRoot(dependency);
            } else {
                logger.warn(String.format("Could node find resolved version for module: %s", moduleName));
            }
        }

        return graph;
    }

    //TODO: New parser
    public Map<String, String> createNameVersionMap(List<String> listText) {
        Map<String, String> nameVersionMap = new HashMap<>();

        for (String line : listText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (StringUtils.countMatches(line, "\t") != 1 || line.trim().contains(" ")) {
                continue;
            }

            try {
                String[] module = line.trim().split("\t");
                String name = module[0].trim();
                String version = module[1].trim();
                nameVersionMap.put(name, version); //TODO: Potential collision point here.
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                logger.debug(String.format("Failed to handle the following line:%s", line));
            }
        }

        return nameVersionMap;
    }

    // TODO: New parser
    public List<String> getDirectModuleNames(List<String> directDependenciesText) {
        List<String> modules = new ArrayList<>();
        for (String line : directDependenciesText) {
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
