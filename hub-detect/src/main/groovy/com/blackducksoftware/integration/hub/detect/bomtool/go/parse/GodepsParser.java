package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.google.gson.Gson;

public class GodepsParser {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;
    private static final String UNCORRECTED_VERSION_REGEX = "-\\d+-g[0-9a-f]+$";

    public GodepsParser(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
        this.gson = gson;
    }

    public DependencyGraph extractProjectDependencies(final String godepContents) {
        final GodepsFile godepsFile = gson.fromJson(godepContents, GodepsFile.class);
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        godepsFile.getDeps().stream()
                .map(godepDependency -> correctVersion(godepDependency))
                .map(godepDependency -> constructDependency(godepDependency))
                .forEach(dependency -> graph.addChildrenToRoot(dependency));

        return graph;
    }

    private GodepDependency correctVersion(final GodepDependency godepDependency) {
        String version = "";

        if (StringUtils.isNotBlank(godepDependency.getComment())) {
            version = godepDependency.getComment().trim();
            // TODO test with kubernetes

            // https://github.com/blackducksoftware/hub-detect/issues/237
            // updating according to 'git describe'
            if (version.matches(".*" + UNCORRECTED_VERSION_REGEX)) {
                // v1.0.0-10-gae3452 should be changed to v1.0.0
                version = version.replaceAll(UNCORRECTED_VERSION_REGEX, "");
            }
        } else {
            version = godepDependency.getRev().trim();
        }

        godepDependency.setRev(version);
        return godepDependency;
    }

    private Dependency constructDependency(final GodepDependency godepDependency) {
        final ExternalId dependencyExternalId = externalIdFactory.createNameVersionExternalId(Forge.GOLANG, godepDependency.getImportPath(), godepDependency.getRev());
        return new Dependency(godepDependency.getImportPath(), godepDependency.getRev(), dependencyExternalId);
    }

}
