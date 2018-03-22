package com.blackducksoftware.integration.hub.detect.bomtool.cpan;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CpanPackager {
    private final Logger logger = LoggerFactory.getLogger(CpanPackager.class);

    @Autowired
    private CpanListParser cpanListParser;

    @Autowired
    private NameVersionNodeTransformer nameVersionNodeTransformer;

    public DependencyGraph makeDependencyGraph(List<String> cpanListText, List<String> directDependenciesText) {
        Map<String, NameVersionNode> allModules = cpanListParser.parse(cpanListText);
        List<String> directModuleNames = getDirectModuleNames(directDependenciesText);

        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        for (String moduleName : directModuleNames) {
            NameVersionNode nameVersionNode = allModules.get(moduleName);
            if (null != nameVersionNode) {
                nameVersionNode.setName(nameVersionNode.getName().replace("::", "-"));
                Dependency module = nameVersionNodeTransformer.addNameVersionNodeToDependencyGraph(graph, Forge.CPAN, nameVersionNode);
                graph.addChildToRoot(module);
            } else {
                logger.warn(String.format("Could node find resolved version for module: %s",moduleName));
            }
        }

        return graph;
    }

    private List<String> getDirectModuleNames(List<String> directDependenciesText) {
        List<String> modules = new ArrayList<>();
        for (String line : directDependenciesText) {
            if(StringUtils.isBlank(line)) {
                continue;
            }
            if (line.contains("-->") || ((line.contains(" ... ") && line.contains("Configuring")))) {
                continue;
            }
            modules.add(line.split("~")[0].trim());
        }

       return  modules;
    }

}
