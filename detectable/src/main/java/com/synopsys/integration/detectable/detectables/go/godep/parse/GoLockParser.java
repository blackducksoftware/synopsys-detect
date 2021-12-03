package com.synopsys.integration.detectable.detectables.go.godep.parse;

import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.moandjiezana.toml.Toml;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.go.godep.model.GoLock;
import com.synopsys.integration.detectable.detectables.go.godep.model.Project;

public class GoLockParser {
    private final ExternalIdFactory externalIdFactory;

    public GoLockParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseDepLock(InputStream depLockInputStream) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        GoLock goLock = new Toml().read(depLockInputStream).to(GoLock.class);
        if (goLock.projects != null) {
            for (Project project : goLock.projects) {
                if (project != null) {
                    String projectName = project.getName();
                    String projectVersion = Optional.ofNullable(StringUtils.stripToNull(project.getVersion())).orElse(project.getRevision());
                    project.getPackages().stream()
                        .map(packageName -> createDependencyName(projectName, packageName))
                        .map(dependencyName -> createGoDependency(dependencyName, projectVersion))
                        .forEach(graph::addChildToRoot);
                }
            }
        }
        return graph;
    }

    private String createDependencyName(String projectName, String parsedPackageName) {
        String dependencyName = projectName;

        if (!".".equals(parsedPackageName)) {
            dependencyName = dependencyName + "/" + parsedPackageName;
        }
        if (dependencyName.startsWith("golang.org/x/")) {
            dependencyName = dependencyName.replace("golang.org/x/", "");
        }

        return dependencyName;
    }

    private Dependency createGoDependency(String name, String version) {
        ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, name, version);
        return new Dependency(name, version, dependencyExternalId);
    }

}
