package com.blackducksoftware.integration.hub.packman.parser.model;

import java.util.List;

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode;

public interface Packager {
    public List<DependencyNode> makeDependencyNodes();
}
