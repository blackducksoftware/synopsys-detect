package com.synopsys.integration.detectable.detectables.conan.cli;

import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;

public class ConanInfoParser {

    public ConanParseResult generateCodeLocation(String conanInfoOutput) {
        // TODO eventually should use ExternalIdFactory; doubt it can handle these IDs
        //ExternalIdFactory f;
        List<Dependency> dependencies = new ArrayList<>();
        ExternalId externalId = new ExternalId(new Forge("/", "conan"));
        externalId.setName("tbdpkg");
        externalId.setVersion("1.0@user/channel#rrev:pkgid#pkgrev");
        Dependency dep = new Dependency("tbdpkg", "tbdpkgversion", externalId);
        dependencies.add(dep);
        MutableMapDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        return new ConanParseResult("tbdproject", "tbdprojectversion", codeLocation);
    }
}
