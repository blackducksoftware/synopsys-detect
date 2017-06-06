package com.blackducksoftware.integration.hub.packman.bomtool

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.packman.type.BomToolType

class DockerBomTool extends BomTool {
    @Override
    public BomToolType getBomToolType() {
        BomToolType.DOCKER
    }

    @Override
    public boolean isBomToolApplicable() {
        return false;
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        return null;
    }
}
