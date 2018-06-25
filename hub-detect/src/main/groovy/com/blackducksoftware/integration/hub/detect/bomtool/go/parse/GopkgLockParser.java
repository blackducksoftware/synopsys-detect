package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.moandjiezana.toml.Toml;

public class GopkgLockParser {
    public ExternalIdFactory externalIdFactory;

    public GopkgLockParser(final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph parseDepLock(final String depLockContents) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();
        final GopkgLock gopkgLock = new Toml().read(depLockContents).to(GopkgLock.class);

        for (final Project project : gopkgLock.getProjects()) {
            if (project != null) {
                final String name = project.getName();
                String version = "";
                if (StringUtils.isNotBlank(project.getVersion())) {
                    version = project.getVersion();
                } else {
                    version = project.getRevision();
                }
                for (final String pkg : project.getPackages()) {
                    String packageName = name;
                    if (!pkg.equals(".")) {
                        packageName = packageName + "/${pack}";
                    }
                    if (packageName.startsWith("golang.org/x/")) {
                        packageName = packageName.replaceAll("golang.org/x/", "");
                    }
                    final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, packageName, version);
                    final Dependency dependency = new Dependency(packageName, version, dependencyExternalId);
                    graph.addChildToRoot(dependency);
                }
            }
        }

        return graph;
    }
}
