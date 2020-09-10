package com.synopsys.integration.detectable.detectables.gradle.inspection.parse;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleConfiguration;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleGav;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleReport;
import com.synopsys.integration.detectable.detectables.gradle.inspection.model.GradleTreeNode;

public class GradleReplacementDiscoverer {
    private final ExternalIdFactory externalIdFactory;

    public GradleReplacementDiscoverer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public void populateFromReport(DependencyReplacementResolver dependencyReplacementResolver, GradleReport gradleReport) {
        for (GradleConfiguration configuration : gradleReport.getConfigurations()) {
            populateFromTreeNodes(dependencyReplacementResolver, configuration.getChildren());
        }
    }

    private void populateFromTreeNodes(DependencyReplacementResolver dependencyReplacementResolver, List<GradleTreeNode> gradleTreeNodes) {
        for (GradleTreeNode currentNode : gradleTreeNodes) {
            Optional<GradleGav> resolvedGavOptional = currentNode.getGav();
            Optional<GradleGav> replacedGavOptional = currentNode.getReplacedGav();
            if (currentNode.getNodeType() != GradleTreeNode.NodeType.GAV || !resolvedGavOptional.isPresent() || !replacedGavOptional.isPresent()) {
                continue;
            }
            GradleGav resolvedGav = resolvedGavOptional.get();
            GradleGav replacedGav = replacedGavOptional.get();

            ExternalId externalId = externalIdFactory.createMavenExternalId(resolvedGav.getName(), resolvedGav.getArtifact(), resolvedGav.getVersion());
            Dependency resolvedDependency = new Dependency(resolvedGav.getArtifact(), resolvedGav.getVersion(), externalId);

            ExternalId replacedExternalId = externalIdFactory.createMavenExternalId(replacedGav.getName(), replacedGav.getArtifact(), replacedGav.getVersion());
            Dependency replacedDependency = new Dependency(replacedGav.getArtifact(), replacedGav.getVersion(), replacedExternalId);

            dependencyReplacementResolver.addReplacementData(replacedDependency, resolvedDependency);
        }
    }
}
